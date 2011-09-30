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

package edu.umn.msi.tropix.common.jobqueue.execution.system;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.concurrent.InitializationTracker;
import edu.umn.msi.tropix.common.concurrent.InitializationTrackers;
import edu.umn.msi.tropix.common.concurrent.PausableCallback;
import edu.umn.msi.tropix.common.concurrent.PausableStateTracker;
import edu.umn.msi.tropix.common.concurrent.PausableStateTrackers;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.concurrent.Timers;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionType;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

// submitJob might fire callback before completing!
@ExecutionType("system")
@ManagedResource
public class ExecutionJobQueueImpl implements ExecutionJobQueue<ExecutableJobDescription>, PausableCallback {
  // Internally Managed
  private static final Log LOG = LogFactory.getLog(ExecutionJobQueueImpl.class);
  private static final long DEFAULT_TICKET_DURATION = 1000 * 60 * 60 * 2; // Keep completed ticket status around for 2 hours by default
  private static final long DEFAULT_POP_RETRY_TIME = 1000 * 10;
  private final ConcurrentHashMap<String, ExecutionInfo> executionInfoMap = new ConcurrentHashMap<String, ExecutionInfo>();
  private final Object queueLock = new Object();
  private final Object executionLock = new Object();
  private int runningJobs = 0;
  private boolean initialized = false;

  // Set externally
  private ExecutionJobQueueLauncher executionJobQueueLauncher;

  // Optionally set externally
  private QueueService queueService;
  private final InitializationTracker<String> initializationTracker = InitializationTrackers.getDefault();
  private Timer timer = Timers.getDefault();
  private PausableStateTracker pausableStateTracker = PausableStateTrackers.get();
  private ExecutionJobQueueObserver<Job> executionJobQueueObserver = null;
  private int maxRunningJobs = 1;
  private long ticketDuration = DEFAULT_TICKET_DURATION;
  private long popRetryTime = DEFAULT_POP_RETRY_TIME;

  public ExecutionJobQueueImpl() {
    LOG.debug("Constructing " + toString());
  }

  public void init() {
    LOG.debug("Starting init...");
    pausableStateTracker.registerPausableCallback(this);
    ensureJobsInitialized();
    LOG.debug("Ending init...");
  }

  private void ensureJobsInitialized() {
    synchronized(initializationTracker) {
      if(!initialized) {
        LOG.info("Initializing jobs started.");
        for(final Job job : queueService.getJobs()) {
          initializeJob(job);
        }
        initializationTracker.initializeAll();
        initialized = true;
        LOG.info("Initializing jobs complete.");
      }
    }
  }

  public ExecutionState getExecutionState(final String localJobId) {
    initializationTracker.waitForInitialization(localJobId);
    final ExecutionInfo info = executionInfoMap.get(localJobId);
    ExecutionState state = null;
    if(info == null) {
      state = ExecutionState.ABSENT;
    } else {
      state = info.executionState;
    }
    return state;
  }

  public Collection<ExecutionJobInfo<ExecutableJobDescription>> listJobs() {
    ensureJobsInitialized();
    final List<ExecutionJobInfo<ExecutableJobDescription>> jobInfoList = Lists.newLinkedList();
    for(final ExecutionInfo info : executionInfoMap.values()) {
      final String description = info.job.getDescription();
      // LOG.trace("Loading job with description string " + description);
      final JobDescriptionType jobDescription = JobDescriptionUtils.deserialize(description);
      final ExecutionState executionState = info.executionState;
      final ExecutableJobDescription executableJobDescription = ExecutableJobDescriptions.forJobDescriptionType(jobDescription);
      LOG.debug("Found ExecutableJobDescription " + executableJobDescription);
      jobInfoList.add(ExecutionJobInfo.create(executableJobDescription, executionState));
    }
    return jobInfoList;
  }

  public void submitJob(final ExecutableJobDescription executableJobDescription) {
    final JobDescriptionType jobDescription = executableJobDescription.getJobDescriptionType();
    if(pausableStateTracker.isPaused()) {
      throw new IllegalStateException("Attempt to submit a job to an inactive ExecutionJobQueue");
    }
    if(executionJobQueueObserver != null) {
      executionJobQueueObserver.onSubmission(jobDescription);
    }
    final String localJobId = JobDescriptionUtils.getLocalJobId(jobDescription);
    initializeJob(queueService.pushJob(localJobId, JobDescriptionUtils.serialize(jobDescription)));
  }

  @ManagedOperation
  public void kill() {
    String runningId = null;
    for(final Map.Entry<String, ExecutionInfo> entry : executionInfoMap.entrySet()) {
      final ExecutionInfo info = entry.getValue();
      synchronized(info) {
        if(info.executionState.equals(ExecutionState.RUNNING)) {
          runningId = entry.getKey();
          break;
        }
      }
    }
    if(runningId != null) {
      cancel(runningId);
    }
  }

