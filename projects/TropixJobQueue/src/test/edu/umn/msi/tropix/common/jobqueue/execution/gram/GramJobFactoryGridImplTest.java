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

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.easymock.EasyMock;
import org.globus.exec.generated.StateEnumeration;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.oasis.wsrf.properties.ResourceUnknownFaultType;
import org.testng.annotations.Test;

public class GramJobFactoryGridImplTest {

  @Test(groups = "unit")
  public void delegation() throws Exception {
    RuntimeException exception = null;
    final GramJobFactoryGridImpl factory = new GramJobFactoryGridImpl();
    final org.globus.exec.client.HackedGramJob wrappedJob = EasyMock.createMock(org.globus.exec.client.HackedGramJob.class);

    wrappedJob.setMessageProtectionTypeOverride((Integer) EasyMock.isNull());
    wrappedJob.setTransportProtectionTypeOverride((Integer) EasyMock.isNull());
    wrappedJob.setAuthorization(EasyMock.isA(NoAuthorization.class));

    wrappedJob.cancel();
    EasyMock.replay(wrappedJob);
    final GramJob job = factory.getGramJob(wrappedJob);
    job.cancel();
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    EasyMock.expect(wrappedJob.getState()).andReturn(StateEnumeration.Active);
    EasyMock.replay(wrappedJob);
    assert StateEnumeration.Active.equals(job.getState());
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    wrappedJob.refreshStatus();
    EasyMock.replay(wrappedJob);
    job.refreshStatus();
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    wrappedJob.refreshStatus();
    EasyMock.expectLastCall().andThrow(new ResourceUnknownFaultType());
    EasyMock.replay(wrappedJob);
    try {
      job.refreshStatus();
    } catch(final RuntimeException e) {
      exception = e;
    }
    assert exception instanceof GramJob.GramJobNotFoundException;
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);
    
    /*
    
    wrappedJob.refreshStatus();
    EasyMock.expectLastCall().andThrow(new EOFException());
    EasyMock.replay(wrappedJob);
    try {
      job.refreshStatus();
    } catch(final RuntimeException e) {
      exception = e;
    }
    assert exception instanceof GramJob.GramJobCredentialExpiredException;
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);
    */
    exception = null;
    EasyMock.expect(wrappedJob.getCredentials()).andStubReturn(null);
    wrappedJob.refreshStatus();
    EasyMock.expectLastCall().andThrow(new Exception());
    EasyMock.replay(wrappedJob);
    try {
      job.refreshStatus();
    } catch(final RuntimeException e) {
      exception = e;
    }
    assert exception != null;
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    wrappedJob.setDelegationEnabled(true);
    wrappedJob.setHandle("Cow Moo");
    EasyMock.replay(wrappedJob);
    job.setDelegationEnabled(true);
    job.setHandle("Cow Moo");
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    exception = null;
    wrappedJob.setHandle("Cow Moo");
    EasyMock.expectLastCall().andThrow(new Exception());
    EasyMock.replay(wrappedJob);
    try {
      job.setHandle("Cow Moo");
    } catch(final RuntimeException e) {
      exception = e;
    }
    assert exception != null;
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    EasyMock.expect(wrappedJob.getHandle()).andReturn("Moo Cow");
    EasyMock.replay(wrappedJob);
    assert "Moo Cow".equals(job.getHandle());
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    final EndpointReferenceType epr = new EndpointReferenceType();
    wrappedJob.submit(EasyMock.same(epr), EasyMock.eq(true), EasyMock.eq(false), EasyMock.eq("str"));
    EasyMock.replay(wrappedJob);
    job.submit(epr, true, false, "str");
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

    exception = null;
    wrappedJob.submit(EasyMock.same(epr), EasyMock.eq(true), EasyMock.eq(false), EasyMock.eq("str"));
    EasyMock.expectLastCall().andThrow(new Exception());
    EasyMock.replay(wrappedJob);
    try {
      job.submit(epr, true, false, "str");
    } catch(final RuntimeException e) {
      exception = e;
    }
    assert exception != null;
    EasyMock.verify(wrappedJob);
    EasyMock.reset(wrappedJob);

  }

}
