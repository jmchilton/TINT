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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.service.TicketBean;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class LocalJobClientFactoryFunctionImpl implements Function<JobQueueContext, JobClientFactory> {
  private TicketBean ticketBean;
  private StatusService statusService;
  
  public JobClientFactory apply(final JobQueueContext context) {
    return new LocalJobClientFactoryImpl(context);
  }
  
  private class LocalJobClientFactoryImpl implements JobClientFactory {
    private final JobQueueContext context;
    
    LocalJobClientFactoryImpl(final JobQueueContext context) {
      this.context = context;
    }
    
    public <T> T createJobContext(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
      return getJobClient(interfaceClass);
    }

    public <T> T getJobClient(final Credential proxy, final String serviceUrl, final Ticket ticket, final Class<T> interfaceClass) {
      ticketBean.set(ticket);
      return getJobClient(interfaceClass);
    }

    private <T> T getJobClient(final Class<T> interfaceClass) {
      @SuppressWarnings("unchecked")
      T castOfContext = (T) context;
      return castOfContext;
    }
    
    public <T> T getServiceClient(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
      Preconditions.checkArgument(interfaceClass.isAssignableFrom(StatusService.class), "getServiceClient only works interfaceClass=StatusService was supplied with "  + interfaceClass);
      @SuppressWarnings("unchecked")
      T castOfStatusService = (T) statusService; 
      return castOfStatusService;
    }
    
  }
  
  public void setTicketBean(final TicketBean ticketBean) {
    this.ticketBean = ticketBean;
  }

  public void setStatusService(final StatusService statusService) {
    this.statusService = statusService;
  }
  
}
