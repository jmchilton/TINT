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

import org.apache.axis.AxisFault;
import org.easymock.EasyMock;
import org.globus.exec.generated.StateEnumeration;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionStateResolverImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobResolver;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class GramExecutionStateResolverImplTest {
  private GramExecutionStateResolverImpl resolver;
  private GramJobResolver gramJobResolver;

  @BeforeTest(groups = "unit")
  public void init() {
    resolver = new GramExecutionStateResolverImpl();
    gramJobResolver = EasyMock.createMock(GramJobResolver.class);
    resolver.setGramJobResolver(gramJobResolver);
  }

  @Test(groups = "unit")
  public void states() {
    testState(StateEnumeration.Active, ExecutionState.RUNNING);
    testState(StateEnumeration.CleanUp, ExecutionState.RUNNING);
    testState(StateEnumeration.Done, ExecutionState.COMPLETE);
    testState(StateEnumeration.Failed, ExecutionState.FAILED);
    testState(StateEnumeration.Pending, ExecutionState.PENDING);
    testState(StateEnumeration.StageIn, ExecutionState.PENDING);
    testState(StateEnumeration.StageOut, ExecutionState.RUNNING);
    testState(StateEnumeration.Suspended, ExecutionState.RUNNING);
  }

  public void testState(final StateEnumeration enumeration, final ExecutionState state) {
    final GramExecutionJob eJob = EasyMock.createMock(GramExecutionJob.class);
    final GramJob gramJob = EasyMock.createMock(GramJob.class);
    gramJob.refreshStatus();
    EasyMock.expect(gramJobResolver.getGramJob(eJob)).andReturn(gramJob);
    EasyMock.expect(gramJob.getState()).andReturn(enumeration);
    EasyMockUtils.replayAll(gramJob, eJob, gramJobResolver);
    assert resolver.getState(eJob).equals(state);
    EasyMockUtils.verifyAndResetAll(gramJob, eJob, gramJobResolver);

  }

  @Test(groups = "unit")
  public void resourcePropertyNotFound() {
    final GramExecutionJob eJob = EasyMock.createMock(GramExecutionJob.class);
    final GramJob gramJob = EasyMock.createMock(GramJob.class);
    gramJob.refreshStatus();
    EasyMock.expectLastCall().andThrow(new GramJob.GramJobNotFoundException(null));
    EasyMock.expect(gramJobResolver.getGramJob(eJob)).andReturn(gramJob);
    EasyMockUtils.replayAll(gramJob, eJob, gramJobResolver);
    assert resolver.getState(eJob).equals(ExecutionState.ABSENT);
    EasyMockUtils.verifyAndResetAll(gramJob, eJob, gramJobResolver);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void axisFault() {
    final GramExecutionJob eJob = EasyMock.createMock(GramExecutionJob.class);
    final GramJob gramJob = EasyMock.createMock(GramJob.class);
    gramJob.refreshStatus();
    EasyMock.expectLastCall().andThrow(new AxisFault());
    EasyMock.expect(gramJobResolver.getGramJob(eJob)).andReturn(gramJob);
    EasyMockUtils.replayAll(gramJob, eJob, gramJobResolver);
    try {
      resolver.getState(eJob);
    } finally {
      EasyMockUtils.resetAll(gramJob, eJob, gramJobResolver);
    }
  }

}
