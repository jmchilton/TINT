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
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueLauncherImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueRunnable;
import edu.umn.msi.tropix.common.jobqueue.execution.system.ExecutionJobQueueRunnableCallback;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;

public class ExecutionJobQueueLauncherImplTest {

  @Test(groups = "unit")
  public void launch() {
    final ExecutionJobQueueLauncherImpl launcher = new ExecutionJobQueueLauncherImpl();
    final ExecutionJobQueueRunnable runnable = EasyMock.createMock(ExecutionJobQueueRunnable.class);
    final Executor executor = EasyMock.createMock(Executor.class);
    launcher.setExecutionRunnableSupplier(Suppliers.ofInstance(runnable));
    launcher.setExecutor(executor);

    final ExecutionJobQueueRunnableCallback callback = EasyMock.createMock(ExecutionJobQueueRunnableCallback.class);
    final Job job = EasyMock.createMock(Job.class);
    runnable.setExecutionRunnableCallback(EasyMock.same(callback));
    runnable.setJob(EasyMock.same(job));
    executor.execute(EasyMock.same(runnable));
    EasyMock.replay(runnable, executor);
    launcher.launch(callback, job);
    EasyMock.verify(runnable, executor);
  }

}
