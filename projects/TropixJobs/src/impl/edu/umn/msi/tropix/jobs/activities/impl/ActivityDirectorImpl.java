/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.common.shutdown.ShutdownAware;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.ActivityDirector;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityStatus;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.cancel.JobCanceller;

// TODO: Maybe add state RUNNING_AND_CANCELLELD
@ManagedBean
class ActivityDirectorImpl implements ActivityDirector, JobCanceller, ShutdownAware {
  private static final Log LOG = LogFactory.getLog(ActivityDirectorImpl.class);
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private AtomicBoolean shutdown = new AtomicBoolean(false);
  private ConcurrentMap<String, WorkflowTacker> workflowTrackers = new MapMaker().makeMap();
  private ActivityExecutor activityExecutor;
  private Executor executor;
  private ActivityService activityService;
  private WorkflowListener workflowListener;

  private static String appendPrefixToProperty(final String prefix, final String property) {
    return prefix + property.substring(0, 1).toUpperCase() + property.substring(1);
  }

  class WorkflowTacker {
    private final ImmutableMap<String, ActivityState> activityStates;
    private final ImmutableMultimap<ActivityDescription, ActivityDescription> dependents;
    private final ImmutableMap<String, JobState> jobStates;
    private final ActivityContext activityContext;

    class JobState {
      private ImmutableList<ActivityState> activityStates;
      private JobDescription jobDescription;

      JobState(final JobDescription jobDescription, final ImmutableList<ActivityState> activityStates) {
        this.activityStates = activityStates;
        this.jobDescription = jobDescription;
      }

    }

    private void updateState() {
      activityService.save(activityContext);
    }

    class ActivityState {
      private final ActivityDescription activityDescription;

      ActivityState(final ActivityDescription activityDescription) {
        this.activityDescription = activityDescription;
      }

      boolean ready() {
        final ActivityStatus activityStatus = activityDescription.getActivityStatus();
        if(activityStatus != ActivityStatus.WAITING) {
          return false;
        }
        boolean ready = true;
        for(final ActivityDependency activityDependency : activityDescription.getDependencies()) {
          final String dependencyId = activityDependency.getActivityDescription().getId();
          final ActivityState dependentActivityState = activityStates.get(dependencyId);
          Preconditions.checkNotNull(dependentActivityState);
          final ActivityDescription dependentActivityDescription = dependentActivityState.activityDescription;
          Preconditions.checkNotNull(dependentActivityDescription);
          if(dependentActivityDescription.getActivityStatus() != ActivityStatus.COMPLETE) {
            ready = false;
            break;
          }
        }
        return ready;
      }
    }

    void launch() {
      if(shutdown.get()) {
        return;
      }
      executor.execute(new Runnable() {
        public void run() {
          final ImmutableList<ActivityDescription> readyDescriptions = getReadyDescriptons();
          for(final ActivityDescription activityDescription : readyDescriptions) {
            executor.execute(new ActivityRunnable(activityDescription, activityContext, WorkflowTacker.this));
          }
        }
      });
    }

    synchronized ImmutableList<ActivityDescription> getReadyDescriptons() {
      final ImmutableList.Builder<ActivityDescription> builder = ImmutableList.builder();
      for(final ActivityState activityState : activityStates.values()) {
        if(activityState.ready()) {
          builder.add(activityState.activityDescription);
          activityState.activityDescription.setActivityStatus(ActivityStatus.RUNNING);
        }
      }
      return builder.build();
    }

    synchronized boolean checkComplete() {
      boolean complete = true;
      for(final ActivityState activityState : activityStates.values()) {
        if(activityState.activityDescription.getActivityStatus() == ActivityStatus.RUNNING
            || activityState.activityDescription.getActivityStatus() == ActivityStatus.WAITING) {
          complete = false;
        }
      }
      if(complete) {
        handleComplete();
      }
      return complete;
    }

    synchronized void fail(final String id) {
      final ActivityState failedActivityState = activityStates.get(id);
      failedActivityState.activityDescription.setActivityStatus(ActivityStatus.FAILED);
      final JobDescription jobDescription = failedActivityState.activityDescription.getJobDescription();
      if(jobDescription != null) {
        workflowListener.jobComplete(activityContext, jobDescription, false);
      }
      cleanup(failedActivityState);
      updateState();
      checkComplete();
    }

