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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GRAMJOB")
public class GramExecutionJobImpl implements GramExecutionJob {
  @Id
  @Column(name = "TICKET", nullable = false)
  private String ticket;

  @Column(name = "JOB_DESCRIPTION", nullable = false, columnDefinition = "longvarchar")
  private String description;

  @Column(name = "HANDLE", nullable = false)
  private String handle;

  @Column(name = "STATE", nullable = false)
  private String state;

  @Column(name = "PROXY", nullable = true, columnDefinition = "longvarchar")
  private String proxy;

  public GramExecutionJobImpl() {
  }

  public GramExecutionJobImpl(final String ticket, final String description, final String handle, final String proxy, final String state) {
    this.ticket = ticket;
    this.description = description;
    this.handle = handle;
    this.proxy = proxy;
    this.state = state;
  }

  public GramExecutionJobImpl(final GramExecutionJobImpl job) {
    this.ticket = job.ticket;
    this.description = job.description;
    this.handle = job.handle;
    this.proxy = job.proxy;
    this.state = job.state;
  }

  public String getTicket() {
    return ticket;
  }

  /**
   * This should NEVER return null!
   */
  public String getDescription() {
    return description;
  }

  public String getHandle() {
    return handle;
  }

  /**
   * This should NEVER return null!
   */
  public String getState() {
    return state;
  }

  public String getProxy() {
    return proxy;
  }

  public void setState(final String state) {
    this.state = state;
  }
}
