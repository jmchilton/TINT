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

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.JobImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.QueueImpl;

public class PersistentQueueObjectsTest {

  @Test(groups = "unit")
  public void queueCopy() {
    final JobImpl job = new JobImpl();
    final List<JobImpl> jobList = Arrays.asList(job);
    final QueueImpl queueImpl = new QueueImpl(1L, 4L, 2L, jobList);
    final Queue queueCopy = queueImpl.copy();
    assert queueCopy.getId().equals(1L);
    assert queueCopy.getJobs().size() == 1;
    assert queueCopy.getJobs().get(0).equals(job);
    assert queueCopy.getNumCreatedJobs().equals(4L);
    assert queueCopy.getNumPendingJobs().equals(2L);
  }

  @Test(groups = "unit")
  public void constructors() {
    final QueueImpl queue = new QueueImpl(4L);
    assert queue.getId().equals(4L);
    assert queue.getNumCreatedJobs().equals(0L);
    assert queue.getNumPendingJobs().equals(0L);
    assert queue.getJobs().isEmpty();

    queue.toString(); // Make sure doesn't throw exception

    final JobImpl jobImpl = new JobImpl("ticket", 1L, "description", queue);
    assert jobImpl.getTicket().equals("ticket");
    assert jobImpl.getPosition().equals(1L);
    assert jobImpl.getDescription().equals("description");
    assert jobImpl.getQueue() == queue;
  }

}
