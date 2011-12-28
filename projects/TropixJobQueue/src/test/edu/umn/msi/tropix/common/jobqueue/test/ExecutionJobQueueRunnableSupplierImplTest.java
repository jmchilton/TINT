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

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionFactory;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueRunnable;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueRunnableCallback;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueRunnableSupplierImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

public class ExecutionJobQueueRunnableSupplierImplTest {
  private ExecutionJobQueueRunnableSupplierImpl supplier;
  private ExecutionFactory executionFactory;
  private Function<JobDescriptionType, ExecutionConfiguration> jobDescriptionFunction;
  private ExecutionJobQueueRunnableCallback callback;
  private Job job;
  private ExecutionJobQueueRunnable runnable;
  private ExecutionHook hook;
  private Collection<Object> mockObjects;

  @BeforeMethod(groups = "unit")
  public void init() {
    supplier = new ExecutionJobQueueRunnableSupplierImpl();
    executionFactory = EasyMock.createMock(ExecutionFactory.class);
    jobDescriptionFunction = EasyMockUtils.createMockFunction();
    supplier.setExecutionFactory(executionFactory);
    supplier.setJobDescriptionFunction(jobDescriptionFunction);

    callback = EasyMock.createMock(ExecutionJobQueueRunnableCallback.class);
    job = EasyMock.createMock(Job.class);

    runnable = supplier.get();
    runnable.setExecutionRunnableCallback(callback);
    runnable.setJob(job);

    hook = EasyMock.createMock(ExecutionHook.class);
    mockObjects = Arrays.asList(executionFactory, jobDescriptionFunction, callback, job, hook);
  }

  private void replay() {
    EasyMockUtils.replayAll(mockObjects.toArray());
  }

  private void verify() {
    EasyMockUtils.verifyAll(mockObjects.toArray());
  }

  @Test(groups = "unit")
  public void setupFailed() {
    EasyMock.expect(job.getTicket()).andReturn("123").anyTimes();
    EasyMock.expect(job.getDescription()).andReturn(JobDescriptionUtils.serialize(new JobDescriptionType()));
    jobDescriptionFunction.apply(EasyMock.isA(JobDescriptionType.class));
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    callback.onComplete(ExecutionState.FAILED);
    replay();
    runnable.run();
    verify();
  }

  @Test(groups = "unit")
  public void executionFailed() {
    EasyMock.expect(job.getTicket()).andReturn("123").anyTimes();
    EasyMock.expect(job.getDescription()).andReturn(JobDescriptionUtils.serialize(new JobDescriptionType()));
    jobDescriptionFunction.apply(EasyMock.isA(JobDescriptionType.class));
    final ExecutionConfiguration config = new ExecutionConfiguration();
    EasyMock.expectLastCall().andReturn(config);
    executionFactory.execute(EasyMock.same(config), EasyMock.isA(ExecutionCallback.class));
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    callback.onComplete(ExecutionState.FAILED);
    replay();
    runnable.run();
    verify();
  }

  enum Type {
    KILLED, EXCEPTION, NORMAL
  }

  @Test(groups = "unit")
  public void killed() {
    callback(Type.KILLED);
  }

  @Test(groups = "unit")
  public void executionException() {
    callback(Type.EXCEPTION);
  }

  @Test(groups = "unit")
  public void completeNormally() {
    callback(Type.NORMAL);
  }

  public void callback(final Type type) {
    EasyMock.expect(job.getTicket()).andReturn("123").anyTimes();
    EasyMock.expect(job.getDescription()).andReturn(JobDescriptionUtils.serialize(new JobDescriptionType()));
    jobDescriptionFunction.apply(EasyMock.isA(JobDescriptionType.class));
    final ExecutionConfiguration config = new ExecutionConfiguration();
    EasyMock.expectLastCall().andReturn(config);
    final Reference<ExecutionCallback> eCallbackReference = EasyMockUtils.newReference();
    executionFactory.execute(EasyMock.same(config), EasyMockUtils.record(eCallbackReference));
    EasyMock.expectLastCall().andReturn(hook);
    callback.onRunning(hook);
    EasyMock.expect(hook.getKilled()).andReturn(type.equals(Type.KILLED)).anyTimes();
    EasyMock.expect(hook.getException()).andReturn(type.equals(Type.EXCEPTION) ? new IllegalStateException() : null).anyTimes();
    EasyMock.expect(hook.getTimedOut()).andReturn(false).anyTimes();
    switch(type) {
    case EXCEPTION:
      callback.onComplete(ExecutionState.FAILED);
      break;
    case NORMAL:
      callback.onComplete(ExecutionState.COMPLETE);
      break;
    case KILLED:
      callback.onComplete(ExecutionState.FAILED);
    }
    replay();
    runnable.run();
    eCallbackReference.get().onCompletion(hook);
    verify();

  }

}
