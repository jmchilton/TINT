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

package edu.umn.msi.tropix.common.jobqueue.client.impl;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;

public class DelegatingJobUpdateListenerImplTest {

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void delegateOps(final boolean iterableSetter) {
    final DelegatingJobUpdateListenerImpl listener = new DelegatingJobUpdateListenerImpl();
    final JobUpdateListener jobUpdateListener = EasyMock.createMock(JobUpdateListener.class);
    if(iterableSetter) {
      listener.setJobUpdateListeners(Lists.newArrayList(jobUpdateListener));
    } else {
      listener.setJobUpdateListener(jobUpdateListener);
    }
    final Ticket ticket = new Ticket("123");
    jobUpdateListener.jobComplete(ticket, true, null);
    EasyMock.replay(jobUpdateListener);
    listener.jobComplete(ticket, true, null);
    EasyMockUtils.verifyAndReset(jobUpdateListener);
    final Status status = new Status();
    jobUpdateListener.update(ticket, status);
    EasyMock.replay(jobUpdateListener);
    listener.update(ticket, status);
    EasyMockUtils.verifyAndReset(jobUpdateListener);
  }
}