    void rollback(final ActivityDescription activityDescription) {
      try {
        activityExecutor.rollback(activityDescription, activityContext);
      } catch(final RuntimeException e) {
        ExceptionUtils.logQuietly(LOG, e, "Failed to activity description " + activityDescription + " with id " + activityDescription.getId()
            + " future rollbacks will likely fail and these activities should be manually removed.");
      }
    }

    private Iterable<ActivityDescription> getAllDependentDescriptions(final Iterable<ActivityDescription> activityDescriptions) {
      final Set<ActivityDescription> allDependentDescriptions = Sets.newHashSet();
      final Set<ActivityDescription> unexploredDescriptions = Sets.newHashSet(activityDescriptions);
      while(!unexploredDescriptions.isEmpty()) {
        final ActivityDescription dependentDescription = unexploredDescriptions.iterator().next();
        for(ActivityDescription subDepentdent : dependents.get(dependentDescription)) {
          if(!allDependentDescriptions.contains(subDepentdent)) {
            unexploredDescriptions.add(subDepentdent);
          }
        }
        allDependentDescriptions.add(dependentDescription);
        unexploredDescriptions.remove(dependentDescription);
      }
      return allDependentDescriptions;
    }

    synchronized void rollback() {
      final Set<ActivityDescription> descriptionsToRollback = Sets.newHashSet();

      // Produce a set of all of the descriptions to rollback.
      for(final ActivityState activityState : activityStates.values()) {
        if(activityState.activityDescription.getActivityStatus() == ActivityStatus.FAILED
            || activityState.activityDescription.getActivityStatus() == ActivityStatus.COMPLETE) {
          final ActivityDescription activityDescription = activityState.activityDescription;
          final JobDescription jobDescription = activityDescription.getJobDescription();
          if(jobDescription != null && !jobDescription.getComplete()) {
            descriptionsToRollback.add(activityDescription);
          }
        }
      }

      // Iteratively rollback descriptions that do not have dependencies on descriptions that
      // need to be rolled back.
      while(!descriptionsToRollback.isEmpty()) {

        // Maintain separate map to prevent concurrent modification exception.
        final Set<ActivityDescription> descriptionsToRollbackThisIteration = Sets.newHashSet();
        for(ActivityDescription description : descriptionsToRollback) {
          boolean canRollback = true;
          for(ActivityDependency dependency : description.getDependencies()) {
            if(descriptionsToRollback.contains(dependency.getActivityDescription())) {
              canRollback = false;
              break;
            }
          }

          if(canRollback) {
            descriptionsToRollbackThisIteration.add(description);
          }
        }
        for(ActivityDescription description : descriptionsToRollbackThisIteration) {
          rollback(description);
          descriptionsToRollback.remove(description);
        }
      }
    }

    private void handleComplete() {
      boolean needToRollback = false;
      for(ActivityState activityState : activityStates.values()) {
        if(activityState.activityDescription.getActivityStatus() != ActivityStatus.COMPLETE) {
          needToRollback = true;
        }
      }
      workflowListener.workflowComplete(activityContext, !needToRollback);
      if(needToRollback) {
        rollback();
      }
      workflowTrackers.remove(this);
    }

    private synchronized void cancelActivity(final ActivityState activityState) {
      LOG.debug("Cancelling activity " + activityState.activityDescription);
      if(activityState.activityDescription.getActivityStatus() == ActivityStatus.RUNNING) {
        activityExecutor.cancel(activityState.activityDescription, activityContext);
      } else if(activityState.activityDescription.getActivityStatus() == ActivityStatus.WAITING) {
        activityState.activityDescription.setActivityStatus(ActivityStatus.CANCELLED);
      }
      cleanup(activityState);
    }

    private void cleanup(final ActivityState activityState) {
      // Cancel descendents
      for(ActivityDescription activityDescription : getAllDependentDescriptions(Lists.newArrayList(activityState.activityDescription))) {
        final ActivityState dependentActivityState = activityStates.get(activityDescription.getId());
        if(dependentActivityState.activityDescription.getActivityStatus() == ActivityStatus.WAITING) {
          dependentActivityState.activityDescription.setActivityStatus(ActivityStatus.CANCELLED);
        }
        checkJobComplete(activityDescription.getJobDescription());
      }
    }

