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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.util.Arrays;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobPoller;
import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.client.persistent.JobService;
import edu.umn.msi.tropix.common.jobqueue.client.persistent.PersistentJobPollerImpl;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class PersistentJobPollerImplTest {
  private PersistentJobPollerImpl poller;
  private JobPoller basePoller;
  private JobService jobService;
  private JobUpdateListener listener;

  @BeforeMethod(groups = "unit")
  public void init() {
    poller = new PersistentJobPollerImpl();

    basePoller = createMock(JobPoller.class);
    jobService = createMock(JobService.class);
    listener = createMock(JobUpdateListener.class);

    poller.setBasePoller(basePoller);
    poller.setJobService(jobService);
    poller.setJobUpdateListener(listener);
  }

  private void replay() {
    EasyMock.replay(basePoller, jobService, listener);
  }

  private void reset() {
    EasyMockUtils.verifyAndReset(basePoller, jobService, listener);
  }

  @Test(groups = "unit")
  public void nonEmptyInit() {
    final Job job1 = getJob(), job2 = getJob();
    jobService.getJobs();
    expectLastCall().andReturn(Arrays.asList(job1, job2));
    basePoller.pollJob(job1);
    basePoller.pollJob(job2);
    replay();
    poller.init();
    reset();
  }

  @Test(groups = "unit")
  public void emptyInit() {
    jobService.getJobs();
    expectLastCall().andReturn(Arrays.asList());
    replay();
    poller.init();
    reset();
  }

  @Test(groups = "unit")
  public void saveFailIgnore() {
    final Job job1 = getJob();
    EasyMock.expect(jobService.jobExists(job1.getTicket())).andReturn(false);
    jobService.saveJob(job1);
    expectLastCall().andThrow(new IllegalStateException());
    basePoller.pollJob(job1);
    replay();
    poller.pollJob(job1);
    reset();
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void saveFailFatal() {
    poller.setFailIfCannotPersist(true);
    final Job job1 = getJob();
    EasyMock.expect(jobService.jobExists(job1.getTicket())).andReturn(false);
    jobService.saveJob(job1);
    expectLastCall().andThrow(new IllegalStateException());
    replay();
    poller.pollJob(job1);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void initListFail() {
    jobService.getJobs();
    expectLastCall().andThrow(new NullPointerException());
    replay();
    poller.init();
  }

  @Test(groups = "unit")
  public void initSubmitFail() {
    final Job job1 = getJob();
    expect(jobService.getJobs()).andReturn(Arrays.asList(job1));
    basePoller.pollJob(job1);
    expectLastCall().andThrow(new IllegalStateException());
    replay();
    poller.init();
    reset();
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void submitFail() {
    submitFail(true);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void submitAndDropFail() {
    submitFail(false);
  }

  @Test(groups = "unit")
  public void jobComplete() {
    jobComplete(false);
  }

  @Test(groups = "unit")
  public void jobCompleteFailDrop() {
    jobComplete(true);
  }

  public void jobComplete(final boolean throwException) {
    final Ticket ticket = new Ticket("123");
    for(final boolean completeNormally : new boolean[] {true, false}) {
      jobService.dropJob(ticket);
      if(throwException) {
        expectLastCall().andThrow(new IllegalStateException());
      }
      listener.jobComplete(ticket, completeNormally, null);
      replay();
      poller.jobComplete(ticket, completeNormally, null);
      reset();
    }
  }

  public void submitFail(final boolean dropProblem) {
    final Job job1 = getJob();
    final Ticket ticket = job1.getTicket();
    
    EasyMock.expect(jobService.jobExists(job1.getTicket())).andReturn(false);
    jobService.saveJob(job1);
    basePoller.pollJob(job1);
    expectLastCall().andThrow(new IllegalStateException());
    jobService.dropJob(ticket);
    if(dropProblem) {
      expectLastCall().andThrow(new IllegalArgumentException());
    }
    replay();
    poller.pollJob(job1);
  }

  @Test(groups = "unit")
  public void update() {
    final Ticket ticket = new Ticket("123");
    final Status status = new Status();
    listener.update(ticket, status);
    replay();
    poller.update(ticket, status);
    reset();
  }
  
  private Job getJob() {
    final Job job = new Job();
    job.setTicket(new Ticket(UUID.randomUUID().toString()));
    return job;
  }

  @Test(groups = "unit")
  public void submit() {
    final Job job1 = getJob();
    jobService.saveJob(job1);
    EasyMock.expect(jobService.jobExists(job1.getTicket())).andReturn(false);
    basePoller.pollJob(job1);
    replay();
    poller.pollJob(job1);
    reset();
    final Job job2 = getJob();
    jobService.saveJob(job2);
    EasyMock.expect(jobService.jobExists(job2.getTicket())).andReturn(false);
    basePoller.pollJob(job2);
    replay();
    poller.pollJob(job2);
    reset();
  }

}
