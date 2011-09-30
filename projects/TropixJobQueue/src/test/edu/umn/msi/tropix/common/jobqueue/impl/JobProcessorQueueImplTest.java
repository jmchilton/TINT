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

package edu.umn.msi.tropix.common.jobqueue.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.concurrent.InitializationTracker;
import edu.umn.msi.tropix.common.concurrent.PausableStateTracker;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorPostProcessedListener;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorRecoverer;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.QueueStage;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue.ExecutionJobInfo;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackable;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusUtils;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class JobProcessorQueueImplTest {
  private JobProcessorQueueImpl<JobDescription> queue;
  private Timer timer;
  private ExecutionJobQueue<JobDescription> mockQueue;
  private Executor executor;
  private PausableStateTracker pausableStateTracker;
  private JobProcessorPostProcessedListener<JobDescription> listener;
  private Supplier<Ticket> mockTicketSupplier;
  private JobProcessorRecoverer<JobDescription> jobProcessorRecoverer;
  private final MockObjectCollection mockObjects = new MockObjectCollection();

  @JobType("test")
  class TestJobProcessorQueueImpl extends JobProcessorQueueImpl<JobDescription> {
  }

  @SuppressWarnings("unchecked")
  @BeforeMethod(groups = "unit")
  public void init() {
    queue = new TestJobProcessorQueueImpl();

    mockObjects.clear();

    timer = createMock(Timer.class);
    mockQueue = createMock(ExecutionJobQueue.class);
    executor = createMock(Executor.class);
    pausableStateTracker = createMock(PausableStateTracker.class);
    listener = createMock(JobProcessorPostProcessedListener.class);
    mockTicketSupplier = EasyMockUtils.createMockSupplier();
    jobProcessorRecoverer = createMock(JobProcessorRecoverer.class);

    queue.setTimer(timer);
    queue.setExecutionJobQueue(mockQueue);
    queue.setExecutor(executor);
    queue.setPausableStateTracker(pausableStateTracker);
    queue.setJobProcessorPostProcessedListener(listener);
    queue.setTicketSupplier(mockTicketSupplier);
    queue.setJobProcessorRecoverer(jobProcessorRecoverer);

    mockObjects.add(timer, mockQueue, executor, pausableStateTracker, listener, mockTicketSupplier, jobProcessorRecoverer);
  }

  @Test(groups = "unit")
  public void testEmptyInit() {
    init(0, 0, false, false);

  }

  @Test(groups = "unit")
  public void test1Init() {
    init(1, 0, false, false);
  }

  @Test(groups = "unit")
  public void testManyInit() {
    init(10, 3, false, false);
  }

  public void testInitProblems() {
    init(10, 3, true, false);
  }

  @Test(groups = "unit")
  public void testSubmitAndException() {
    submit(LifeCycleType.SUBMISSION_EXCEPTION);
  }

  @Test(groups = "unit")
  public void testSubmitAndPreprocessException() {
    submit(LifeCycleType.PREPROCESS_EXCEPTION);
  }

  @Test(groups = "unit")
  public void testSubmitAndPostprocessException() {
    submit(LifeCycleType.POSTPROCESS_EXCEPTION);
  }

  @Test(groups = "unit")
  public void testSubmitAndComplete() {
    submit(LifeCycleType.COMPLETE);
  }

  @Test(groups = "unit")
  public void testSubmitAndFail() {
    submit(LifeCycleType.FAILED);
  }

  @Test(groups = "unit")
  public void testSubmitAndAbsent() {
    submit(LifeCycleType.ABSENT);
  }

  @Test(groups = "unit")
  public void testSubmitAndCancelPreprocessing() {
    submit(LifeCycleType.CANCELLED_PREPROCESSING);
  }

  @Test(groups = "unit")
  public void testSubmitAndCancelPending() {
    submit(LifeCycleType.CANCELLED_PENDING);
  }

  @Test(groups = "unit")
  public void testSubmitAndCancelRunning() {
    submit(LifeCycleType.CANCELLED_RUNNING);
  }

  enum LifeCycleType {
    COMPLETE, CANCELLED_RUNNING, CANCELLED_PENDING, CANCELLED_PREPROCESSING, FAILED, ABSENT, POSTPROCESS_EXCEPTION, PREPROCESS_EXCEPTION, SUBMISSION_EXCEPTION;
  }

  interface TrackableJobProcessor extends JobProcessor<JobDescription>, ProgressTrackable {
  }

  @Test(groups = "unit")
  public void percentCompleteEndWithComplete() {
    percentComplete(ExecutionState.COMPLETE);
  }

  @Test(groups = "unit")
  public void percentCompleteEndWithFail() {
    percentComplete(ExecutionState.ABSENT);
  }

  @Test(groups = "unit")
  public void percentCompleteEndWithAbsent() {
    percentComplete(ExecutionState.FAILED);
  }

  @Test(groups = "unit")
  public void getTrackableExcepton() {
    final TrackableJobProcessor jobProcessor = createMock(TrackableJobProcessor.class);
    mockObjects.add(jobProcessor);
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    expect(mockTicketSupplier.get()).andReturn(new Ticket("123"));
    // Submit job
    final Reference<Runnable> preprocessRunnableReference = EasyMockUtils.newReference();
    executor.execute(EasyMockUtils.record(preprocessRunnableReference));
    mockObjects.replay();
    queue.submitJob(jobProcessor, "test");
    mockObjects.verifyAndReset();

    // Allow preprocessing
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    final JobDescription jobDescription = EasyMock.createMock(JobDescription.class);
    jobDescription.setTicket("123");
    mockObjects.add(jobDescription);
    expect(jobProcessor.preprocess()).andReturn(jobDescription);
    mockQueue.submitJob(jobDescription);
    mockObjects.replay();
    preprocessRunnableReference.get().run();
    mockObjects.verifyAndReset();

    // Send running event
    expect(jobProcessor.getProgressTracker()).andThrow(new RuntimeException());
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    // Verify executor doesn't start up a progress tracker
    mockObjects.replay();
    queue.jobStateChanged("123", ExecutionState.RUNNING, null);
    mockObjects.verifyAndReset();
  }

  public void percentComplete(final ExecutionState endState) {
    final TrackableJobProcessor jobProcessor = createMock(TrackableJobProcessor.class);
    mockObjects.add(jobProcessor);
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    expect(mockTicketSupplier.get()).andReturn(new Ticket("123"));
    // Submit job
    final Reference<Runnable> preprocessRunnableReference = EasyMockUtils.newReference();
    executor.execute(EasyMockUtils.record(preprocessRunnableReference));
    mockObjects.replay();
    queue.submitJob(jobProcessor, "test");
    mockObjects.verifyAndReset();

    // Allow preprocessing
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    final JobDescription jobDescription = EasyMock.createMock(JobDescription.class);
    jobDescription.setTicket("123");
    mockObjects.add(jobDescription);
    expect(jobProcessor.preprocess()).andReturn(jobDescription);
    mockQueue.submitJob(jobDescription);
    mockObjects.replay();
    preprocessRunnableReference.get().run();
    mockObjects.verifyAndReset();

    // Send running event
    final Reference<Runnable> runnableReference = EasyMockUtils.newReference();
    executor.execute(EasyMockUtils.record(runnableReference));
    final ProgressTracker progressTracker = createMock(ProgressTracker.class);
    mockObjects.add(progressTracker);
    expect(jobProcessor.getProgressTracker()).andReturn(progressTracker);
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    queue.jobStateChanged("123", ExecutionState.RUNNING, null);
    mockObjects.verifyAndReset();

    // Run start progress
    final Reference<ProgressTrackerCallback> callbackReference = EasyMockUtils.newReference();
    progressTracker.start(EasyMockUtils.record(callbackReference));
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    runnableReference.get().run();
    mockObjects.verifyAndReset();
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    Status status = queue.getStatus(new Ticket("123"));
    assert status.getStage().getValue().equals(StageEnumeration.Running);
    assert StatusUtils.getPercentComplete(status) == null;

    final ProgressTrackerCallback callback = callbackReference.get();
    assert callback != null;
    callback.updateProgress(null);
    status = queue.getStatus(new Ticket("123"));
    assert StatusUtils.getPercentComplete(status) == null;

    callback.updateProgress(.01);
    status = queue.getStatus(new Ticket("123"));
    assert StatusUtils.getPercentComplete(status).equals(.01);

    callback.updateProgress(.99);
    status = queue.getStatus(new Ticket("123"));
    assert StatusUtils.getPercentComplete(status).equals(.99);
    mockObjects.verifyAndReset();

    // Send running again, make sure doesn't cause to rerun
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    queue.jobStateChanged("123", ExecutionState.RUNNING, null);
    mockObjects.verifyAndReset();

    // Send pending again, make sure doesn't cause stop
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    queue.jobStateChanged("123", ExecutionState.PENDING, null);
    mockObjects.verifyAndReset();

    // End and make sure stop is called
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    executor.execute(isA(Runnable.class));
    progressTracker.stop();
    mockObjects.replay();
    queue.jobStateChanged("123", endState, null);
    mockObjects.verifyAndReset();

    // Send running again, make sure doesn't cause to rerun
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    queue.jobStateChanged("123", ExecutionState.RUNNING, null);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void submitDuplicateTicket() {
    final JobProcessor<JobDescription> jobProcessor = EasyMock.createMock(JobProcessor.class);
    mockObjects.add(jobProcessor);
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    executor.execute(isA(Runnable.class));
    expectLastCall().anyTimes();

    expect(mockTicketSupplier.get()).andReturn(new Ticket("123")).times(2);
    mockObjects.replay();

    queue.submitJob(jobProcessor, "test");
    queue.submitJob(jobProcessor, "test");

  }

  public void submit(final LifeCycleType type) {
    queue.setTicketDuration(1000L);
    final JobProcessor<JobDescription> jobProcessor = EasyMock.createMock(JobProcessor.class);
    mockObjects.add(jobProcessor);
    final JobDescription jobDescription = EasyMock.createMock(JobDescription.class);
    mockObjects.add(jobDescription);
    expect(mockTicketSupplier.get()).andReturn(new Ticket("123"));
    final Reference<Runnable> runnableReference = EasyMockUtils.newReference();
    final Reference<Runnable> cleanupRunnable = EasyMockUtils.newReference();
    executor.execute(EasyMockUtils.record(runnableReference));

    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    queue.submitJob(jobProcessor, "test");

    mockObjects.verifyAndReset();

    final Status status = queue.getStatus(new Ticket("123"));
    // Test here that when submitJob return status is Preprocessing and runnable does not
    // need to execute for this to happen
    assert QueueStage.fromStatusUpdateList(status).equals(QueueStage.PREPROCESSING);

    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    if(!type.equals(LifeCycleType.PREPROCESS_EXCEPTION)) {
      expect(jobProcessor.preprocess()).andReturn(jobDescription);
      EasyMock.expect(jobDescription.getTicket()).andStubReturn("123");
      if(!type.equals(LifeCycleType.CANCELLED_PREPROCESSING)) {
        jobDescription.setTicket("123");
        mockQueue.submitJob(jobDescription);
        if(type.equals(LifeCycleType.SUBMISSION_EXCEPTION)) {
          expectLastCall().andThrow(new NullPointerException());
        }
      }
    } else if(type.equals(LifeCycleType.PREPROCESS_EXCEPTION)) {
      expect(jobProcessor.preprocess()).andThrow(new IllegalStateException());
    } else {
      throw new IllegalStateException();
    }

    if(type.equals(LifeCycleType.PREPROCESS_EXCEPTION) || type.equals(LifeCycleType.SUBMISSION_EXCEPTION)
        || type.equals(LifeCycleType.CANCELLED_PREPROCESSING)) {
      executor.execute(EasyMockUtils.record(runnableReference));
    }

    mockObjects.replay();
    if(type.equals(LifeCycleType.CANCELLED_PREPROCESSING)) {
      queue.cancel(new Ticket("123"));
    }
    runnableReference.get().run();
    mockObjects.verifyAndReset();

    if(type.equals(LifeCycleType.CANCELLED_PENDING)) {
      mockQueue.cancel("123");
      mockObjects.replay();
      queue.cancel(new Ticket("123"));
      mockObjects.verifyAndReset();
    }

    if(!type.equals(LifeCycleType.PREPROCESS_EXCEPTION) && !type.equals(LifeCycleType.SUBMISSION_EXCEPTION)
        && !type.equals(LifeCycleType.CANCELLED_PREPROCESSING)) {
      expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
      mockObjects.replay();
      assert QueueStage.fromStatusUpdateList(queue.getStatus(new Ticket("123"))).equals(QueueStage.PENDING);

      // Test out of order callbacks
      queue.jobStateChanged("123", ExecutionState.RUNNING, null);
      assert QueueStage.fromStatusUpdateList(queue.getStatus(new Ticket("123"))).equals(QueueStage.RUNNING);

      queue.jobStateChanged("123", ExecutionState.PENDING, null);
      assert QueueStage.fromStatusUpdateList(queue.getStatus(new Ticket("123"))).equals(QueueStage.RUNNING);
      mockObjects.verifyAndReset();
    }
    if(type.equals(LifeCycleType.CANCELLED_RUNNING)) {
      mockQueue.cancel("123");
      mockObjects.replay();
      queue.cancel(new Ticket("123"));
      mockObjects.verifyAndReset();
    }

    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    if(!type.equals(LifeCycleType.PREPROCESS_EXCEPTION) && !type.equals(LifeCycleType.SUBMISSION_EXCEPTION)
        && !type.equals(LifeCycleType.CANCELLED_PREPROCESSING)) {
      executor.execute(EasyMockUtils.record(runnableReference));
    }
    mockObjects.replay();

    boolean completedNormally = false;
    if(type.equals(LifeCycleType.FAILED)) {
      queue.jobStateChanged("123", ExecutionState.FAILED, null);
    } else if(type.equals(LifeCycleType.ABSENT)) {
      queue.jobStateChanged("123", ExecutionState.ABSENT, null);
    } else if(type.equals(LifeCycleType.PREPROCESS_EXCEPTION) || type.equals(LifeCycleType.SUBMISSION_EXCEPTION)
        || type.equals(LifeCycleType.CANCELLED_PREPROCESSING)) {
      completedNormally = false;
    } else {
      queue.jobStateChanged("123", ExecutionState.COMPLETE, null);
      completedNormally = true;
    }
    assert QueueStage.fromStatusUpdateList(queue.getStatus(new Ticket("123"))).equals(QueueStage.POSTPROCESSING);
    mockObjects.verifyAndReset();

    mockObjects.replay();
    IllegalStateException e = null;
    try {
      queue.cancel(new Ticket("123"));
    } catch(final IllegalStateException exception) {
      e = exception;
    }
    assert e != null;
    mockObjects.verifyAndReset();

    timer.schedule(EasyMockUtils.record(cleanupRunnable), eq(1000L));
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    jobProcessor.postprocess(completedNormally);
    if(type.equals(LifeCycleType.POSTPROCESS_EXCEPTION)) {
      expectLastCall().andThrow(new IllegalArgumentException());
      completedNormally = false;
    }
    listener.jobPostProcessed(new Ticket("123"), "test", jobProcessor, completedNormally);
    mockObjects.replay();
    runnableReference.get().run();

    final QueueStage stage = QueueStage.fromStatusUpdateList(queue.getStatus(new Ticket("123")));
    assert stage.equals(completedNormally ? QueueStage.POSTPROCESSED : QueueStage.FAILED) : stage;
    mockObjects.verifyAndReset();

    mockObjects.replay();
    e = null;
    try {
      queue.cancel(new Ticket("123"));
    } catch(final IllegalStateException exception) {
      e = exception;
    }
    assert e != null;
    mockObjects.verifyAndReset();

    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    mockObjects.replay();
    cleanupRunnable.get().run();
    assert QueueStage.fromStatusUpdateList(queue.getStatus(new Ticket("123"))).equals(QueueStage.ABSENT);
    mockObjects.verifyAndReset();

    mockObjects.replay();
    e = null;
    try {
      queue.cancel(new Ticket("123"));
    } catch(final IllegalStateException exception) {
      e = exception;
    }
    assert e != null;
    mockObjects.verifyAndReset();

  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void initFailure() {
    pausableStateTracker.registerPausableCallback(queue);
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    expect(mockQueue.listJobs()).andThrow(new RuntimeException());
    mockObjects.replay();
    queue.init();
  }

  public void init(final int numInitJobs, final int numActive, final boolean getJobProcessorException, final boolean mockInitializationTracker) {
    final ArrayList<ExecutionJobInfo<JobDescription>> jobInfoList = Lists.newArrayList();
    pausableStateTracker.registerPausableCallback(queue);
    InitializationTracker<Ticket> tracker = null;
    if(mockInitializationTracker) {
      tracker = EasyMock.createMock(InitializationTracker.class);
      mockObjects.add(tracker);
    }
    for(int i = 0; i < numInitJobs; i++) {
      final JobDescription jobDescription = EasyMock.createMock(JobDescription.class);
      EasyMock.expect(jobDescription.getTicket()).andStubReturn("" + i);
      EasyMock.expect(jobDescription.getJobType()).andStubReturn("test");
      mockObjects.add(jobDescription);
      jobInfoList.add(ExecutionJobInfo.create(jobDescription, i < numActive ? ExecutionState.RUNNING : ExecutionState.PENDING));
      @SuppressWarnings("unchecked")
      final JobProcessor<JobDescription> jobProcessor = EasyMock.createMock(JobProcessor.class);
      mockObjects.createMock(JobProcessor.class);
      jobProcessorRecoverer.recover(jobDescription);
      if(!getJobProcessorException) {
        expectLastCall().andReturn(jobProcessor);
      } else {
        expectLastCall().andThrow(new IllegalStateException());
        if(mockInitializationTracker) {
          tracker.fail(new Ticket("" + i));
        }
      }
    }
    mockQueue.init();
    expect(pausableStateTracker.isPaused()).andReturn(false).anyTimes();
    expect(mockQueue.listJobs()).andReturn(jobInfoList);
    mockObjects.replay();

    queue.init();

    mockObjects.verifyAndReset();

    for(int i = 0; i < numInitJobs; i++) {
      final Ticket ticket = new Ticket("" + i);
      final Status status = queue.getStatus(ticket);
      final QueueStage stage = QueueStage.fromStatusUpdateList(status);
      assert stage == (i < numActive ? QueueStage.RUNNING : QueueStage.PENDING) : stage + " " + i;
    }
  }

}
