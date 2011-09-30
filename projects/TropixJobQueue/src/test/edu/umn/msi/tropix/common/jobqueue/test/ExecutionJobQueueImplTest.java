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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.PausableStateTracker;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue.ExecutionJobInfo;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueLauncher;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueRunnableCallback;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;
import edu.umn.msi.tropix.common.jobqueue.execution.system.QueueService;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

public class ExecutionJobQueueImplTest {
  private ExecutionJobQueueImpl executionQueue = new ExecutionJobQueueImpl();
  private ExecutionJobQueueObserver<Job> observer;
  private QueueService queueService;
  private ExecutionJobQueueLauncher launcher;

  @BeforeMethod(groups = "unit")
  public void init() {
    executionQueue = new ExecutionJobQueueImpl();

    observer = EasyMock.createMock(ExecutionJobQueueObserver.class);
    queueService = EasyMock.createMock(QueueService.class);
    launcher = EasyMock.createMock(ExecutionJobQueueLauncher.class);

    executionQueue.setExecutionJobQueueLauncher(launcher);
    executionQueue.setQueueService(queueService);
  }

  @Test(groups = {"unit"})
  public void testEmptyInit() {
    testInit(0, 1, true);
  }

  @Test(groups = {"unit"})
  public void singleInit() {
    testInit(1, 1, true);
  }

  @Test(groups = {"unit"})
  public void moreJobsThanThreadsInit() {
    testInit(10, 4, true);
  }

  @Test(groups = {"unit"})
  public void moreThreadsThanJobsInit() {
    testInit(5, 7, true);
  }

  @Test(groups = {"unit"})
  public void moreJobsThanThreadsInitNoStateListener() {
    testInit(10, 4, false);
  }

  @Test(groups = {"unit"})
  public void moreThreadsThanJobsInitNoStateListener() {
    testInit(5, 7, false);
  }

  @Test(groups = "unit")
  public void absentStatus() {
    emptyInit();
    final String absentKey = "12341423412342134213414";
    assert executionQueue.getExecutionState(absentKey).equals(ExecutionState.ABSENT);
  }

