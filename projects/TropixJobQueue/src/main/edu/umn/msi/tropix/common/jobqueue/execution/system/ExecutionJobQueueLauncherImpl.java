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

import java.util.concurrent.Executor;

import com.google.common.base.Supplier;

public class ExecutionJobQueueLauncherImpl implements ExecutionJobQueueLauncher {
  private Supplier<ExecutionJobQueueRunnable> executionRunnableSupplier;
  private Executor executor;

  public void launch(final ExecutionJobQueueRunnableCallback callback, final Job job) {
    final ExecutionJobQueueRunnable executionJobQueueRunnable = executionRunnableSupplier.get();
    executionJobQueueRunnable.setExecutionRunnableCallback(callback);
    executionJobQueueRunnable.setJob(job);
    executor.execute(executionJobQueueRunnable);
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

  public void setExecutionRunnableSupplier(final Supplier<ExecutionJobQueueRunnable> executionRunnableSupplier) {
    this.executionRunnableSupplier = executionRunnableSupplier;
  }
}
