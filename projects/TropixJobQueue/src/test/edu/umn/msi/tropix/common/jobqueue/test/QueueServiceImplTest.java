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

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;
import edu.umn.msi.tropix.common.jobqueue.execution.system.QueueServiceImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.PersistentQueueService;

public class QueueServiceImplTest {
  private QueueServiceImpl queueServiceImpl;
  private Queue queue, backupQueue;
  private Job job1;
  private PersistentQueueService persistentQueueService;

  @BeforeMethod(groups = "unit")
  public void init() {
    queueServiceImpl = new QueueServiceImpl();
    queue = EasyMock.createMock(Queue.class);
    backupQueue = EasyMock.createMock(Queue.class);
    job1 = EasyMock.createMock(Job.class);
    persistentQueueService = EasyMock.createMock(PersistentQueueService.class);
    queueServiceImpl.setQueue(queue);
    queueServiceImpl.setPersistentQueueService(persistentQueueService);
  }

  @Test(groups = "unit")
  public void getJobs() {
    final List<Job> jobs = Collections.emptyList();
    EasyMock.expect(queue.getJobs()).andReturn(jobs);
    EasyMock.replay(queue);
    assert jobs == queueServiceImpl.getJobs();
    EasyMock.verify(queue);
  }

  @Test(groups = "unit")
  public void remove() {
    EasyMock.expect(queue.copy()).andReturn(backupQueue);
    EasyMock.expect(persistentQueueService.removeJob(queue, "123")).andReturn(job1);
    EasyMock.replay(queue, persistentQueueService);
    assert job1 == queueServiceImpl.removeJob("123");
    EasyMock.verify(queue, persistentQueueService);
  }

  @Test(groups = "unit")
  public void push() {
    final String ticket = "123";
    final String jobDescription = "job description";
    EasyMock.expect(queue.copy()).andReturn(backupQueue);
    EasyMock.expect(persistentQueueService.pushJob(queue, ticket, jobDescription)).andReturn(job1);
    EasyMock.replay(queue, persistentQueueService);
    assert job1 == queueServiceImpl.pushJob(ticket, jobDescription);
    EasyMock.verify(queue, persistentQueueService);
  }

  @Test(groups = "unit")
  public void backup() {
    EasyMock.expect(queue.copy()).andReturn(backupQueue);
    EasyMock.expect(persistentQueueService.popJob(queue)).andThrow(new RuntimeException());
    EasyMock.expect(backupQueue.copy()).andReturn(backupQueue);
    EasyMock.expect(persistentQueueService.popJob(backupQueue)).andReturn(job1);

    EasyMock.replay(queue, backupQueue, persistentQueueService);
    RuntimeException re = null;
    try {
      queueServiceImpl.popJob();
    } catch(final RuntimeException e) {
      re = e;
    }
    assert re != null;
    assert queueServiceImpl.popJob() == job1;
    EasyMock.verify(queue, backupQueue, persistentQueueService);

  }

  @Test(groups = "unit")
  public void pop() {
    EasyMock.expect(queue.copy()).andReturn(backupQueue);
    EasyMock.expect(persistentQueueService.popJob(queue)).andReturn(job1);
    EasyMock.replay(queue, persistentQueueService);
    assert queueServiceImpl.popJob() == job1;
    EasyMock.verify(queue, persistentQueueService);
  }

}
