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

package edu.umn.msi.tropix.common.jobqueue.client.persistent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "JOB_CLIENT")
public class PersistentJob {
  @Id
  @Column(name = "TICKET", nullable = false)
  private String ticket;

  @Column(name = "TYPE")
  private String jobType;

  @Column(name = "SERVICE_URL")
  private String serviceUrl;

  @Column(name = "PROXY", nullable = true, columnDefinition = "longvarchar")
  private String proxy;

  public String getTicket() {
    return ticket;
  }

  public void setTicket(final String ticket) {
    this.ticket = ticket;
  }

  public String getJobType() {
    return jobType;
  }

  public void setJobType(final String jobType) {
    this.jobType = jobType;
  }

  public String getServiceUrl() {
    return serviceUrl;
  }

  public void setServiceUrl(final String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public String getProxy() {
    return proxy;
  }

  public void setProxy(final String proxy) {
    this.proxy = proxy;
  }
}
