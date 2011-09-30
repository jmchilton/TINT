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
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobTracker;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobTrackerSupplierImpl;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class JobTrackerTest {

  public Status getStatus(final StageEnumeration stageEnumeration) {
    final Status status = new Status();
    final Stage stage = new Stage();
    stage.setValue(stageEnumeration);
    status.setStage(stage);
    return status;
  }

  @Test(groups = "unit")
  public void addCompleteEmpty() {
    final JobTrackerSupplierImpl trackerSupplier = new JobTrackerSupplierImpl();
    final JobUpdateListener updateListener = createMock(JobUpdateListener.class);
    trackerSupplier.setJobUpdateListener(updateListener);
    JobTracker tracker = trackerSupplier.get();

    final List<Ticket> tickets = new ArrayList<Ticket>(50);
    final List<Job> jobs = new ArrayList<Job>(50);
    for(int i = 0; i < 50; i++) {
      tickets.add(new Ticket("" + i));
      final Job job = new Job();
      job.setTicket(new Ticket("" + i));
      jobs.add(job);
      updateListener.jobComplete(eq(new Ticket("" + i)), eq(true), isA(Status.class));
      updateListener.update(new Ticket("" + i), getStatus(StageEnumeration.Complete));
    }

    replay(updateListener);

    assert !tracker.hasJobs();

    for(int i = 0; i < 50; i++) {
      assert tracker.getTickets().size() == i;
      tracker.addJob(tickets.get(i), jobs.get(i));
      assert tracker.hasJobs();
    }

    for(int i = 0; i < 50; i++) {
      assert tracker.hasJobs();
      final int size = tracker.getTickets().size();
      assert size == 50 - i : "Wrong size on iteration " + i + ", size is " + size;
      tracker.updateJob(tickets.get(i), getStatus(StageEnumeration.Complete));
    }
    assert !tracker.hasJobs();

    EasyMockUtils.verifyAndReset(updateListener);

    for(final boolean finishedProperly : new boolean[] {true, false}) {
      tracker = trackerSupplier.get();

      for(int i = 0; i < 50; i++) {
        final Ticket ticket = tickets.get(i);
        final Job job = jobs.get(i);
        tracker.addJob(ticket, job);
        updateListener.jobComplete(eq(ticket), eq(finishedProperly), (Status) isNull());
      }

      replay(updateListener);
      assert tracker.hasJobs();
      tracker.completeAllJobs(finishedProperly);
      assert !tracker.hasJobs();
      assert tracker.getTickets().isEmpty();
      EasyMockUtils.verifyAndReset(updateListener);
    }
  }

  @Test(groups = "unit")
  public void handlesExceptions() {
    final JobTrackerSupplierImpl trackerSupplier = new JobTrackerSupplierImpl();
    final JobUpdateListener updateListener = createMock(JobUpdateListener.class);
    trackerSupplier.setJobUpdateListener(updateListener);

    final JobTracker tracker = trackerSupplier.get();

    final Ticket ticket = new Ticket("1234");
    final Job job = new Job();
    job.setTicket(ticket);

    final Ticket ticket2 = new Ticket("12345");
    final Job job2 = new Job();
    job2.setTicket(ticket2);

    updateListener.update(ticket2, getStatus(StageEnumeration.Failed));
    expectLastCall().andThrow(new IllegalArgumentException());
    updateListener.jobComplete(same(ticket2), eq(false), isA(Status.class));
    expectLastCall().andThrow(new IllegalStateException());
    updateListener.update(ticket, getStatus(StageEnumeration.Complete));
    updateListener.jobComplete(same(ticket), eq(true), isA(Status.class));

    replay(updateListener);

    tracker.addJob(ticket, job);
    tracker.addJob(ticket2, job2);

    tracker.updateJob(ticket2, getStatus(StageEnumeration.Failed));
    tracker.updateJob(ticket, getStatus(StageEnumeration.Complete));
    EasyMockUtils.verifyAndReset(updateListener);
  }

}
