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

package edu.umn.msi.tropix.common.jobqueue.execution.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.PersistentQueueService;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/common/jobqueue/execution/system/testDatabaseContext.xml")
@TransactionConfiguration(transactionManager = "systemJobQueueTransactionManager", defaultRollback = true)
public class PersistentQueueServiceTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired
  private PersistentQueueService persistentQueueService;

  @Test(groups = "persistence")
  public void loadEmpty() {
    Queue queue = persistentQueueService.load(0L);
    assert queue.getJobs().isEmpty();
    assert queue.getNumCreatedJobs() == 0;
    assert queue.getNumPendingJobs() == 0;

    queue = persistentQueueService.load(0L);
    assert queue.getJobs().isEmpty();
    assert queue.getNumCreatedJobs() == 0;
    assert queue.getNumPendingJobs() == 0;
  }

  @Test(groups = "persistence")
  public void loadNonempty() {
    createQueueViaJdbc(1L);
    final String[] tickets = new String[] {"12342341234123", "22344565756772", "35423451234125"};
    final Queue queue = persistentQueueService.load(1L);

    assert queue.getNumCreatedJobs() == 5;
    assert queue.getNumPendingJobs() == 3;
    assert queue.getJobs().size() == 3;
    for(int i = 0; i < 3; i++) {
      final Job job = queue.getJobs().get(i);
      assert job.getDescription().equals("Hello");
      assert job.getPosition().equals(new Long(i + 2)) : "Expected position " + (i + 2) + " but it was " + job.getPosition();
      assert job.getTicket().equals(tickets[i]);
    }

  }

  private void createQueueViaJdbc(final Long queueId) {
    exec("INSERT INTO QUEUE(ID, NUM_CREATED_JOBS, NUM_PENDING_JOBS) VALUES (" + queueId + ", 5, 3)");
    exec("INSERT INTO JOB(TICKET, POS, JOB_DESCRIPTION, QUEUE_ID) VALUES(12342341234123, 2, 'Hello', " + queueId + ")");
    exec("INSERT INTO JOB(TICKET, POS, JOB_DESCRIPTION, QUEUE_ID) VALUES(35423451234125, 4, 'Hello', " + queueId + ")");
    exec("INSERT INTO JOB(TICKET, POS, JOB_DESCRIPTION, QUEUE_ID) VALuES(22344565756772, 3, 'Hello', " + queueId + ")");
  }

  @Test(groups = "persistence")
  public void pop() {
    createQueueViaJdbc(2L);
    final Queue queue = persistentQueueService.load(2L);

    assert longQuery("SELECT count(*) from JOB where TICKET='12342341234123'") == 1;
    final Job job1 = persistentQueueService.popJob(queue);
    assert longQuery("SELECT count(*) from JOB where TICKET='12342341234123'") == 0;

    assert job1.getPosition().equals(new Long(2));
    assert queue.getJobs().size() == 2;
    assert queue.getJobs().get(0).getPosition().equals(new Long(3));

    final Job job2 = persistentQueueService.popJob(queue);

    assert job2.getPosition().equals(new Long(3));
    assert queue.getJobs().size() == 1;

    final Job job3 = persistentQueueService.popJob(queue);
    assert job3.getPosition().equals(new Long(4));
    assert queue.getJobs().size() == 0;

    final Job nullJob = persistentQueueService.popJob(queue);
    assert nullJob == null;
  }

  @Test(groups = "persistence")
  public void push() {
    assert longQuery("SELECT count(*) from QUEUE where ID=3") == 0L;
    final Queue queue = persistentQueueService.load(3L);
    assert longQuery("SELECT count(*) from QUEUE where ID=3") == 1;

    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=3") == 0;
    assert longQuery("SELECT count(*) from JOB where TICKET=123") == 0;
    Job returnedJob = persistentQueueService.pushJob(queue, "123", "Hello");
    assert longQuery("SELECT count(*) from JOB where TICKET=123") == 1;
    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=3") == 1;
    assert returnedJob.getTicket().equals("123");
    assert returnedJob.getDescription().equals("Hello");

    Job foundJob = queue.getJobs().get(0);
    assert foundJob.getTicket().equals("123");
    assert foundJob.getDescription().equals("Hello");

    assert longQuery("SELECT count(*) from JOB where TICKET=456") == 0;
    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=3") == 1;
    returnedJob = persistentQueueService.pushJob(queue, "456", "Hello");
    assert longQuery("SELECT count(*) from JOB where TICKET=456") == 1;
    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=3") == 2;

    assert returnedJob.getTicket().equals("456");
    assert returnedJob.getDescription().equals("Hello");
    foundJob = queue.getJobs().get(1);
    assert foundJob.getTicket().equals("456");
    assert foundJob.getDescription().equals("Hello");
  }

  @Test(groups = "persistence")
  public void pushThenPop() {
    final Queue queue = persistentQueueService.load(4L);
    persistentQueueService.pushJob(queue, "1234", "World");
    persistentQueueService.pushJob(queue, "5678", "!");

    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=4") == 2;
    persistentQueueService.popJob(queue);
    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=4") == 1;

    assert queue.getJobs().size() == 1;
    assert queue.getJobs().get(0).getTicket().equals("5678");

    persistentQueueService.popJob(queue);
    assert longQuery("SELECT count(*) from JOB where QUEUE_ID=4") == 0;

    assert queue.getJobs().isEmpty();
  }

  @Test(groups = "persistence")
  public void remove() {
    final Queue queue = persistentQueueService.load(5L);
    persistentQueueService.pushJob(queue, "123456", "Hello");
    persistentQueueService.pushJob(queue, "1234567", "World");
    persistentQueueService.pushJob(queue, "12345678", "!");
    List<Job> jobs = queue.getJobs();
    assert jobs.size() == 3;

    persistentQueueService.removeJob(queue, "1234567");
    jobs = queue.getJobs();

    assert jobs.size() == 2;

    assert jobs.get(0).getTicket().equals("123456");
    assert jobs.get(1).getTicket().equals("12345678");
  }

  private long longQuery(final String queryString) {
    return simpleJdbcTemplate.queryForLong(queryString);
  }

  private void exec(final String execString) {
    simpleJdbcTemplate.getJdbcOperations().execute(execString);
  }
}
