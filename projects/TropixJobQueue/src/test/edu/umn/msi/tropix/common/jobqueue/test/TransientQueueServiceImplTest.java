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

package edu.umn.msi.tropix.common.jobqueue.test;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.JobImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.QueueImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.TransientQueueServiceImpl;

public class TransientQueueServiceImplTest {

  private void testEmptyQueue(final Queue queue) {
    assert queue.getNumCreatedJobs().equals(0L);
    assert queue.getNumPendingJobs().equals(0L);
    assert queue.getJobs().isEmpty();
  }

  @Test(groups = "unit")
  public void load() {
    final TransientQueueServiceImpl service = new TransientQueueServiceImpl();
    final QueueImpl queue = service.load(1L);
    testEmptyQueue(queue);
    service.pushJob(queue, "1", "1");
    service.pushJob(queue, "123", "moo");
    service.pushJob(queue, "12345", "cow");
    assert queue.getId().equals(1L);
    assert queue.getNumCreatedJobs().equals(3L);
    assert queue.getNumPendingJobs().equals(3L);
    assert queue.getJobs().get(0).getTicket().equals("1");
    JobImpl job = service.popJob(queue);
    assert job.getTicket().equals("1");
    job = service.removeJob(queue, "12345");
    assert job.getTicket().equals("12345");

    assert service.load(1L) == queue;

    final QueueImpl queue2 = service.load(2L);
    testEmptyQueue(queue2);
    assert queue2.getId().equals(2L);
  }
}
