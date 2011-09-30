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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJobService;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramStateExecutionJobQueueObserverImpl;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class GramStateExecutionJobQueueObserverImplTest {

  @Test(groups = "unit")
  public void done() {
    for(final ExecutionState doneState : Arrays.asList(ExecutionState.ABSENT, ExecutionState.COMPLETE, ExecutionState.FAILED)) {
      final GramStateExecutionJobQueueObserverImpl obs = new GramStateExecutionJobQueueObserverImpl();
      final GramExecutionJobService gramExecutionJobService = EasyMock.createMock(GramExecutionJobService.class);
      obs.setGramExecutionJobService(gramExecutionJobService);
      final GramExecutionJob job = EasyMock.createMock(GramExecutionJob.class);
      final String localJobId = "12345";
      EasyMock.expect(job.getTicket()).andReturn(localJobId);
      gramExecutionJobService.delete(localJobId);
      EasyMockUtils.replayAll(gramExecutionJobService, job);
      obs.onExecutionStateChange(job, doneState, null);
      EasyMockUtils.verifyAndResetAll(gramExecutionJobService, job);
    }
  }

  @Test(groups = "unit")
  public void notDone() {
    for(final ExecutionState notDoneState : Arrays.asList(ExecutionState.PENDING, ExecutionState.RUNNING)) {
      final GramStateExecutionJobQueueObserverImpl obs = new GramStateExecutionJobQueueObserverImpl();
      final GramExecutionJobService gramExecutionJobService = EasyMock.createMock(GramExecutionJobService.class);
      obs.setGramExecutionJobService(gramExecutionJobService);
      final GramExecutionJob job = EasyMock.createMock(GramExecutionJob.class);
      final String localJobId = "12345";
      EasyMock.expect(job.getTicket()).andReturn(localJobId);
      gramExecutionJobService.updateState(localJobId, notDoneState.toString());
      EasyMockUtils.replayAll(gramExecutionJobService, job);
      obs.onExecutionStateChange(job, notDoneState, null);
      EasyMockUtils.verifyAndResetAll(gramExecutionJobService, job);
    }
  }

}
