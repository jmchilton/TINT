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

package edu.umn.msi.tropix.common.jobqueue.execution;

public class ExecutionStateChangeObserverImpl<T extends ExecutionJob> extends BaseExecutionJobQueueObserverImpl<T> implements ExecutionStateChangeObserver<T> {
  private ExecutionStateChangeListener executionStateChangeListener;

  public void setExecutionStateChangeListener(final ExecutionStateChangeListener executionStateChangeListener) {
    this.executionStateChangeListener = executionStateChangeListener;
  }

  @Override
  public void onExecutionStateChange(final T job, final ExecutionState executionState, final Throwable throwable) {
    executionStateChangeListener.jobStateChanged(job.getTicket(), executionState, throwable);
  }

}