    private synchronized void cancel(final Iterable<ActivityState> activityStates) {
      for(final ActivityState activityState : activityStates) {
        cancelActivity(activityState);
      }
      updateState();
    }

    synchronized void cancel() {
      cancel(activityStates.values());
    }

    synchronized void checkJobComplete(@Nullable final JobDescription jobDescription) {
      if(jobDescription == null) {
        return;
      }
      boolean allComplete = true;
      boolean anyRunning = false;
      boolean anyCancelled = false;
      for(ActivityState jobActivityState : jobStates.get(jobDescription.getId()).activityStates) {
        final ActivityDescription jobActivityDescription = jobActivityState.activityDescription;
        final ActivityStatus status = jobActivityDescription.getActivityStatus();
        if(status != ActivityStatus.COMPLETE) {
          allComplete = false;
        }
        if(status == ActivityStatus.CANCELLED) {
          anyCancelled = true;
        }
        if(status == ActivityStatus.RUNNING) {
          anyRunning = true;
        }
      }
      if(allComplete) {
        jobDescription.setComplete(true);
        workflowListener.jobComplete(activityContext, jobDescription, true);
      }
      if(anyCancelled && !anyRunning) {
        workflowListener.jobComplete(activityContext, jobDescription, false);
      }
    }

    /*
     * TODO: make static.
     */
    private void handleDependency(final ActivityDependency dependency, final ActivityDescription fromActivityDescription,
        final ActivityDescription toActivityDescription) {
      final String producerProperty = dependency.getProducerProperty();
      if(producerProperty != null) {
        final Object rawProducerValue = REFLECTION_HELPER.getBeanProperty(fromActivityDescription, producerProperty);
        Object producerValue = null;
        if(ActivityDependency.specifiesProducesIndex(dependency)) {
          final int index = dependency.getIndex();
          @SuppressWarnings("unchecked")
          final Iterable<Object> rawProducerValueAsIterable = (Iterable<Object>) rawProducerValue;
          producerValue = Iterables.get(rawProducerValueAsIterable, index);
        } else if(ActivityDependency.specifiesProducesKey(dependency)) {
          final String key = dependency.getKey();
          @SuppressWarnings("unchecked")
          final Map<String, Object> rawProducerValueAsMap = (Map<String, Object>) rawProducerValue;
          producerValue = rawProducerValueAsMap.get(key);
        } else {
          producerValue = rawProducerValue;
        }
        final String setMethodName = appendPrefixToProperty("set", dependency.getConsumerProperty());
        boolean hasSetter = REFLECTION_HELPER.hasMethod(toActivityDescription.getClass(), setMethodName);

        final String consumerProperty = dependency.getConsumerProperty();
        final boolean specifiesConsumesIndex = ActivityDependency.specifiesConsumesIndex(dependency);
        if(specifiesConsumesIndex) {
          final String addMethodName = appendPrefixToProperty("set", consumerProperty);
          REFLECTION_HELPER.invoke(addMethodName, toActivityDescription, producerValue, dependency.getConsumesIndex());
        } else if(hasSetter) {
          REFLECTION_HELPER.setBeanProperty(toActivityDescription, consumerProperty, producerValue);
        } else {
          final String addMethodName = appendPrefixToProperty("add", consumerProperty);
          REFLECTION_HELPER.invoke(addMethodName, toActivityDescription, producerValue);
        }
      }
    }

    synchronized void complete(final String id) {
      final ActivityState completedActivityState = activityStates.get(id);
      final ActivityDescription completedActivityDescription = completedActivityState.activityDescription;
      completedActivityState.activityDescription.setActivityStatus(ActivityStatus.COMPLETE);
      checkJobComplete(completedActivityDescription.getJobDescription());
      for(ActivityDescription dependent : dependents.get(completedActivityDescription)) {
        for(final ActivityDependency dependency : dependent.getDependencies()) {
          if(dependency.getActivityDescription().equals(completedActivityState.activityDescription)) {
            handleDependency(dependency, completedActivityDescription, dependent);
          }
        }
      }
      updateState();
      if(!checkComplete()) {
        launch();
      }
    }

