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

package edu.umn.msi.tropix.common.jobqueue.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.TicketProvider;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;

public class JobProcessorQueueContextImpl<T extends JobDescription> implements JobQueueContext {
  private static final Log LOG = LogFactory.getLog(JobProcessorQueueContextImpl.class);
  private JobProcessorQueue<? super T> jobProcessorQueue;
  private TicketProvider<Ticket> ticketSupplier;

  public JobProcessorQueueContextImpl() {
    LOG.debug("Constructing " + toString());
  }
  
  public Status getStatus() {
    final Ticket ticket = getTicket();
    final Status status = jobProcessorQueue.getStatus(ticket);
    return status;
  }

  protected void submitJob(final JobProcessor<T> jobProcessor) {
    final Ticket ticket = jobProcessorQueue.submitJob(jobProcessor, AnnotationUtils.findAnnotation(getClass(), JobType.class).value());
    ticketSupplier.set(ticket);
  }
  
  protected void complete() {
    final Ticket ticket = ticketSupplier.get();
    complete(ticket);
  }
  
  protected void complete(final Ticket ticket) {
    jobProcessorQueue.complete(ticket);    
  }
  
  protected void fail(final Ticket ticket) {
    jobProcessorQueue.fail(ticket);
  }
  
  protected void fail() {
    final Ticket ticket = ticketSupplier.get();
    fail(ticket);
  }
  
  protected void transferring(final Ticket ticket) {
    jobProcessorQueue.transferring(ticket);
  }
  
  public Ticket getTicket() {
    final Ticket ticket = ticketSupplier.get();
    return ticket;
  }

  public void cancel() {
    final Ticket ticket = getTicket();
    jobProcessorQueue.cancel(ticket);
  }

  public void setJobProcessorQueue(final JobProcessorQueue<? super T> jobProcessorQueue) {
    LOG.debug("Setting JobProcessorQueue to  " + jobProcessorQueue);
    this.jobProcessorQueue = jobProcessorQueue;
  }

  public void setTicketSupplier(final TicketProvider<Ticket> ticketSupplier) {
    this.ticketSupplier = ticketSupplier;
  }
}
