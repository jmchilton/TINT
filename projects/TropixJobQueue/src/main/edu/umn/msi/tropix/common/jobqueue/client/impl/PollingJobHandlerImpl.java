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

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.concurrent.Haltable;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobPoller;
import edu.umn.msi.tropix.common.jobqueue.client.StatusServiceFactory;
import edu.umn.msi.tropix.common.jobqueue.client.persistent.JobService;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class PollingJobHandlerImpl implements JobPoller {
  private static final Log LOG = LogFactory.getLog(PollingJobHandlerImpl.class);

  // Managed internally
  private Map<String, JobTracker> jobTrackerMap = Maps.newHashMap();

  // Set externally
  private Supplier<JobTracker> jobTrackerSupplier;
  private Supplier<JobPollingRunnable> jobPollingRunnableSupplier;
  private Supplier<LoopingRunnable> loopingRunnableSupplier;
  private Executor jobPollingExecutor;
  private StatusServiceFactory statusServiceFactory;

  private boolean failJobIfCannotPersist = false;
  private Credential hostProxy = null;
  private JobService jobService = null;

  private class JobPollingHalterImpl implements JobPollingHalter {
    private String serviceUrl;
    private Haltable haltable;

    public void halt() {
      synchronized(jobTrackerMap) {
        haltable.shutdown();
        final JobTracker jobTracker = jobTrackerMap.remove(serviceUrl);
        jobTracker.completeAllJobs(false);
      }
    }

    public void haltIfComplete() {
      synchronized(jobTrackerMap) {
        final JobTracker jobTracker = jobTrackerMap.get(serviceUrl);
        if(!jobTracker.hasJobs()) {
          halt();
        }
      }
    }
  }

  public void pollJob(final Job job) {
    final String serviceUrl = job.getServiceAddress();
    final Ticket ticket = job.getTicket();

    synchronized(jobTrackerMap) {
      if(!jobTrackerMap.containsKey(serviceUrl)) {
        // Create a new job tracker...
        final JobTracker jobTracker = jobTrackerSupplier.get();
        jobTracker.addJob(ticket, job);
        jobTrackerMap.put(serviceUrl, jobTracker);

        final JobPollingRunnable jobPollingRunnable = jobPollingRunnableSupplier.get();

        final LoopingRunnable loopingRunnable = loopingRunnableSupplier.get();
        loopingRunnable.setBaseRunnable(jobPollingRunnable);

        final JobPollingHalterImpl jobPollingHalter = new JobPollingHalterImpl();
        jobPollingHalter.serviceUrl = serviceUrl;
        jobPollingHalter.haltable = loopingRunnable;

        final Supplier<StatusService> statusServiceSupplier = new Supplier<StatusService>() {
          public StatusService get() {
            return statusServiceFactory.getStatusService(serviceUrl, hostProxy);
          }
        };

        jobPollingRunnable.setJobTracker(jobTracker);
        jobPollingRunnable.setServiceUrl(serviceUrl);
        jobPollingRunnable.setJobPollingHalter(jobPollingHalter);
        jobPollingRunnable.setStatusServiceSupplier(statusServiceSupplier);
        LOG.debug("Starting new poller for url " + serviceUrl);
        jobPollingExecutor.execute(loopingRunnable);
      } else {
        LOG.debug("Adding another ticket to poller at url " + serviceUrl);
        jobTrackerMap.get(serviceUrl).addJob(ticket, job);
      }
    }
  }

  public void setHostProxy(final Credential hostProxy) {
    this.hostProxy = hostProxy;
  }

  public void setJobTrackerSupplier(final Supplier<JobTracker> jobTrackerSupplier) {
    this.jobTrackerSupplier = jobTrackerSupplier;
  }

  public void setJobPollingRunnableSupplier(final Supplier<JobPollingRunnable> jobPollingRunnableSupplier) {
    this.jobPollingRunnableSupplier = jobPollingRunnableSupplier;
  }

  public void setLoopingRunnableSupplier(final Supplier<LoopingRunnable> loopingRunnableSupplier) {
    this.loopingRunnableSupplier = loopingRunnableSupplier;
  }

  public void setJobPollingExecutor(final Executor jobPollingExecutor) {
    this.jobPollingExecutor = jobPollingExecutor;
  }

  public void setStatusServiceFactory(final StatusServiceFactory statusServiceFactory) {
    this.statusServiceFactory = statusServiceFactory;
  }
}
