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

package edu.umn.msi.tropix.common.jobqueue.execution.inprocess;

import java.util.concurrent.Executor;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionStateChangeListener;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class InProcessExecutionJobQueueImplTest {
  private ExecutionStateChangeListener listener;
  private Executor executor;
  private InProcessExecutionJobQueueImpl queue;
  private Capture<Runnable> runnableCapture;
  private MockObjectCollection mockObjects;

  private Thread startThread() {
    final Thread thread = new Thread(runnableCapture.getValue());
    thread.start();
    return thread;
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    listener = EasyMock.createMock(ExecutionStateChangeListener.class);
    executor = EasyMock.createMock(Executor.class);
    runnableCapture = EasyMockUtils.newCapture();

    queue = new InProcessExecutionJobQueueImpl();
    queue.setExecutor(executor);
    queue.setExecutionStateChangeListener(listener);

    mockObjects = MockObjectCollection.fromObjects(listener, executor);
  }

  @Test(groups = "unit")
  public void lifeCycle() throws Exception {
    executor.execute(EasyMock.capture(runnableCapture));
    mockObjects.replay();
    queue.init();
    mockObjects.verifyAndReset();

    final Thread thread = startThread();
    Thread.sleep(2000);
    queue.destroy();
    thread.join();
    assert !thread.isAlive();
  }

  @Test(groups = "unit")
  public void survivesInterrupts() throws Exception {
    executor.execute(EasyMock.capture(runnableCapture));
    mockObjects.replay();
    queue.init();
    mockObjects.verifyAndReset();

    final Thread thread = startThread();
    Thread.sleep(100);

    thread.interrupt();
    thread.join(100);
    assert thread.isAlive();

    queue.destroy();
    thread.join();
    assert !thread.isAlive();
  }

  @Test(groups = "unit")
  public void completedProperly() throws InterruptedException {
    submit(true);
  }

  @Test(groups = "unit")
  public void failedJob() throws InterruptedException {
    submit(false);
  }

  @Test(groups = "unit")
  public void unimplementedMethods() {
    // Just make sure they don't throw exceptions for now...
    queue.cancel("12345");
    assert queue.listJobs().isEmpty();
  }

  private void submit(final boolean completedProperly) throws InterruptedException {
    executor.execute(EasyMock.capture(runnableCapture));
    mockObjects.replay();
    queue.init();
    mockObjects.verifyAndReset();

    final InProcessJobDescription description = EasyMock.createMock(InProcessJobDescription.class);
    EasyMock.expect(description.getTicket()).andStubReturn("ticket");
    listener.jobStateChanged("ticket", ExecutionState.RUNNING, null);
    description.execute();
    if(completedProperly) {
      listener.jobStateChanged("ticket", ExecutionState.COMPLETE, null);
    } else {
      final RuntimeException e = new RuntimeException();
      EasyMock.expectLastCall().andThrow(e);
      listener.jobStateChanged("ticket", ExecutionState.FAILED, e);
    }
    mockObjects.add(description);
    mockObjects.replay();
    queue.submitJob(description);
    queue.destroy();
    runnableCapture.getValue().run();
    mockObjects.verifyAndReset();
  }

}
