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

package edu.umn.msi.tropix.common.jobqueue.client.persistent;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobPoller;
import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class PersistentJobPollerImpl implements JobPoller, JobUpdateListener {
  private final Log log = LogFactory.getLog(PersistentJobPollerImpl.class);
  private JobUpdateListener jobUpdateListener;
  private JobService jobService;
  private JobPoller basePoller;
  private boolean failIfCannotPersist = false;

  @PostConstruct
  public void init() throws IllegalStateException {
    Collection<Job> jobs = null;
    try {
      jobs = jobService.getJobs();
    } catch(final Throwable e) {
      throw ExceptionUtils.logAndConvert(log, e, "Failed to load persisted jobs, program should be terminated and persistence issues resolved.", IllegalStateException.class);
    }
    log.info("Starting up polling saved jobs, " + jobs.size() + " jobs obtained.");
    for(final Job job : jobs) {
      try {
        // No need to persist, its was loaded from persistent source
        pollJob(job, false);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(log, t, "Failed start polling job loaded from persistence " + job);
      }
    }
  }

  public void pollJob(final Job job) {
    pollJob(job, !jobService.jobExists(job.getTicket()));
  }

  private void pollJob(final Job job, final boolean persist) {    
    if(persist) {
      try {
        jobService.saveJob(job);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(log, t, "Failed to persist job " + job + ".");
        if(failIfCannotPersist) {
          throw new IllegalStateException("Failed to persist job " + job, t);
        }
      }
    }
    try {
      basePoller.pollJob(job);
    } catch(final Throwable t) {
      try {
        jobService.dropJob(job.getTicket());
      } catch(final Throwable innerThrowable) {
        ExceptionUtils.logQuietly(log, innerThrowable, "Failed to drop job " + job + " that failed to submit, it may still be in the database");
      }
      throw ExceptionUtils.logAndConvert(log, t, "Failed to poll job " + job, IllegalStateException.class);
    }
  }

  public void jobComplete(final Ticket ticket, final boolean finishedProperly, final Status finalStatus) {
    try {
      jobService.dropJob(ticket);
    } catch(final Throwable t) {
      ExceptionUtils.logQuietly(log, t, "Failed to remove possibly persisted job with ticket " + ticket + ". This should be manually removed.");
    }
    try {
      jobUpdateListener.jobComplete(ticket, finishedProperly, null);
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(log, e);
    }
  }

  public void update(final Ticket ticket, final Status status) {
    try {
      jobUpdateListener.update(ticket, status);
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(log, e);
    }
  }

  public void setJobUpdateListener(final JobUpdateListener baseJobUpdateListener) {
    this.jobUpdateListener = baseJobUpdateListener;
  }

  public void setJobService(final JobService jobService) {
    this.jobService = jobService;
  }

  public void setBasePoller(final JobPoller basePoller) {
    this.basePoller = basePoller;
  }

  public void setFailIfCannotPersist(final boolean failIfCannotPersist) {
    this.failIfCannotPersist = failIfCannotPersist;
  }
}
