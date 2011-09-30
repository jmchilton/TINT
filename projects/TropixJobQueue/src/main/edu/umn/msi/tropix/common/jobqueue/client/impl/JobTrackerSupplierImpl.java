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

package edu.umn.msi.tropix.common.jobqueue.client.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.jobqueue.QueueStage;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class JobTrackerSupplierImpl implements Supplier<JobTracker> {
  private static final Log LOG = LogFactory.getLog(JobTrackerSupplierImpl.class);
  private JobUpdateListener jobUpdateListener;

  class JobTrackerImpl implements JobTracker {
    private final Map<Ticket, Job> map = new ConcurrentHashMap<Ticket, Job>();

    public void addJob(final Ticket ticket, final Job job) {
      Preconditions.checkNotNull(ticket);
      Preconditions.checkNotNull(job);
      map.put(ticket, job);
    }

    public void completeAllJobs(final boolean finishedProperly) {
      final Collection<Ticket> tickets = getTickets();
      for(final Ticket ticket : tickets) {
        completeJob(ticket, finishedProperly, null);
      }
    }

    private void completeJob(final Ticket ticket, final boolean finishedProperly, final Status finalStatus) {
      try {
        final Job job = map.get(ticket);
        Preconditions.checkNotNull(job);
        jobUpdateListener.jobComplete(ticket, finishedProperly, finalStatus);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(LOG, t, "Failed to execute completeJob on job with ticket " + ticket.getValue() + " which finished properly? " + finishedProperly);
      }
      map.remove(ticket);
    }

    public Collection<Ticket> getTickets() {
      final LinkedList<Ticket> tickets = new LinkedList<Ticket>();
      tickets.addAll(map.keySet());
      return tickets;
    }

    public boolean hasJobs() {
      return !map.isEmpty();
    }

    public void updateJob(final Ticket ticket, final Status status) {
      final QueueStage stage = QueueStage.fromStatusUpdateList(status);
      try {
        jobUpdateListener.update(ticket, status);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(LOG, t, "update call on jobUpdateListener failed.");
      }
      if(stage.isComplete()) {
        completeJob(ticket, stage.equals(QueueStage.COMPLETED), status);
      }
    }
  }

  public JobTracker get() {
    final JobTrackerImpl tracker = new JobTrackerImpl();
    return tracker;
  }

  public void setJobUpdateListener(final JobUpdateListener jobUpdateListener) {
    this.jobUpdateListener = jobUpdateListener;
  }
}
