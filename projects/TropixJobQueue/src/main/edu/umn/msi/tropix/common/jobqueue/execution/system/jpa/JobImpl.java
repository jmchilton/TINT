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

package edu.umn.msi.tropix.common.jobqueue.execution.system.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;

@Entity
@Table(name = "JOB")
public class JobImpl implements Job {
  @Id
  @Column(name = "TICKET", nullable = false)
  private String ticket;

  @Column(name = "POS", nullable = false)
  private Long position;

  @Column(name = "JOB_DESCRIPTION", nullable = false, columnDefinition = "longvarchar")
  private String description;

  @ManyToOne
  @JoinColumn(name = "QUEUE_ID")
  private QueueImpl queue;

  public JobImpl() {
  }

  public JobImpl(final String ticket, final Long position, final String description, final QueueImpl queue) {
    this.ticket = ticket;
    this.position = position;
    this.description = description;
    this.queue = queue;
  }

  public String getTicket() {
    return ticket;
  }

  public Long getPosition() {
    return position;
  }

  public String getDescription() {
    return description;
  }

  public Queue getQueue() {
    return queue;
  }
}
