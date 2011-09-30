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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.collect.Closures;
import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionType;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;

@ExecutionType("gram")
public class GramExecutionJobQueueImpl implements ExecutionJobQueue<ExecutableJobDescription> {
  private static final Log LOG = LogFactory.getLog(GramExecutionJobQueueImpl.class);
  private ExecutionJobQueueObserver<GramExecutionJob> executionJobQueueObserver = null;
  private GramExecutionJobService gramExecutionJobService;
  private GramJobSubmitter gramJobSubmitter;
  private GramJobResolver gramJobResolver;
  private GramJobPoller gramJobPoller;
  private Closure<JobDescriptionType> defaultsClosure = Closures.nullClosure();

  public void setDefaultsClosure(final Closure<JobDescriptionType> defaultsClosure) {
    this.defaultsClosure = defaultsClosure;
  }

  public void init() {
    final Collection<GramExecutionJob> gramExecutionJobs = gramExecutionJobService.loadJobs();
    for(final GramExecutionJob gramExecutionJob : gramExecutionJobs) {
      gramJobPoller.poll(gramExecutionJob);
    }
  }

  public void cancel(final String localJobId) {
    LOG.trace("Attempting to cancel job with id " + localJobId);
    final GramExecutionJob gramExecutionJob = gramExecutionJobService.loadJob(localJobId);
    if(gramExecutionJob != null) {
      final GramJob gramJob = gramJobResolver.getGramJob(gramExecutionJob);
      gramJob.cancel();
    }
  }

  public ExecutionState getExecutionState(final String localJobId) {
    ExecutionState state;
    try {
      final GramExecutionJob gramExecutionJob = gramExecutionJobService.loadJob(localJobId);
      state = ExecutionState.valueOf(gramExecutionJob.getState());
    } catch(final RuntimeException e) {
      // Though choice here..., ABSENT, PENDING, throw an exception... not sure
      // Probably should depend on wheather it is a db connection problem or invalid
      // key problem
      state = ExecutionState.ABSENT;
    }
    return state;
  }

  public Collection<ExecutionJobInfo<ExecutableJobDescription>> listJobs() {
    final Collection<GramExecutionJob> gramExecutionJobs = gramExecutionJobService.loadJobs();
    return Collections.transform(gramExecutionJobs, new Function<GramExecutionJob, ExecutionJobInfo<ExecutableJobDescription>>() {
      public ExecutionJobInfo<ExecutableJobDescription> apply(final GramExecutionJob gramExecutionJob) {
        final String jobDescriptionStr = gramExecutionJob.getDescription();
        final JobDescriptionType jobDescription = JobDescriptionUtils.deserialize(jobDescriptionStr);
        final ExecutionState state = ExecutionState.valueOf(gramExecutionJob.getState());
        return ExecutionJobInfo.create(ExecutableJobDescriptions.forJobDescriptionType(jobDescription), state);
      }
    });
  }

  public void submitJob(final ExecutableJobDescription executableJobDescription) {
    final JobDescriptionType description = executableJobDescription.getJobDescriptionType();
    defaultsClosure.apply(description);

    final Credential proxy = JobDescriptionUtils.getProxy(description);
    final GramJob gramJob = gramJobSubmitter.createGramJob(description, proxy);
    final String handle = gramJob.getHandle();
    final String jobDescriptionStr = JobDescriptionUtils.serialize(description);
    final String proxyStr = proxy.toString();
    final String localJobId = JobDescriptionUtils.getLocalJobId(description);
    final GramExecutionJob gramExecutionJob = gramExecutionJobService.persistJob(handle, jobDescriptionStr, localJobId, proxyStr, ExecutionState.PENDING.toString());
    gramJobPoller.poll(gramExecutionJob);
    executionJobQueueObserver.onSubmission(description);
  }

  public void setExecutionJobQueueObserver(final ExecutionJobQueueObserver<GramExecutionJob> executionJobQueueObserver) {
    this.executionJobQueueObserver = executionJobQueueObserver;
  }

  public void setGramExecutionJobService(final GramExecutionJobService gramExecutionJobService) {
    this.gramExecutionJobService = gramExecutionJobService;
  }

  public void setGramJobSubmitter(final GramJobSubmitter gramJobSubmitter) {
    this.gramJobSubmitter = gramJobSubmitter;
  }

  public void setGramJobResolver(final GramJobResolver gramJobResolver) {
    this.gramJobResolver = gramJobResolver;
  }

  public void setGramJobPoller(final GramJobPoller gramJobPoller) {
    this.gramJobPoller = gramJobPoller;
  }

}
