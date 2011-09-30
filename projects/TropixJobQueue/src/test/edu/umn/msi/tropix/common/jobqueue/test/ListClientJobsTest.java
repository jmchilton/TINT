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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.persistent.JobService;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/common/test/clientDatabaseContext.xml"})
@TransactionConfiguration(transactionManager = "jobClientTransactionManager", defaultRollback = true)
public class ListClientJobsTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired
  private JobService jobService;

  @NotTransactional
  @Test(groups = "one-off")
  public void printJobs() {
    for(final Job job : jobService.getJobs()) {
      System.out.println("Job found with type " + job.getJobType() + " and ticket " + job.getTicket());
    }
  }

}
