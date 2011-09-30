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

package edu.umn.msi.tropix.common.jobqueue.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue.ExecutionJobInfo;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJobQueueImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJobService;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobPoller;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobResolver;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobSubmitter;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class GramExecutionJobQueueImplTest {
  private GramExecutionJobQueueImpl queue;
  private ExecutionJobQueueObserver<GramExecutionJob> executionJobQueueObserver = null;
  private GramExecutionJobService gramExecutionJobService;
  private GramJobSubmitter gramJobSubmitter;
  private GramJobResolver gramJobResolver;
  private GramJobPoller gramJobPoller;
  private Credential proxy;
  private MockObjectCollection mockObjects;

  @BeforeTest(groups = "unit")
  public void init() {
    queue = new GramExecutionJobQueueImpl();
    executionJobQueueObserver = EasyMock.createMock(ExecutionJobQueueObserver.class);
    gramExecutionJobService = EasyMock.createMock(GramExecutionJobService.class);
    gramJobSubmitter = EasyMock.createMock(GramJobSubmitter.class);
    gramJobResolver = EasyMock.createMock(GramJobResolver.class);
    gramJobPoller = EasyMock.createMock(GramJobPoller.class);
    proxy = Credentials.getMock();

    queue.setGramExecutionJobService(gramExecutionJobService);
    queue.setExecutionJobQueueObserver(executionJobQueueObserver);
    queue.setGramJobSubmitter(gramJobSubmitter);
    queue.setGramJobResolver(gramJobResolver);
    queue.setGramJobPoller(gramJobPoller);

    mockObjects = MockObjectCollection.fromObjects(executionJobQueueObserver, gramExecutionJobService, gramJobSubmitter, gramJobResolver,
        gramJobPoller);
  }

  @Test(groups = "unit")
  public void listJobsEmpty() {
    listJobs(Arrays.<GramExecutionJob>asList());
  }

  @Test(groups = "unit")
  public void listJobsOne() {
    listJobs(getOneJobCollection());
  }

  public Collection<GramExecutionJob> getOneJobCollection() {
    final GramExecutionJob job = EasyMock.createMock(GramExecutionJob.class);
    EasyMock.expect(job.getState()).andStubReturn(ExecutionState.RUNNING.toString());
    final JobDescriptionType description = new JobDescriptionType();
    JobDescriptionUtils.setProxy(description, proxy);
    EasyMock.expect(job.getDescription()).andStubReturn(JobDescriptionUtils.serialize(description));
    EasyMock.replay(job);
    return Arrays.asList(job);
  }

  public void listJobs(final Collection<GramExecutionJob> jobs) {
    EasyMock.expect(gramExecutionJobService.loadJobs()).andReturn(jobs);
    mockObjects.replay();
    final Collection<ExecutionJobInfo<ExecutableJobDescription>> eJobs = queue.listJobs();
    mockObjects.verifyAndReset();
    final Iterator<GramExecutionJob> jobIter = jobs.iterator();
    for(final ExecutionJobInfo<ExecutableJobDescription> jobInfo : eJobs) {
      final GramExecutionJob job = jobIter.next();
      assert jobInfo.getExecutionState().equals(ExecutionState.valueOf(job.getState()));
      assert jobInfo.getJobDescription().getJobDescriptionType().equals(JobDescriptionUtils.deserialize(job.getDescription()));
    }
  }

  @Test(groups = "unit")
  public void testInitEmpty() {
    testInit(Arrays.<GramExecutionJob>asList());
  }

  @Test(groups = "unit")
  public void testInitOne() {
    testInit(getOneJobCollection());
  }

  public void testInit(final Collection<GramExecutionJob> jobs) {
    EasyMock.expect(gramExecutionJobService.loadJobs()).andReturn(jobs);
    for(final GramExecutionJob job : jobs) {
      gramJobPoller.poll(EasyMock.same(job));
    }
    mockObjects.replay();
    queue.init();
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void cancel() {
    final GramExecutionJob executionJob = EasyMock.createMock(GramExecutionJob.class);
    mockObjects.add(executionJob);
    EasyMock.expect(gramExecutionJobService.loadJob("123")).andReturn(executionJob);
    final GramJob gramJob = EasyMock.createMock(GramJob.class);
    mockObjects.add(gramJob);
    EasyMock.expect(gramJobResolver.getGramJob(executionJob)).andReturn(gramJob);
    gramJob.cancel();
    mockObjects.replay();
    queue.cancel("123");
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void getStatus() {
    final GramExecutionJob executionJob = EasyMock.createMock(GramExecutionJob.class);
    mockObjects.add(executionJob);
    EasyMock.expect(gramExecutionJobService.loadJob("123")).andReturn(executionJob);
    EasyMock.expect(executionJob.getState()).andReturn(ExecutionState.RUNNING.toString());
    mockObjects.replay();
    assert ExecutionState.RUNNING.equals(queue.getExecutionState("123"));
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void submit() {
    final JobDescriptionType description = new JobDescriptionType();
    JobDescriptionUtils.setProxy(description, proxy);
    executionJobQueueObserver.onSubmission(EasyMock.same(description));
    JobDescriptionUtils.setLocalJobId(description, "123");
    final GramJob gramJob = EasyMock.createMock(GramJob.class);
    mockObjects.add(gramJob);
    EasyMock.expect(gramJobSubmitter.createGramJob(EasyMock.same(description), EasyMock.isA(Credential.class))).andReturn(gramJob);
    EasyMock.expect(gramJob.getHandle()).andReturn("the handle");
    final GramExecutionJob executionJob = EasyMock.createMock(GramExecutionJob.class);
    mockObjects.add(executionJob);
    gramExecutionJobService.persistJob(EasyMock.eq("the handle"), EasyMock.eq(JobDescriptionUtils.serialize(description)), EasyMock.eq("123"),
        EasyMock.eq(proxy.toString()), EasyMock.eq(ExecutionState.PENDING.toString()));
    EasyMock.expectLastCall().andReturn(executionJob);
    gramJobPoller.poll(EasyMock.same(executionJob));
    mockObjects.replay();
    queue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(description));
    mockObjects.verifyAndReset();
  }

}
