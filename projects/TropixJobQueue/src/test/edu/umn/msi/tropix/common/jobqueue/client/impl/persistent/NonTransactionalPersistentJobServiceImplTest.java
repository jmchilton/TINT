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

package edu.umn.msi.tropix.common.jobqueue.client.impl.persistent;

import java.util.Collection;

import org.globus.gsi.GlobusCredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.persistent.JobService;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credentials;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/common/jobqueue/client/persistent/testDatabaseContext.xml"})
public class NonTransactionalPersistentJobServiceImplTest extends AbstractTestNGSpringContextTests {
  @Autowired
  private JobService jobService;

  private Job getJob(final String ticketStr) throws GlobusCredentialException {
    final Job job = new Job();
    job.setProxy(Credentials.getMock());
    final Ticket ticket = new Ticket(ticketStr);
    job.setTicket(ticket);
    job.setJobType("Test");
    job.setServiceAddress("http://test");
    return job;
  }

  @Test(groups = "integration")
  public synchronized void allOps() throws GlobusCredentialException {
    Collection<Job> jobs = jobService.getJobs();
    assert jobs != null && jobs.isEmpty();

    final Job job1 = getJob("1");
    jobService.saveJob(job1);

    jobs = jobService.getJobs();
    assert jobs.size() == 1;
    assert jobs.iterator().next().getTicket().getValue().equals("1");

    jobService.dropJob(new Ticket("1"));
  }

}
