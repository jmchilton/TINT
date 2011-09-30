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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramExecutionJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJob;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobFactory;
import edu.umn.msi.tropix.common.jobqueue.execution.gram.GramJobResolverImpl;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class GramJobResolverImplTest {

  @Test(groups = "unit")
  public void gramJobrResolver() {
    final Credential proxy = Credentials.getMock();
    final GramJobResolverImpl resolver = new GramJobResolverImpl();
    final GramJobFactory factory = EasyMock.createMock(GramJobFactory.class);
    resolver.setGramJobFactory(factory);
    final GramExecutionJob job = EasyMock.createMock(GramExecutionJob.class);
    final GramJob gramJob = EasyMock.createMock(GramJob.class);
    final String handle = "moohandle";
    EasyMock.expect(job.getHandle()).andReturn(handle);
    EasyMock.expect(job.getProxy()).andReturn(proxy.toString());
    EasyMock.expect(factory.getGramJob()).andReturn(gramJob);
    gramJob.setCredentials(proxy);
    gramJob.setHandle(handle);
    EasyMockUtils.replayAll(factory, job, gramJob);
    assert gramJob == resolver.getGramJob(job);
    EasyMockUtils.verifyAndResetAll(factory, job, gramJob);
  }

}
