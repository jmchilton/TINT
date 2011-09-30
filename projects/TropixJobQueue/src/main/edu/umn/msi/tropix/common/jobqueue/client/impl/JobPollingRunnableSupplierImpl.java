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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;

/**
 * Contract : For each JobPollingHalter instance, there will exist only one JobPollingRunnable. Only that instance of JobPollingRunnable should execute halt and/or haltIfComplete. If halt is called, all jobs left in the JobTracker should be processed as having failed by
 * implementer.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
// TODO: Test
public class JobPollingRunnableSupplierImpl implements Supplier<JobPollingRunnable> {
  private static final Log LOG = LogFactory.getLog(JobPollingRunnableSupplierImpl.class);

  class JobPollingRunnableImpl implements JobPollingRunnable {
    // Managed internally
    private StatusService statusService = null;

    // Set externally
    private Supplier<StatusService> statusServiceSupplier; // This should be factory that aggressively tries to create a client
    private JobPollingHalter jobPollingHalter;
    private JobTracker jobTracker;
    private String serviceUrl; // Only used for logging purposes

    public void run() {
      final Collection<Ticket> ticketCollection = jobTracker.getTickets();
      if(!ticketCollection.isEmpty()) {
        // Check shouldn't be jobTracker.hasJobs(). -John
        // The would be problematic if jobs get added in the mean time, and this version
        // is more testable (because it relies on less of the interface)
        Status[] statuses = null;
        Ticket[] tickets = null;
        try {
          if(statusService == null) {
            try {
              statusService = statusServiceSupplier.get();
              LOG.trace("statusServiceSupplier.get() returned");
            } catch(final ShutdownException exception) {
              // Need to treat the ShutdownException differently, don't want to rollback the jobs if this is why statusServiceSupplier
              // threw an exception. Also its probably best not to log, since log4j might have been shutdown also
              System.out.println("Received ShutdownException while attempt to connect to a status service at" + serviceUrl + ". Quiting runnable.");
              return;
            } catch(final Throwable t) {
              ExceptionUtils.logQuietly(LOG, t, "Failed to initialize status service client. Failing all jobs sent to service " + serviceUrl);
              jobPollingHalter.halt();
              return;
            }
          }
          tickets = ticketCollection.toArray(new Ticket[0]);
          statuses = statusService.getStatuses(tickets);
        } catch(final Throwable throwable) {
          // status service appears to be corrupt, null it out and
          // try to reinitialize it next iteration...
          LOG.warn("Exception thrown while attempting to call getStatuses for service " + serviceUrl);
          LOG.info("Throwable info", throwable);
          statusService = null;
          return;
        }
        // These conditions should never be violated...
        Preconditions.checkState(statuses != null && tickets != null && statuses.length == tickets.length);
        for(int i = 0; i < tickets.length; i++) {
          final Status status = statuses[i];
          final Ticket ticket = tickets[i];
          try {
            jobTracker.updateJob(ticket, status);
          } catch(final Throwable t) {
            ExceptionUtils.logQuietly(LOG, t, "Exception thrown while calling update on callback.");
          }
        }
      }
      jobPollingHalter.haltIfComplete();
    }

    public void setStatusServiceSupplier(final Supplier<StatusService> statusServiceSupplier) {
      this.statusServiceSupplier = statusServiceSupplier;
    }

    public void setJobPollingHalter(final JobPollingHalter jobPollingHalter) {
      this.jobPollingHalter = jobPollingHalter;
    }

    public void setJobTracker(final JobTracker jobTracker) {
      this.jobTracker = jobTracker;
    }

    public void setServiceUrl(final String serviceUrl) {
      this.serviceUrl = serviceUrl;
    }
  }

  public JobPollingRunnable get() {
    final JobPollingRunnableImpl jobPollingRunnableImpl = new JobPollingRunnableImpl();
    return jobPollingRunnableImpl;
  }
}