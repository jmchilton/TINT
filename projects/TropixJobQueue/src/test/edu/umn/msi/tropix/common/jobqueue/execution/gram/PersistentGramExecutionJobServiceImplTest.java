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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.test.FreshConfigTransactionalTest;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/common/test/gramDatabaseContext.xml"})
@TransactionConfiguration(transactionManager = "gramQueueTransactionManager", defaultRollback = true)
public class PersistentGramExecutionJobServiceImplTest extends FreshConfigTransactionalTest {
  @Autowired
  private GramExecutionJobService persistentGramExecutionJobServiceImpl;

  @Test(groups = "persistence")
  public void persist() {
    persistentGramExecutionJobServiceImpl.persistJob("123456", "desc", "123", "proxystr", ExecutionState.PENDING.toString());

    assertLongQuery("SELECT count(*) FROM GRAMJOB", 1L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE JOB_DESCRIPTION='desc'", 1L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE PROXY='proxystr'", 1L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE STATE='PENDING'", 1L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE TICKET='123'", 1L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE HANDLE='123456'", 1L);

    persistentGramExecutionJobServiceImpl.persistJob("1234566", "desc", "1234", "proxystr", ExecutionState.COMPLETE.toString());
    assertLongQuery("SELECT count(*) FROM GRAMJOB", 2L);
  }

  @NotTransactional
  @Test(groups = "persistence")
  public void loadOutsideTransaction() {
    assert persistentGramExecutionJobServiceImpl.loadJobs().size() == 0;
  }

  @Test(groups = "persistence")
  public void load() {
    assert persistentGramExecutionJobServiceImpl.loadJobs().size() == 0;
    exec("INSERT INTO GRAMJOB(TICKET, JOB_DESCRIPTION, PROXY, STATE, HANDLE) VALUES('1', '2', '3', '4', '5')");
    assert persistentGramExecutionJobServiceImpl.loadJobs().size() == 1;
    GramExecutionJob job = persistentGramExecutionJobServiceImpl.loadJob("1");
    assert job.getTicket().equals("1");
    assert job.getDescription().equals("2");
    assert job.getProxy().equals("3");
    assert job.getState().equals("4");
    assert job.getHandle().equals("5");

    exec("INSERT INTO GRAMJOB(TICKET, JOB_DESCRIPTION, PROXY, STATE, HANDLE) VALUES('10', '20', '30', '40', '50')");
    assert persistentGramExecutionJobServiceImpl.loadJobs().size() == 2;

    job = persistentGramExecutionJobServiceImpl.loadJob("10");
    assert job.getTicket().equals("10");
    assert job.getDescription().equals("20");
    assert job.getProxy().equals("30");
    assert job.getState().equals("40");
    assert job.getHandle().equals("50");

    // Verify old job unaffected
    job = persistentGramExecutionJobServiceImpl.loadJob("1");
    assert job.getTicket().equals("1");
    assert job.getDescription().equals("2");
    assert job.getProxy().equals("3");
    assert job.getState().equals("4");
    assert job.getHandle().equals("5");
  }

  @Test(groups = "persistence")
  public void updateState() {
    exec("INSERT INTO GRAMJOB(TICKET, JOB_DESCRIPTION, PROXY, STATE, HANDLE) VALUES('1', '2', '3', 'PENDING', '5')");
    assert persistentGramExecutionJobServiceImpl.loadJobs().size() == 1;

    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE STATE='PENDING'", 1L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE STATE='RUNNING'", 0L);

    persistentGramExecutionJobServiceImpl.updateState("1", "RUNNING");

    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE STATE='PENDING'", 0L);
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE STATE='RUNNING'", 1L);
  }

  @Test(groups = "persistence")
  public void delete() {
    exec("INSERT INTO GRAMJOB(TICKET, JOB_DESCRIPTION, PROXY, STATE, HANDLE) VALUES('1', '2', '3', 'PENDING', '5')");
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE TICKET='1'", 1L);
    persistentGramExecutionJobServiceImpl.delete("1");
    assertLongQuery("SELECT count(*) FROM GRAMJOB WHERE TICKET='1'", 0L);
  }

  private void assertLongQuery(final String queryString, final long expectedCount) {
    final long count = simpleJdbcTemplate.queryForLong(queryString);
    assert count == expectedCount : count;
  }

  private void exec(final String execString) {
    simpleJdbcTemplate.getJdbcOperations().execute(execString);
  }
}
