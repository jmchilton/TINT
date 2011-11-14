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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.service.TicketBean;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class LocalJobClientFactoryFunctionImplTest {  
  private LocalJobClientFactoryFunctionImpl factory;
  private StatusService statusService;
  private TicketBean ticketBean;
  private JobQueueContext wrappedContext;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    ticketBean = new TicketBean();
    statusService = EasyMock.createMock(StatusService.class);
    wrappedContext = EasyMock.createMock(JobQueueContext.class);
    
    factory = new LocalJobClientFactoryFunctionImpl();
    factory.setStatusService(statusService);
    factory.setTicketBean(ticketBean);
  }
  
  @Test(groups = "unit")
  public void testCreateClient() {    
    final JobQueueContext returnedContext = 
      factory.apply(wrappedContext).createJobContext(Credentials.getMock("moo"), "http://service", JobQueueContext.class);
    assert wrappedContext == returnedContext;
  }
  
  @Test(groups = "unit")
  public void testGetClient() {
    final JobQueueContext returnedContext = 
      factory.apply(wrappedContext).getJobClient(Credentials.getMock("moo"), "http://service", new Ticket("moo"), JobQueueContext.class);
    assert ticketBean.get().equals(new Ticket("moo"));
    assert wrappedContext == returnedContext;
  }
  
  @Test(groups = "unit")
  public void testGetStatusService() {
    final StatusService returnedStatusService =
      factory.apply(wrappedContext).getServiceClient(Credentials.getMock(), "http://service", StatusService.class);
    assert returnedStatusService == statusService;
  }
  
}
