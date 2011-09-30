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

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;

public class StatusServiceTest {

  @Test(groups = "unit")
  public void status() {
    final StatusServiceImpl service = new StatusServiceImpl();
    final JobProcessorQueue<JobDescription> jobQueue = EasyMock.createMock(JobProcessorQueue.class);
    service.setJobProcessorQueue(jobQueue);

    Status[] statuses = service.getStatuses(new Ticket[] {});
    assert statuses != null && statuses.length == 0;

    final boolean[] throwExceptionsArray = new boolean[] {false, true};
    for(final boolean throwExceptions : throwExceptionsArray) {
      final Ticket[] tickets = new Ticket[10];
      final Status[] expectedStatuses = new Status[10];

      for(int i = 0; i < 10; i++) {
        tickets[i] = new Ticket("" + i);
        jobQueue.getStatus(tickets[i]);
        expectedStatuses[i] = new Status();
        if(i % 3 == 0 && throwExceptions) {
          expectLastCall().andThrow(new IllegalStateException());
        } else {
          expectLastCall().andReturn(expectedStatuses[i]);
        }
      }
      replay(jobQueue);
      statuses = service.getStatuses(tickets);
      for(int i = 0; i < 10; i++) {
        if(i % 3 == 0 && throwExceptions) {
          assert statuses[i] == null;
        } else {
          assert statuses[i] == expectedStatuses[i];
        }
      }
      verify(jobQueue);
      reset(jobQueue);
    }
  }
}
