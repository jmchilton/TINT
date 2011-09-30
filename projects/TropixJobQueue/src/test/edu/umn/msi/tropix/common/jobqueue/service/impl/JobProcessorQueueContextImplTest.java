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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.TicketProvider;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class JobProcessorQueueContextImplTest {

  @Test(groups = "unit")
  public void queueOperations() {
    @SuppressWarnings("unchecked")
    final TicketProvider<Ticket> ticketSupplier = createMock(TicketProvider.class);
    final JobProcessorQueue<JobDescription> jobProcessorQueue = EasyMock.createMock(JobProcessorQueue.class);

    @JobType("test")
    class PublicJobProcessorQueueContextImpl extends JobProcessorQueueContextImpl<JobDescription> {
      @Override
      // Overrideen to test
      public void submitJob(final JobProcessor<JobDescription> jobProcessor) {
        super.submitJob(jobProcessor);
      }
    }

    final PublicJobProcessorQueueContextImpl queueContext = new PublicJobProcessorQueueContextImpl();
    queueContext.setJobProcessorQueue(jobProcessorQueue);
    queueContext.setTicketSupplier(ticketSupplier);

    // getTicket
    expect(ticketSupplier.get()).andReturn(new Ticket("0"));
    replay(ticketSupplier);
    assert new Ticket("0").equals(queueContext.getTicket());
    EasyMockUtils.verifyAndReset(ticketSupplier);

    // getStatus
    final Status status = new Status();
    expect(ticketSupplier.get()).andReturn(new Ticket("1"));
    expect(jobProcessorQueue.getStatus(new Ticket("1"))).andReturn(status);
    replay(ticketSupplier, jobProcessorQueue);
    assert status == queueContext.getStatus();
    EasyMockUtils.verifyAndReset(ticketSupplier, jobProcessorQueue);

    // cancel
    expect(ticketSupplier.get()).andReturn(new Ticket("1"));
    jobProcessorQueue.cancel(new Ticket("1"));
    replay(ticketSupplier, jobProcessorQueue);
    queueContext.cancel();
    EasyMockUtils.verifyAndReset(ticketSupplier, jobProcessorQueue);

    // submitJob
    final JobProcessor<JobDescription> jobProcessor = EasyMock.createMock(JobProcessor.class);
    expect(jobProcessorQueue.submitJob(jobProcessor, "test")).andReturn(new Ticket("2"));
    ticketSupplier.set(new Ticket("2"));
    replay(ticketSupplier, jobProcessorQueue);
    queueContext.submitJob(jobProcessor);
    verify(ticketSupplier, jobProcessorQueue);
  }
}
