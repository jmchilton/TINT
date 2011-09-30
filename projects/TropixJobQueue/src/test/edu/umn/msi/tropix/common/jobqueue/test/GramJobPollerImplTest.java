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

import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionStateResolver;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobPollerImpl;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class GramJobPollerImplTest {
  private GramJobPollerImpl poller;
  private Executor executor;
  private Supplier<LoopingRunnable> loopingRunnableSupplier;
  private MockObjectCollection mockObjects;
  private ExecutionJobQueueObserver<GramExecutionJob> executionJobQueueObserver;
  private GramExecutionStateResolver gramExecutionStateResolver;
  private Reference<Runnable> runnableReference;
  private LoopingRunnable loopingRunnable;

  @BeforeTest(alwaysRun = true, groups = "unit")
  public void init() {
    poller = new GramJobPollerImpl();
    executor = EasyMock.createMock(Executor.class);
    System.out.println("In init");
    loopingRunnableSupplier = EasyMockUtils.createMockSupplier();
    loopingRunnable = EasyMock.createMock(LoopingRunnable.class);
    executionJobQueueObserver = EasyMock.createMock(ExecutionJobQueueObserver.class);
    gramExecutionStateResolver = EasyMock.createMock(GramExecutionStateResolver.class);

    poller.setExecutor(executor);
    poller.setLoopingRunnableSupplier(loopingRunnableSupplier);
    poller.setExecutionJobQueueObserver(executionJobQueueObserver);
    poller.setGramStateResolver(gramExecutionStateResolver);

    mockObjects = MockObjectCollection.fromObjects(executor, loopingRunnableSupplier, executionJobQueueObserver, gramExecutionStateResolver,
        loopingRunnable);

  }

  @Test(groups = "unit")
  public void running() {
    testState(ExecutionState.RUNNING);
  }

  @Test(groups = "unit")
  public void pending() {
    testState(ExecutionState.PENDING);
  }

  @Test(groups = "unit")
  public void complete() {
    testState(ExecutionState.COMPLETE);
  }

  @Test(groups = "unit")
  public void failed() {
    testState(ExecutionState.FAILED);
  }

  @Test(groups = "unit")
  public void absent() {
    testState(ExecutionState.ABSENT);
  }

  @Test(groups = "unit")
  public void testToString() {
    runnableReference = EasyMockUtils.newReference();
    EasyMock.expect(loopingRunnableSupplier.get()).andReturn(loopingRunnable);
    loopingRunnable.setBaseRunnable(EasyMockUtils.record(runnableReference));
    executor.execute(loopingRunnable);
    final GramExecutionJob job = EasyMock.createMock(GramExecutionJob.class);
    mockObjects.replay();
    poller.poll(job);
    runnableReference.get().toString();
    mockObjects.verifyAndReset();

  }

  private void testState(final ExecutionState state) {
    runnableReference = EasyMockUtils.newReference();
    EasyMock.expect(loopingRunnableSupplier.get()).andReturn(loopingRunnable);
    loopingRunnable.setBaseRunnable(EasyMockUtils.record(runnableReference));
    executor.execute(loopingRunnable);
    final GramExecutionJob job = EasyMock.createMock(GramExecutionJob.class);
    EasyMock.expect(gramExecutionStateResolver.getState(EasyMock.same(job))).andReturn(state);
    executionJobQueueObserver.onExecutionStateChange(EasyMock.same(job), EasyMock.same(state), (Throwable) EasyMock.isNull());
    if(state.isDone()) {
      loopingRunnable.shutdown();
    }
    mockObjects.replay();
    poller.poll(job);
    runnableReference.get().run();
    mockObjects.verifyAndReset();
  }

}
