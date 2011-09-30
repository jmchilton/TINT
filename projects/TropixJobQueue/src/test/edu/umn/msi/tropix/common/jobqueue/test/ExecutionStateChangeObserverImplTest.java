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
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionStateChangeListener;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionStateChangeObserverImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;

public class ExecutionStateChangeObserverImplTest {

  @Test(groups = "unit")
  public void translation() {
    final ExecutionStateChangeObserverImpl<Job> observer = new ExecutionStateChangeObserverImpl<Job>();
    final ExecutionStateChangeListener listener = createStrictMock(ExecutionStateChangeListener.class);
    listener.jobStateChanged("1", ExecutionState.PENDING, null);
    listener.jobStateChanged("1", ExecutionState.RUNNING, null);
    listener.jobStateChanged("1", ExecutionState.COMPLETE, null);
    listener.jobStateChanged("1", ExecutionState.FAILED, null);
    listener.jobStateChanged("1", ExecutionState.ABSENT, null);
    replay(listener);

    final Job job = createMock(Job.class);
    expect(job.getTicket()).andReturn("1").anyTimes();
    replay(job);

    observer.setExecutionStateChangeListener(listener);
    observer.onExecutionStateChange(job, ExecutionState.PENDING, null);
    observer.onExecutionStateChange(job, ExecutionState.RUNNING, null);
    observer.onExecutionStateChange(job, ExecutionState.COMPLETE, null);
    observer.onExecutionStateChange(job, ExecutionState.FAILED, null);
    observer.onExecutionStateChange(job, ExecutionState.ABSENT, null);
    verify(listener);
  }

  @Test(groups = "unit")
  public void nullOps() {
    final ExecutionStateChangeObserverImpl<Job> observer = new ExecutionStateChangeObserverImpl<Job>();
    observer.onShutdown();
    observer.onStartup();
    observer.onSubmission(null);
  }
}
