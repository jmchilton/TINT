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

package edu.umn.msi.tropix.jobs.impl;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;
import edu.umn.msi.tropix.jobs.services.impl.StatusServiceFactoryImpl;

public class StatusServiceFactoryImplTest {

  @Test(groups = "unit", timeOut = 1000)
  public void getStatusService() {
    final StatusServiceFactoryImpl service = new StatusServiceFactoryImpl();
    final JobContextClientFactory jobContextClientFactory = EasyMock.createMock(JobContextClientFactory.class);
    service.setJobContextClientFactory(jobContextClientFactory);
    final StatusService statusService = EasyMock.createMock(StatusService.class);
    jobContextClientFactory.getServiceClient(null, "http://cow", StatusService.class);
    EasyMock.expectLastCall().andReturn(statusService);
    EasyMock.replay(jobContextClientFactory);
    assert statusService == service.getStatusService("http://cow", null);
    EasyMock.verify(jobContextClientFactory);
  }
}
