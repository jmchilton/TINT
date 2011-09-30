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

import java.util.concurrent.Executor;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Haltable;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;

public class GramJobPollerImpl implements GramJobPoller {
  private GramExecutionStateResolver gramStateResolver;
  private ExecutionJobQueueObserver<GramExecutionJob> executionJobQueueObserver;
  private Supplier<LoopingRunnable> loopingRunnableSupplier;
  private Executor executor;

  public void poll(final GramExecutionJob job) {
    final PollingRunnable runnable = new PollingRunnable();
    final LoopingRunnable loopingRunnable = loopingRunnableSupplier.get();
    runnable.loopingRunnable = loopingRunnable;
    runnable.job = job;
    loopingRunnable.setBaseRunnable(runnable);
    executor.execute(loopingRunnable);
  }

  class PollingRunnable implements Runnable {
    private GramExecutionJob job;
    private Haltable loopingRunnable;

    public void run() {
      final ExecutionState executionState = gramStateResolver.getState(job);

      // Update job state
      executionJobQueueObserver.onExecutionStateChange(job, executionState, null);

      // Stop looping when execution state is complete
      if(executionState.isDone()) {
        loopingRunnable.shutdown();
      }
    }

    public String toString() {
      return "Polling Runnable for Gram Job " + job.toString();
    }
  }

  public void setExecutionJobQueueObserver(final ExecutionJobQueueObserver<GramExecutionJob> executionJobQueueObserver) {
    this.executionJobQueueObserver = executionJobQueueObserver;
  }

  public void setGramStateResolver(final GramExecutionStateResolver gramStateResolver) {
    this.gramStateResolver = gramStateResolver;
  }

  public void setLoopingRunnableSupplier(final Supplier<LoopingRunnable> loopingRunnableSupplier) {
    this.loopingRunnableSupplier = loopingRunnableSupplier;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

}
