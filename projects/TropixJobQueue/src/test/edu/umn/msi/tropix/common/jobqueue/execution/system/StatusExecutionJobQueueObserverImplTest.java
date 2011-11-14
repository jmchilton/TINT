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

import static edu.umn.msi.tropix.common.test.EasyMockUtils.verifyAndReset;
import static org.easymock.EasyMock.anyDouble;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.impl.StatusModifier;
import edu.umn.msi.tropix.common.jobqueue.status.EstimatedPendingTime;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusEntry;
import edu.umn.msi.tropix.common.jobqueue.status.StatusModifierManager;
import edu.umn.msi.tropix.common.jobqueue.status.StatusUtils;
import edu.umn.msi.tropix.common.prediction.Model;
import edu.umn.msi.tropix.common.time.TimeProvider;

public class StatusExecutionJobQueueObserverImplTest {
  private static StageEnumeration[] stages = new StageEnumeration[] {StageEnumeration.Complete, StageEnumeration.Pending, StageEnumeration.Failed, StageEnumeration.Absent};
  private static boolean addRandomEntry = false;

  private Status getNewStatus(final StageEnumeration stageEnumeration) {
    addRandomEntry = !addRandomEntry;
    return getNewStatus(stageEnumeration, addRandomEntry);
  }

  private Status getNewStatus(final StageEnumeration stageEnumeration, final boolean existingEntry) {
    final Stage stage = new Stage();
    stage.setValue(stageEnumeration);
    final Status status = new Status();
    status.setStage(stage);
    if(existingEntry) {
      status.setStatusEntry(new StatusEntry[] {EasyMock.createMock(StatusEntry.class)});
    }
    return status;
  }

  private final class JobImpl implements Job {
    private final String id;
    private final Long position;

    private JobImpl(final Long pos) {
      this("" + pos, pos);
    }

    private JobImpl(final String id, final Long position) {
      this.id = id;
      this.position = position;
    }

    public String getDescription() {
      return null;
    }

    public Long getPosition() {
      return position;
    }

    public Queue getQueue() {
      return null;
    }

    public String getTicket() {
      return id;
    }

  }

  class FixedModel implements Model {
    private Double value;

    FixedModel(final Double value) {
      this.value = value;
    }

    public void addData(final double[] xs, final double y) {
    }

    public Double predict(final double[] x) {
      return value;
    }
  }