  @ManagedOperation
  public void cancel(final String localJobId) {
    initializationTracker.waitForInitialization(localJobId);
    final ExecutionInfo info = executionInfoMap.get(localJobId);
    synchronized(info) { // Don't allow the state of info to change
      info.cancelled = true;
      final ExecutionState state = info.executionState;
      synchronized(queueLock) { // Don't allow popping of job, it could be this one!
        if(state.equals(ExecutionState.PENDING)) {
          queueService.removeJob(localJobId);
          changeState(localJobId, ExecutionState.FAILED);
        } else if(state.equals(ExecutionState.RUNNING)) {
          if(info.executionHook != null) {
            info.executionHook.kill();
          }
        }
      }
    }
  }

  public boolean acceptingJobs() {
    return !pausableStateTracker.isPaused();
  }

  public void onShutdown() {
    if(executionJobQueueObserver != null) {
      executionJobQueueObserver.onShutdown();
    }
  }

  public void onStartup() {
    if(executionJobQueueObserver != null) {
      executionJobQueueObserver.onStartup();
    }
    for(int i = 0; i < maxRunningJobs; i++) {
      attemptExecute();
    }
  }

  // Helper methods
  private void completeJob(final String localJobId, final ExecutionState finalState) {
    changeState(localJobId, finalState);
    synchronized(executionLock) {
      runningJobs--;
    }
    timer.schedule(new Runnable() {
      public void run() {
        executionInfoMap.remove(localJobId);
      }
    }, ticketDuration);
    attemptExecute();
  }

  private class ExecutionRunnableCallbackImpl implements ExecutionJobQueueRunnableCallback {
    private String localJobId;

    public void onComplete(final ExecutionState executionState) {
      completeJob(localJobId, executionState);
    }

    public void onRunning(final ExecutionHook executionHook) {
      final ExecutionInfo info = executionInfoMap.get(localJobId);
      synchronized(info) {
        info.executionHook = executionHook;
        if(info.cancelled) {
          executionHook.kill();
        }
      }
    }
  }

  private void attemptExecute() {
    if(pausableStateTracker.isPaused()) {
      return;
    }
    Job job = null;
    synchronized(executionLock) {
      if(runningJobs >= maxRunningJobs) {
        return;
      }
      try {
        job = queueService.popJob();
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(LOG, t, "Exception thrown while attempting to pop job from queue. Will try again later.");
        timer.schedule(new Runnable() {
          public void run() {
            attemptExecute();
          }
        }, 10 * 1000);
      }
      if(job == null) {
        return;
      }
      runningJobs++;
      changeState(job.getTicket(), ExecutionState.RUNNING);
    }

    final ExecutionRunnableCallbackImpl callback = new ExecutionRunnableCallbackImpl();
    callback.localJobId = job.getTicket();
    executionJobQueueLauncher.launch(callback, job);
  }

  private void initializeJob(final Job job) {
    final ExecutionInfo info = new ExecutionInfo();
    info.job = job;
    executionInfoMap.putIfAbsent(job.getTicket(), info);
    changeState(job.getTicket(), ExecutionState.PENDING);
    initializationTracker.initialize(job.getTicket());
    attemptExecute();
  }

  private void changeState(final String localJobId, final ExecutionState executionState) {
    final ExecutionInfo info = executionInfoMap.get(localJobId);
    synchronized(info) {
      info.executionState = executionState;
    }
    if(executionJobQueueObserver != null) {
      executionJobQueueObserver.onExecutionStateChange(info.job, executionState, null);
    }
  }

  private static class ExecutionInfo {
    private ExecutionState executionState = ExecutionState.PENDING;
    private ExecutionHook executionHook = null;
    private Job job = null;
    private boolean cancelled = false;
  }

  public void setExecutionJobQueueLauncher(final ExecutionJobQueueLauncher executionJobQueueLauncher) {
    this.executionJobQueueLauncher = executionJobQueueLauncher;
  }

  public void setTimer(final Timer timer) {
    this.timer = timer;
  }

  public void setPausableStateTracker(final PausableStateTracker pausableStateTracker) {
    this.pausableStateTracker = pausableStateTracker;
  }

  public void setExecutionJobQueueObserver(final ExecutionJobQueueObserver<Job> executionJobQueueObserver) {
    this.executionJobQueueObserver = executionJobQueueObserver;
  }

  public void setQueueService(final QueueService queueService) {
    this.queueService = queueService;
  }

  @ManagedAttribute
  public void setTicketDuration(final long ticketDuration) {
    this.ticketDuration = ticketDuration;
  }

  @ManagedAttribute
  public long getTicketDuration() {
    return ticketDuration;
  }

  @ManagedAttribute
  public void setMaxRunningJobs(final int maxRunningJobs) {
    this.maxRunningJobs = maxRunningJobs;
  }

  @ManagedAttribute
  public int getMaxRunningJobs() {
    return maxRunningJobs;
  }

  @ManagedAttribute
  public long getPopRetryTime() {
    return popRetryTime;
  }

  @ManagedAttribute
  public void setPopRetryTime(final long popRetryTime) {
    this.popRetryTime = popRetryTime;
  }

}