    WorkflowTacker(final ActivityContext activityContext) {
      final ImmutableMap.Builder<String, ActivityState> activityStatesBuilder = ImmutableMap.builder();
      final Multimap<JobDescription, ActivityState> jobDescriptionMultimap = HashMultimap.create();
      final ImmutableMultimap.Builder<ActivityDescription, ActivityDescription> dependentsBuilder = ImmutableMultimap.builder();
      for(final ActivityDescription activityDescription : activityContext.getActivityDescriptions()) {
        for(final ActivityDependency activityDependency : activityDescription.getDependencies()) {
          final ActivityDescription dependencyDescription = activityDependency.getActivityDescription();
          dependentsBuilder.put(dependencyDescription, activityDescription);
        }
        final ActivityState state = new ActivityState(activityDescription);
        if(activityDescription.getActivityStatus() == ActivityStatus.RUNNING) { // Was running, but we need to reset
          activityDescription.setActivityStatus(ActivityStatus.WAITING);
        }
        activityStatesBuilder.put(activityDescription.getId(), state);
        final JobDescription jobDescription = activityDescription.getJobDescription();
        if(jobDescription != null) {
          jobDescriptionMultimap.put(jobDescription, state);
        }
      }
      final ImmutableMap.Builder<String, JobState> jobStatesBuilder = ImmutableMap.builder();
      for(Map.Entry<JobDescription, Collection<ActivityState>> entry : jobDescriptionMultimap.asMap().entrySet()) {
        final JobState jobState = new JobState(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
        jobStatesBuilder.put(entry.getKey().getId(), jobState);
      }
      this.activityContext = activityContext;
      dependents = dependentsBuilder.build();
      activityStates = activityStatesBuilder.build();
      jobStates = jobStatesBuilder.build();
    }

    private synchronized void cancelJob(final String jobId) {
      final JobState jobState = jobStates.get(jobId);
      cancel(jobState.activityStates);

    }

  }

  private class ActivityRunnable implements Runnable {
    private final ActivityDescription activityDescription;
    private final ActivityContext activityContext;
    private final WorkflowTacker activityDescriptionTracker;

    ActivityRunnable(final ActivityDescription description, final ActivityContext activityContext, final WorkflowTacker activityDescriptionTracker) {
      this.activityDescription = description;
      this.activityContext = activityContext;
      this.activityDescriptionTracker = activityDescriptionTracker;
    }

    public void run() {
      workflowListener.activityStarted(activityContext, activityDescription);
      RuntimeException exception = null;
      try {
        activityExecutor.run(activityDescription, activityContext);
      } catch(final ShutdownException se) {
        throw se;
      } catch(final RuntimeException e) {
        exception = e;
      }
      workflowListener.activityComplete(activityContext, activityDescription, exception == null);
      final String id = activityDescription.getId();
      if(exception == null) {
        activityDescriptionTracker.complete(id);
      } else {
        ExceptionUtils.logQuietly(LOG, exception);
        activityDescriptionTracker.fail(id);
      }
    }

  }

  public void execute(final ActivityContext activityContext) {
    activityService.save(activityContext);
    final WorkflowTacker tracker = new WorkflowTacker(activityContext);
    workflowTrackers.put(activityContext.getId(), tracker);
    tracker.launch();
  }

  @PostConstruct
  public void init() {
    final Iterable<ActivityContext> contexts = activityService.loadActivityContexts();
    for(ActivityContext context : contexts) {
      final WorkflowTacker tracker = new WorkflowTacker(context);
      workflowTrackers.put(context.getId(), tracker);
      tracker.launch();
    }
  }

  @Inject
  void setActivityExecutor(final ActivityExecutor activityExecutor) {
    this.activityExecutor = activityExecutor;
  }

  @Inject
  void setExecutor(@Named("daemonExecutor") final Executor executor) {
    this.executor = executor;
  }

  @Inject
  void setActivityService(final ActivityService activityService) {
    this.activityService = activityService;
  }

  @Inject
  void setWorkflowListener(final WorkflowListener workflowListener) {
    this.workflowListener = workflowListener;
  }

  public void cancelWorkflow(final String workflowId) {
    Preconditions.checkNotNull(workflowId);
    final WorkflowTacker tracker = workflowTrackers.get(workflowId);
    if(tracker != null) {
      tracker.cancel();
    }
  }

  public void cancelJob(final String workflowId, final String jobId) {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(jobId);
    final WorkflowTacker tracker = workflowTrackers.get(workflowId);
    if(tracker != null) {
      tracker.cancelJob(jobId);
    }
  }

  public void destroy() throws Exception {
    shutdown.set(true);
  }

}