  @Test(groups = "unit")
  public void nullOps() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    observer.onShutdown();
    observer.onStartup();
    observer.onSubmission(new JobDescriptionType());
  }
  
  private static class StaticTimeProvider implements TimeProvider {
    private long staticTime = 1L;
    
    public long currentTimeMillis() {
      return staticTime;
    }
    
  }

  @Test(groups = "unit")
  public void timing() {
    final StaticTimeProvider staticTimeProvider = new StaticTimeProvider();
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    final StatusModifierManager statusModifierManager = EasyMock.createMock(StatusModifierManager.class);
    observer.setStatusModifierManager(statusModifierManager);
    observer.setTimeProvider(staticTimeProvider);
    
    final long minute = 1000 * 60;
    observer.setExecutionTimeModel(new FixedModel(1.0 * minute));
    statusModifierManager.registerStatusModifier("0", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("1", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("2", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("3", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("4", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("5", observer);
    EasyMock.expectLastCall().anyTimes();
    EasyMock.replay(statusModifierManager);

    staticTimeProvider.staticTime = 1 * minute;
    observer.onExecutionStateChange(new JobImpl(0L), ExecutionState.RUNNING, null);
    observer.onExecutionStateChange(new JobImpl(1L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(2L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(3L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(4L), ExecutionState.PENDING, null);
    for(int i = 1; i <= 4; i++) {
      staticTimeProvider.staticTime = 1 * minute;
      final Long pendingTime = getPendingTime(observer, i);
      assert pendingTime == minute * i : pendingTime;
    }
    for(int i = 1; i <= 4; i++) {
      staticTimeProvider.staticTime = 1 * minute + minute / 2;
      final Long pendingTime = getPendingTime(observer, i);
      assert pendingTime == (minute * i) - minute / 2 : pendingTime;
    }
    staticTimeProvider.staticTime = 2 * minute;
    observer.onExecutionStateChange(new JobImpl(0L), ExecutionState.COMPLETE, null);
    for(int i = 1; i <= 4; i++) {
      final Long pendingTime = getPendingTime(observer, i);
      assert (i - 1) * minute == pendingTime : pendingTime;
    }

    staticTimeProvider.staticTime = 3 * minute;
    observer.onExecutionStateChange(new JobImpl(5L), ExecutionState.RUNNING, null);
    for(int i = 1; i <= 4; i++) {
      final Long pendingTime = getPendingTime(observer, i);
      //assert (i - 1) * minute <= pendingTime : pendingTime;
      assert pendingTime == i * minute : pendingTime;
    }

    staticTimeProvider.staticTime = 4 * minute;
    observer.onExecutionStateChange(new JobImpl(5L), ExecutionState.COMPLETE, null);
    observer.onExecutionStateChange(new JobImpl(1L), ExecutionState.RUNNING, null);
    for(int i = 1; i <= 3; i++) {
      final Long pendingTime = getPendingTime(observer, i + 1);
      assert i * minute == pendingTime : pendingTime;
    }
    staticTimeProvider.staticTime = 5 * minute;
    observer.onExecutionStateChange(new JobImpl(1L), ExecutionState.COMPLETE, null);
    for(int i = 1; i <= 3; i++) {
      final Long pendingTime = getPendingTime(observer, i + 1);
      assert (i - 1) * minute == pendingTime : pendingTime;
    }
    EasyMock.verify(statusModifierManager);

  }

  @Test(groups = "unit")
  public void outOfOrder() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    final StatusModifierManager statusModifierManager = EasyMock.createMock(StatusModifierManager.class);
    observer.setStatusModifierManager(statusModifierManager);

    statusModifierManager.registerStatusModifier("0", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("1", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("2", observer);
    EasyMock.expectLastCall().anyTimes();
    statusModifierManager.registerStatusModifier("3", observer);
    EasyMock.expectLastCall().anyTimes();
    EasyMock.replay(statusModifierManager);

    final long minute = 1000 * 60;
    observer.setExecutionTimeModel(new FixedModel(1.0 * minute));
    observer.onExecutionStateChange(new JobImpl(0L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(1L), ExecutionState.COMPLETE, null);
    observer.onExecutionStateChange(new JobImpl(1L), ExecutionState.PENDING, null);
    // Tests out of order pending does not affect position of existing jobs
    assert getSize(observer, 0) == 1;

    observer.onExecutionStateChange(new JobImpl(2L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(3L), ExecutionState.COMPLETE, null);
    observer.onExecutionStateChange(new JobImpl(3L), ExecutionState.RUNNING, null);
    final Long pendingTime = getPendingTime(observer, 2);
    // Tests out of order running does not affect wait time of pending jobs (i.e. it doesn't think its pending).
    assert pendingTime != null && Math.abs(minute - pendingTime) < 10 : pendingTime;

    EasyMock.verify(statusModifierManager);

  }

  private class NullStatusModifierManager implements StatusModifierManager {

    public void registerStatusModifier(final String jobId, final StatusModifier statusModifier) {
    }

    public void unregisterStatusModifier(final String jobId, final StatusModifier statusModifier) {
    }

    public void extendStatus(final String localJobId, final Status status) {
    }
  }

  @Test(groups = "unit")
  public void position() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    observer.setStatusModifierManager(new NullStatusModifierManager());

    final Job[] jobs = new Job[20];
    for(int i = 0; i < 20; i++) {
      jobs[i] = new JobImpl("" + i, (long) i);
    }
    observer.onExecutionStateChange(jobs[0], ExecutionState.PENDING, null);
    observer.onExecutionStateChange(jobs[1], ExecutionState.PENDING, null);
    observer.onExecutionStateChange(jobs[2], ExecutionState.PENDING, null);
    observer.onExecutionStateChange(jobs[3], ExecutionState.PENDING, null);
    assert getPosition(observer, 0) == 1;
    assert getPosition(observer, 1) == 2 : getPosition(observer, 1);
    assert getPosition(observer, 2) == 3;
    assert getPosition(observer, 3) == 4;
    assert getSize(observer, 0) == 4 : getSize(observer, 0);
    assert getSize(observer, 1) == 4 : getSize(observer, 1);
    assert getSize(observer, 3) == 4 : getSize(observer, 3);
    observer.onExecutionStateChange(jobs[0], ExecutionState.COMPLETE, null);
    assert getPosition(observer, 1) == 1;
    assert getPosition(observer, 2) == 2;
    assert getPosition(observer, 3) == 3;
    observer.onExecutionStateChange(jobs[1], ExecutionState.FAILED, null);
    assert getPosition(observer, 2) == 1;
    assert getPosition(observer, 3) == 2;
    assert getSize(observer, 3) == 2;
    observer.onExecutionStateChange(jobs[2], ExecutionState.RUNNING, null);
    assert getPosition(observer, 3) == 1;
    assert getSize(observer, 3) == 1;
    observer.onExecutionStateChange(jobs[5], ExecutionState.PENDING, null);
    observer.onExecutionStateChange(jobs[4], ExecutionState.PENDING, null);
    assert getPosition(observer, 3) == 1;
    assert getPosition(observer, 4) == 2;
    assert getPosition(observer, 5) == 3;
    observer.onExecutionStateChange(jobs[4], ExecutionState.FAILED, null);
    assert getPosition(observer, 3) == 1;
    assert getPosition(observer, 5) == 2;
  }

  @Test(groups = "unit")
  public void modelPredictCorrectNumberOfTimes() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    final StatusModifierManager statusModifierManager = EasyMock.createMock(StatusModifierManager.class);
    observer.setStatusModifierManager(statusModifierManager);

    final Model model = createMock(Model.class);
    expect(model.predict(isA(double[].class))).andReturn(4.0).times(9);

    statusModifierManager.registerStatusModifier("0", observer);
    statusModifierManager.registerStatusModifier("1", observer);
    statusModifierManager.registerStatusModifier("2", observer);
    statusModifierManager.registerStatusModifier("3", observer);
    statusModifierManager.registerStatusModifier("4", observer);
    statusModifierManager.registerStatusModifier("5", observer);
    statusModifierManager.registerStatusModifier("6", observer);
    statusModifierManager.registerStatusModifier("7", observer);
    statusModifierManager.registerStatusModifier("8", observer);
    replay(statusModifierManager);
    replay(model);

    observer.setExecutionTimeModel(model);
    observer.onExecutionStateChange(new JobImpl(0L), ExecutionState.RUNNING, null);
    observer.onExecutionStateChange(new JobImpl(1L), ExecutionState.RUNNING, null);
    observer.onExecutionStateChange(new JobImpl(2L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(3L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(4L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(5L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(6L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(7L), ExecutionState.PENDING, null);
    observer.onExecutionStateChange(new JobImpl(8L), ExecutionState.PENDING, null);

    verifyAndReset(model, statusModifierManager);
    statusModifierManager.unregisterStatusModifier("5", observer);
    replay(model, statusModifierManager);
    // Eject 5, make sure no recalculation...
    observer.onExecutionStateChange(new JobImpl(5L), ExecutionState.ABSENT, null);
    verifyAndReset(model, statusModifierManager);
    replay(model);
    // Eject 4 make sure no recalculation
    observer.onExecutionStateChange(new JobImpl(4L), ExecutionState.FAILED, null);
    verifyAndReset(model);
    replay(model);
    // Eject 3 make sure no recalculation
    observer.onExecutionStateChange(new JobImpl(3L), ExecutionState.COMPLETE, null);
    verifyAndReset(model);

    expect(model.predict(isA(double[].class))).andReturn(5.0).times(6);
    model.addData(isA(double[].class), anyDouble());
    replay(model);
    observer.onExecutionStateChange(new JobImpl(0L), ExecutionState.COMPLETE, null);
  }

  @Test(groups = "unit")
  public void modelPredictAndAddAtRightSteps() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    final StatusModifierManager statusModifierManager = EasyMock.createMock(StatusModifierManager.class);
    observer.setStatusModifierManager(statusModifierManager);

    final Model model = createMock(Model.class);
    observer.setExecutionTimeModel(model);
    Job job = new JobImpl("0", 0L);
    expect(model.predict(isA(double[].class))).andReturn(5.0);
    statusModifierManager.registerStatusModifier("0", observer);
    replay(model, statusModifierManager);
    // 0) Starting 0 in pending state, should predict at this step
    observer.onExecutionStateChange(job, ExecutionState.PENDING, null);
    verifyAndReset(model, statusModifierManager);
    replay(model, statusModifierManager);
    // 0) Switch 0 to running state, no need to recalculate prediction
    observer.onExecutionStateChange(job, ExecutionState.RUNNING, null);
    verifyAndReset(model, statusModifierManager);
    model.addData(isA(double[].class), EasyMock.anyDouble());
    replay(model, statusModifierManager);
    // 0) Completing 0 properly, should get addData
    observer.onExecutionStateChange(job, ExecutionState.COMPLETE, null);
    verifyAndReset(model, statusModifierManager);
    // 1) Start with RUNNING, should predict
    job = new JobImpl("1", 1L);
    statusModifierManager.registerStatusModifier("1", observer);
    expect(model.predict(isA(double[].class))).andReturn(5.0);
    replay(model, statusModifierManager);
    observer.onExecutionStateChange(job, ExecutionState.RUNNING, null);
    verifyAndReset(model, statusModifierManager);
    model.addData(isA(double[].class), EasyMock.anyDouble());
    replay(model, statusModifierManager);
    // 1) Completing 1 properly, should get addData
    observer.onExecutionStateChange(job, ExecutionState.COMPLETE, null);
    verifyAndReset(model, statusModifierManager);
    expect(model.predict(isA(double[].class))).andReturn(null);
    statusModifierManager.registerStatusModifier("2", observer);
    replay(model, statusModifierManager);
    job = new JobImpl("2", 2L);
    // 2) Starting 2 in pending, should predict
    observer.onExecutionStateChange(job, ExecutionState.PENDING, null);
    verifyAndReset(model, statusModifierManager);
    replay(model, statusModifierManager);
    // 2) Finishing 2 with a failed, should be no predict or add data
    job = new JobImpl("2", 2L);
    observer.onExecutionStateChange(job, ExecutionState.FAILED, null);
    verifyAndReset(model, statusModifierManager);
    statusModifierManager.registerStatusModifier("3", observer);
    replay(model, statusModifierManager);
    job = new JobImpl("3", 3L);
    // 3) Starting 3 in completed state, should get not predict or add
    observer.onExecutionStateChange(job, ExecutionState.COMPLETE, null);
    verifyAndReset(model, statusModifierManager);
    // 4) Starting 4 in failed state should get no predict or add data
    statusModifierManager.registerStatusModifier("4", observer);
    replay(model, statusModifierManager);
    job = new JobImpl("4", 4L);
    observer.onExecutionStateChange(job, ExecutionState.FAILED, null);
    verifyAndReset(model, statusModifierManager);
    statusModifierManager.registerStatusModifier("5", observer);
    statusModifierManager.unregisterStatusModifier("5", observer);
    replay(model, statusModifierManager);
    job = new JobImpl("5", 5L);
    // 5) Starting 5 in absent state should get not predict or add data
    observer.onExecutionStateChange(job, ExecutionState.ABSENT, null);
  }

  private int getPosition(final StatusExecutionJobQueueObserverImpl observer, final int i) {
    final Status status = getNewStatus(StageEnumeration.Pending);
    observer.extendStatus("" + i, status);
    return status.getQueuePosition().getValue();
  }

  private int getSize(final StatusExecutionJobQueueObserverImpl observer, final int i) {
    final Status status = getNewStatus(StageEnumeration.Pending);
    observer.extendStatus("" + i, status);
    return status.getQueuePosition().getQueueSize();
  }

  private Long getPendingTime(final StatusExecutionJobQueueObserverImpl observer, final int i) {
    final Status status = getNewStatus(StageEnumeration.Pending);
    observer.extendStatus("" + i, status);
    final EstimatedPendingTime entry = StatusUtils.getStatusEntry(status, EstimatedPendingTime.class);
    return entry == null ? null : entry.getValue();
  }

  // Tests that unitialized tickets don't cause problems...
  @Test(groups = "unit")
  public void unititialized() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    for(final StageEnumeration stage : stages) {
      final Status status = getNewStatus(stage);
      observer.extendStatus("123", status);
      assert status.getQueuePosition() == null;
    }
  }

  // Check most combinations of previous update and status to make sure none
  // cause null pointer exceptions or other such runtime exceptions.
  @Test(groups = "unit")
  public void checkProblemCombinations() {
    final StatusExecutionJobQueueObserverImpl observer = new StatusExecutionJobQueueObserverImpl();
    final StatusModifierManager statusModifierManager = EasyMock.createMock(StatusModifierManager.class);
    observer.setStatusModifierManager(statusModifierManager);

    long i = 0;
    for(final ExecutionState state : ExecutionState.values()) {
      for(final StageEnumeration stageEnumeration : stages) {
        observer.onExecutionStateChange(new JobImpl("" + i++, i), state, null);
        observer.extendStatus("" + i, getNewStatus(stageEnumeration));
      }
    }
  }

}
