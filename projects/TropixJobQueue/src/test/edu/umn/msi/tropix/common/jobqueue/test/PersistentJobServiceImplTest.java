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

import java.io.InputStream;
import java.util.Collection;

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.persistent.JobService;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credentials;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/common/jobqueue/client/persistent/testDatabaseContext.xml"})
@TransactionConfiguration(transactionManager = "jobClientTransactionManager", defaultRollback = true)
public class PersistentJobServiceImplTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired
  private JobService jobService;

  private Job getJob(final String ticketStr) throws GlobusCredentialException {
    final Job job = new Job();
    final InputStream stream = this.getClass().getResourceAsStream("proxy");
    final GlobusCredential proxy = new GlobusCredential(stream);
    job.setProxy(Credentials.get(proxy));
    final Ticket ticket = new Ticket(ticketStr);
    job.setTicket(ticket);
    job.setJobType("Test");
    job.setServiceAddress("http://test");
    return job;
  }

  @NotTransactional
  @Test(groups = "persistence")
  public void getJobs() {
    jobService.getJobs(); // Just make sure transactions are working properly
  }
  
  @Test(groups = "persistence")
  public synchronized void allOps() throws GlobusCredentialException {
    Collection<Job> jobs = jobService.getJobs();
    assert jobs != null && jobs.isEmpty();

    final Job job1 = getJob("1"), job2 = getJob("2");
    jobService.saveJob(job1);

    jobs = jobService.getJobs();
    assert jobs.size() == 1;
    assert jobs.iterator().next().getTicket().getValue().equals("1");

    jobService.saveJob(job2);
    jobs = jobService.getJobs();
    assert jobs.size() == 2;

    jobService.dropJob(new Ticket("2"));
    jobs = jobService.getJobs();
    assert jobs.size() == 1;
    assert jobs.iterator().next().getTicket().getValue().equals("1");
  }

  @Test(groups = "persistence")
  public synchronized void jdbcLoad() throws Exception {
    exec("INSERT INTO JOB_CLIENT(TICKET,TYPE) VALUES(12345,'TestType')");
    final Collection<Job> jobs = jobService.getJobs();
    assert jobs.iterator().next().getTicket().equals(new Ticket("12345"));
    assert jobs.iterator().next().getJobType().equals("TestType");
    assert jobs.size() == 1;
  }

  @Test(groups = "persistence")
  public synchronized void jdbcSave() throws Exception {
    final Job job = new Job();
    job.setJobType("AnotherType");
    job.setTicket(new Ticket("4567"));
    jobService.saveJob(job);

    assert longQuery("SELECT count(*) FROM JOB_CLIENT WHERE TICKET='4567'") == 1;
    assert longQuery("SELECT count(*) FROM JOB_CLIENT WHERE TYPE='AnotherType'") == 1;
  }

  private void exec(final String execString) {
    simpleJdbcTemplate.getJdbcOperations().execute(execString);
  }

  private long longQuery(final String queryString) {
    return simpleJdbcTemplate.queryForLong(queryString);
  }
}
