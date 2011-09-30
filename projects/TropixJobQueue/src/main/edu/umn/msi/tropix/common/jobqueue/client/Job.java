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

package edu.umn.msi.tropix.common.jobqueue.client;

import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class Job {
  private String jobType;
  private String serviceAddress;
  private Ticket ticket;
  private Credential proxy;

  public String getJobType() {
    return jobType;
  }

  public void setJobType(final String jobType) {
    this.jobType = jobType;
  }

  public String getServiceAddress() {
    return serviceAddress;
  }

  public void setServiceAddress(final String serviceAddress) {
    this.serviceAddress = serviceAddress;
  }

  public Ticket getTicket() {
    return ticket;
  }

  public void setTicket(final Ticket ticket) {
    this.ticket = ticket;
  }

  public Credential getProxy() {
    return proxy;
  }

  public void setProxy(final Credential proxy) {
    this.proxy = proxy;
  }
}