  @Test(groups = "unit")
  public void submitJob() {
    emptyInit();

    final JobDescriptionType jobDescription = new JobDescriptionType();
    JobDescriptionUtils.setLocalJobId(jobDescription, "4");

    final String serializedJob = JobDescriptionUtils.serialize(jobDescription);
    final Job job = EasyMock.createMock(Job.class);

    launcher.launch(EasyMock.isA(ExecutionJobQueueRunnableCallback.class), EasyMock.same(job));

    EasyMock.expect(job.getTicket()).andReturn("4").anyTimes();
    EasyMock.expect(queueService.pushJob("4", serializedJob)).andReturn(job);
    EasyMock.expect(queueService.popJob()).andReturn(job);
    EasyMock.replay(queueService, job, launcher);

    executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(jobDescription));
    EasyMock.verify(queueService, job, launcher);

  }

  private JobDescriptionType getJobDescription(final String localJobId) {
    final JobDescriptionType jobDescription = new JobDescriptionType();
    JobDescriptionUtils.setLocalJobId(jobDescription, localJobId);
    return jobDescription;
  }

  private List<Job> getJobs(final List<String> ids) {
    final List<Job> jobs = new ArrayList<Job>(ids.size());
    for(final String id : ids) {
      final Job job = createMock(Job.class);
      final JobDescriptionType jobDescription = getJobDescription(id);
      expect(job.getTicket()).andReturn(id).anyTimes();
      expect(job.getDescription()).andReturn(JobDescriptionUtils.serialize(jobDescription)).anyTimes();
      replay(job);
      jobs.add(job);
    }
    return jobs;
  }

  enum CancelType {
    BEFORE_ON_RUNNING, AFTER_RUNNING, PENDING, COMPLETE
  };

  @Test(groups = "unit")
  public void cancelBeforeOnRunning() {
    cancel(CancelType.BEFORE_ON_RUNNING);
  }

  @Test(groups = "unit")
  public void cancelAfterOnRunning() {
    cancel(CancelType.AFTER_RUNNING);
  }

  @Test(groups = "unit")
  public void cancelWhilePending() {
    cancel(CancelType.PENDING);
  }

  public void cancel(final CancelType cancelType) {
    final List<Job> jobs = getJobs(Arrays.asList("1", "2"));
    final LinkedList<ExecutionJobQueueRunnableCallback> callbacks = new LinkedList<ExecutionJobQueueRunnableCallback>();
    testWithJobs(1, jobs, callbacks);
    assert executionQueue.getExecutionState("1").equals(ExecutionState.RUNNING);
    if(cancelType.equals(CancelType.BEFORE_ON_RUNNING)) {
      executionQueue.cancel("1");
      assert executionQueue.getExecutionState("1").equals(ExecutionState.RUNNING);
      final ExecutionHook hook = createMock(ExecutionHook.class);
      hook.kill(); // Make sure hook is killed once set...
      replay(hook);
      callbacks.get(0).onRunning(hook);
      verify(hook);
    } else if(cancelType.equals(CancelType.AFTER_RUNNING)) {
      final ExecutionHook hook = createMock(ExecutionHook.class);
      hook.kill(); // Make sure hook is killed once set...
      replay(hook);
      callbacks.get(0).onRunning(hook);
      executionQueue.cancel("1");
      verify(hook);
      assert executionQueue.getExecutionState("1").equals(ExecutionState.RUNNING);
    } else if(cancelType.equals(CancelType.PENDING)) {
      expect(queueService.removeJob("2")).andReturn(jobs.get(1));
      observer.onExecutionStateChange(jobs.get(1), ExecutionState.FAILED, null);
      executionQueue.setExecutionJobQueueObserver(observer);
      replay(queueService, observer);
      executionQueue.cancel("2");
      EasyMockUtils.verifyAndReset(queueService, observer);
      assert executionQueue.getExecutionState("2").equals(ExecutionState.FAILED);
    }
  }

  private void emptyInit() {
    testWithJobs(1, Collections.<Job>emptyList(), null);
  }

  private void testWithJobs(final int maxActiveJobs, final List<Job> jobs, final List<ExecutionJobQueueRunnableCallback> callbacks) {
    executionQueue.setMaxRunningJobs(maxActiveJobs);
    final List<Job> jobsEmpty = Collections.emptyList();
    expect(queueService.getJobs()).andReturn(jobsEmpty);
    replay(queueService);
    executionQueue.init();
    EasyMockUtils.verifyAndReset(queueService);

    int jobCount = 0;
    for(final Job job : jobs) {
      queueService.pushJob(job.getTicket(), job.getDescription());
      expectLastCall().andReturn(job);

      final Reference<ExecutionJobQueueRunnableCallback> callbackReference = EasyMockUtils.newReference();

      if(jobCount < maxActiveJobs) {
        expect(queueService.popJob()).andReturn(job);
        launcher.launch(EasyMockUtils.record(callbackReference), EasyMock.same(job));
        replay(launcher);
      }
      replay(queueService);
      executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(JobDescriptionUtils.deserialize(job.getDescription())));
      EasyMockUtils.verifyAndReset(queueService);
      if(jobCount < maxActiveJobs) {
        EasyMockUtils.verifyAndReset(launcher);
        if(callbacks != null) {
          callbacks.add(callbackReference.get());
        }
      }
      jobCount++;
    }
  }

  public void testInit(final int numJobs, final int numThreads, final boolean setStateChangeListener) {
    final HashSet<JobDescriptionType> expectedDescriptions = new HashSet<JobDescriptionType>();

    final LinkedList<Job> jobs = new LinkedList<Job>();
    for(int i = 0; i < numJobs; i++) {
      final Job job = EasyMock.createMock(Job.class);
      EasyMock.expect(job.getTicket()).andReturn("" + i).anyTimes();
      EasyMock.replay(job);
      jobs.add(job);
    }
    EasyMock.expect(queueService.getJobs()).andReturn(jobs);
    final int executes = (numJobs < numThreads) ? numJobs : numThreads;

    final LinkedList<ExecutionJobQueueRunnableCallback> callbacks = new LinkedList<ExecutionJobQueueRunnableCallback>();

    for(int i = 0; i < executes; i++) {
      EasyMock.expect(queueService.popJob()).andReturn(jobs.get(i));
      launcher.launch(EasyMockUtils.addToCollection(callbacks), EasyMock.same(jobs.get(i)));
    }

    if(setStateChangeListener) {
      executionQueue.setExecutionJobQueueObserver(observer);
      for(int i = 0; i < numJobs; i++) {
        observer.onExecutionStateChange(jobs.get(i), ExecutionState.PENDING, null);
        if(i < executes) {
          observer.onExecutionStateChange(jobs.get(i), ExecutionState.RUNNING, null);
        }
      }
    }
    EasyMock.replay(queueService, launcher, observer);

    executionQueue.setMaxRunningJobs(numThreads);
    executionQueue.init();
    EasyMock.verify(queueService, launcher, observer);
    for(final Job job : jobs) {
      EasyMock.verify(job);
    }

    /*
     * Test List Jobs
     */
    EasyMock.reset(observer);
    for(int i = 0; i < numJobs; i++) {
      final Job job = jobs.get(i);
      EasyMock.reset(job);
      final JobDescriptionType jobDescription = new JobDescriptionType();
      JobDescriptionUtils.setLocalJobId(jobDescription, "" + i);
      expectedDescriptions.add(jobDescription);
      EasyMock.expect(job.getTicket()).andReturn("" + i).anyTimes();
      EasyMock.expect(job.getDescription()).andReturn(JobDescriptionUtils.serialize(jobDescription)).anyTimes();
      EasyMock.replay(job);
    }

    EasyMock.replay(observer);

    final Collection<ExecutionJobInfo<ExecutableJobDescription>> returnedJobDescriptions = executionQueue.listJobs();

    for(int i = 0; i < numJobs; i++) {
      boolean found = false;
      for(final ExecutionJobInfo<ExecutableJobDescription> jobDescription : returnedJobDescriptions) {
        if(jobDescription.getJobDescription().getTicket().equals("" + i)) {
          found = true;
        }
      }
      assert found;
    }
    EasyMock.verify(queueService, launcher, observer);
    for(final Job job : jobs) {
      EasyMock.verify(job);
    }

    /*
     * Test getExecutionState
     */
    for(final ExecutionJobQueueRunnableCallback callback : callbacks) {
      final ExecutionHook executionHook = EasyMock.createMock(ExecutionHook.class);
      callback.onRunning(executionHook);
    }

    for(int i = 0; i < numJobs; i++) {
      if(i < numThreads) {
        // Expect running
        assert executionQueue.getExecutionState("" + i).equals(ExecutionState.RUNNING);
      } else {
        // Expect pending
        assert executionQueue.getExecutionState("" + i).equals(ExecutionState.PENDING);
      }
    }
  }

  @Test(groups = "unit")
  public void stateTracker() {
    final PausableStateTracker tracker = EasyMock.createMock(PausableStateTracker.class);
    executionQueue.setPausableStateTracker(tracker);
    EasyMock.expect(tracker.isPaused()).andReturn(true);
    EasyMock.replay(tracker);
    assert !executionQueue.acceptingJobs();
    EasyMockUtils.verifyAndReset(tracker);
    EasyMock.expect(tracker.isPaused()).andReturn(false);
    EasyMock.replay(tracker);
    assert executionQueue.acceptingJobs();
    EasyMockUtils.verifyAndReset(tracker);
    EasyMock.expect(tracker.isPaused()).andReturn(true);
    EasyMock.replay(tracker);
    RuntimeException re = null;
    try {
      executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(new JobDescriptionType()));
    } catch(final IllegalStateException e) {
      re = e;
    }
    assert re != null;
    EasyMockUtils.verifyAndReset(tracker);
  }

  @Test(groups = "unit")
  public void beanOps() {
    assert executionQueue.getTicketDuration() > 0;
    executionQueue.setTicketDuration(1313);
    assert executionQueue.getTicketDuration() == 1313;

    assert executionQueue.getMaxRunningJobs() > 0;
    executionQueue.setMaxRunningJobs(12);
    assert executionQueue.getMaxRunningJobs() == 12;

    assert executionQueue.getPopRetryTime() > 0;
    executionQueue.setPopRetryTime(123L);
    assert executionQueue.getPopRetryTime() == 123L;
  }

  private Job getJob(final String ticket) {
    final Job job = EasyMock.createMock(Job.class);
    EasyMock.expect(job.getTicket()).andStubReturn(ticket);
    EasyMock.replay(job);
    return job;
  }

  @Test(groups = "unit")
  public void stateTracker2() {
    // TODO: Finish verifying that if service is paused a second job wouldn't be popped...
    final PausableStateTracker tracker = EasyMock.createMock(PausableStateTracker.class);
    executionQueue.setPausableStateTracker(tracker);
    executionQueue.setMaxRunningJobs(1);
    final JobDescriptionType jobDescription1 = getJobDescription("1"), jobDescription2 = getJobDescription("2");
    final String description1 = JobDescriptionUtils.serialize(jobDescription1);
    final String description2 = JobDescriptionUtils.serialize(jobDescription2);

    final Job job1 = getJob("1"), job2 = getJob("2");

    final Reference<ExecutionJobQueueRunnableCallback> callbackReference = EasyMockUtils.newReference();
    launcher.launch(EasyMockUtils.record(callbackReference), EasyMock.same(job1));
    EasyMock.expect(queueService.pushJob(EasyMock.eq("1"), EasyMock.eq(description1))).andReturn(job1);
    EasyMock.expect(queueService.pushJob(EasyMock.eq("2"), EasyMock.eq(description2))).andReturn(job2);
    EasyMock.expect(queueService.popJob()).andReturn(job1);
    EasyMock.expect(tracker.isPaused()).andReturn(false).anyTimes();
    EasyMock.replay(queueService, tracker, launcher);
    executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(jobDescription1));
    executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(jobDescription2));
    EasyMockUtils.verifyAndReset(queueService, tracker, launcher);
    final ExecutionHook hook1 = EasyMock.createMock(ExecutionHook.class);
    EasyMock.expect(tracker.isPaused()).andReturn(true).anyTimes();
    final Timer timer = EasyMock.createMock(Timer.class);
    // executionQueue.setTimer(timer);
    // timer.schedule(EasyMockUtils.record(runnableReference), EasyMock.isA(Long.class));
    EasyMock.replay(queueService, tracker, launcher, hook1, timer);
    callbackReference.get().onRunning(hook1);
    callbackReference.get().onComplete(ExecutionState.COMPLETE);
    EasyMockUtils.verifyAndReset(queueService, tracker, launcher, hook1, timer);
    // EasyMock.replay(queueService, tracker,launcher,hook1,timer);
    // assert executionQueue.getExecutionState("1").equals(ExecutionState.COMPLETE);
    // runnableReference.get().run();
    // assert executionQueue.getExecutionState("1").equals(ExecutionState.ABSENT);
    // EasyMockUtils.verifyAndReset(queueService, tracker,launcher,hook1,timer);
  }

  @Test(groups = "unit")
  public void popFailure() {
    final Timer timer = EasyMock.createMock(Timer.class);
    executionQueue.setTimer(timer);
    final Reference<Runnable> runnableReference = EasyMockUtils.newReference();
    assert timer != null;
    timer.schedule(EasyMockUtils.record(runnableReference), EasyMock.eq(10000L));
    final JobDescriptionType jd1 = getJobDescription("1");
    final Job job = getJob("1");
    queueService.pushJob("1", JobDescriptionUtils.serialize(jd1));
    EasyMock.expectLastCall().andReturn(job);
    EasyMock.expect(queueService.popJob()).andThrow(new RuntimeException());
    EasyMock.replay(queueService, timer);
    RuntimeException re = null;
    try {
      executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(jd1));
    } catch(final RuntimeException e) {
      re = e;
    }
    assert re == null;
    EasyMockUtils.verifyAndReset(queueService, timer);
    EasyMock.expect(queueService.popJob()).andReturn(job);
    launcher.launch(EasyMock.isA(ExecutionJobQueueRunnableCallback.class), EasyMock.same(job));
    EasyMock.replay(queueService, launcher);
    runnableReference.get().run();
    EasyMockUtils.verifyAndReset(queueService, launcher);
  }

  @Test(groups = "unit")
  public void onStartAndStop() {
    executionQueue.setMaxRunningJobs(1);
    executionQueue.setExecutionJobQueueObserver(observer);
    observer.onShutdown();
    EasyMock.replay(observer);
    executionQueue.onShutdown();
    EasyMockUtils.verifyAndReset(observer);
    observer.onStartup();
    EasyMock.expect(queueService.popJob()).andReturn(null);
    EasyMock.replay(observer, queueService);
    executionQueue.onStartup();
    EasyMockUtils.verifyAndReset(observer, queueService);

    final JobDescriptionType jd1 = getJobDescription("1");
    final Job job1 = getJob("1");
    queueService.pushJob("1", JobDescriptionUtils.serialize(jd1));
    EasyMock.expectLastCall().andReturn(job1);
    EasyMock.expect(queueService.popJob()).andReturn(null);
    observer.onSubmission(jd1);
    observer.onExecutionStateChange(job1, ExecutionState.PENDING, null);
    EasyMock.replay(queueService, observer);
    executionQueue.submitJob(ExecutableJobDescriptions.forJobDescriptionType(jd1));
    EasyMockUtils.verifyAndReset(queueService, observer);

  }
}