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

package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.ManagedBean;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import edu.umn.msi.tropix.common.concurrent.Haltable;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobPoller;
import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.jobs.activities.impl.Cancellable;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.impl.ResultTransferLauncher;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

@ManagedBean @ActivityFactoryFor(PollJobDescription.class) @Named("pollJobUpdateListener")
class PollJobActivityFactoryImpl implements Haltable, JobUpdateListener, ActivityFactory<PollJobDescription> {
  private static final Log LOG = LogFactory.getLog(PollJobActivityFactoryImpl.class);
  private static <T> Function<String, LinkedBlockingQueue<T>> getQueueFunction(final int max) {
    return new Function<String, LinkedBlockingQueue<T>>() {
      public LinkedBlockingQueue<T> apply(final String arg) {
        return new LinkedBlockingQueue<T>(max);
      }
    };
  }
  
  private static class FinalStatus {
    private final Status status;
    private final boolean completedNormally;
    
    FinalStatus(final Status status, final boolean completedNormally) {
      this.status = status;
      this.completedNormally = completedNormally;
    }
    
  }
  
  private final AtomicBoolean shutdown = new AtomicBoolean(false);
  private final ConcurrentMap<String, EventBase> updateDestinations = new MapMaker().makeMap();
  private final ConcurrentMap<String, LinkedBlockingQueue<FinalStatus>> completeQueues = new MapMaker().makeComputingMap(PollJobActivityFactoryImpl.<FinalStatus>getQueueFunction(1));
  private final FactorySupport factorySupport;
  private final JobContextClientFactory jobContextClientFactory;
  private final ResultTransferLauncher resultTransferLauncher;
  private JobPoller jobPoller;
  
  @Inject
  PollJobActivityFactoryImpl(final FactorySupport factorySupport, final JobContextClientFactory jobContextClientFactory, final ResultTransferLauncher resultTransferLauncher) {
    System.out.println("Created PollJobActivityFactoryImpl");
    this.factorySupport = factorySupport;
    this.jobContextClientFactory = jobContextClientFactory;
    this.resultTransferLauncher = resultTransferLauncher;
  }
  
  // This cannot be set via constructor because a circular dependency JobPoller -> UpdateListener -> PollJobActivityFactoryImpl -> JobPoller
  @Inject
  public void setJobPoller(final JobPoller jobPoller) {
    this.jobPoller = jobPoller;
  }
  
  
  // Using map as a set.
  @GuardedBy("threads")
  private final ConcurrentMap<Thread, Thread> threads = new MapMaker().weakKeys().weakValues().makeMap();

  public void shutdown() {
    synchronized(threads) {
      shutdown.set(true);
      for(Thread thread : threads.values()) {
        thread.interrupt();
      }
    }
  }

  class PollJobActivityImpl extends BaseActivityImpl<PollJobDescription> implements Cancellable {

    PollJobActivityImpl(final PollJobDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext);
    }

    private void registerThreadForShutdownInterruption() {
      synchronized(threads) {
        if(shutdown.get()) {
          throw new ShutdownException();
        }
        final Thread thread = Thread.currentThread();
        threads.put(thread, thread);
      }      
    }

    public void run() throws ShutdownException {
      resultTransferLauncher.register(getCredential(), getDescription());
      registerThreadForShutdownInterruption();
      final String ticket = getDescription().getTicket();
      
      final Job job = new Job();
      job.setProxy(getCredential());
      job.setServiceAddress(getDescription().getServiceUrl());
      job.setTicket(new Ticket(getDescription().getTicket()));

      final EventBase eventBase = getEventBase();
      updateDestinations.put(ticket, eventBase);
      jobPoller.pollJob(job);
      
      final LinkedBlockingQueue<FinalStatus> queue = completeQueues.get(ticket);
      FinalStatus finalStatus;
      while(true) {
        try {
          finalStatus = queue.take();
          break;
        } catch(Exception e) {
          if(shutdown.get()) {
            throw new ShutdownException();
          }
          ExceptionUtils.logQuietly(LOG, e, "Exception while waiting on countdown latch");
        }
      }
      
      updateDestinations.remove(ticket);
      completeQueues.remove(ticket);
      // While seemingly unlikely, there is no guarantee this is the last update that will be sent. 
      // The destination is no longer present in updateDestinations, but a context switch may have occurred  
      // while processing the last event.
      if(finalStatus.status != null) {
        factorySupport.getEventSupport().gridStatus(eventBase, finalStatus.status);
      }
      if(!finalStatus.completedNormally) {
        throw new RuntimeException("Job not completed normally.");
      }
    }

    public void cancel() {
      final JobQueueContext context = jobContextClientFactory.getJobClient(getCredential(), getDescription().getServiceUrl(), getDescription().getTicket(), JobQueueContext.class);
      context.cancel();
    }

  }

  public void jobComplete(final Ticket ticket, final boolean finishedProperly, final Status status) {
    FinalStatus finalStatus = new FinalStatus(status, finishedProperly);
    completeQueues.get(ticket.getValue()).add(finalStatus);
  }

  public void update(final Ticket ticket, final Status status) {
    EventBase eventBase = updateDestinations.get(ticket.getValue());
    if(eventBase != null) {
      factorySupport.getEventSupport().gridStatus(eventBase, status);
    }
  }

  public Activity getActivity(final PollJobDescription activityDescription, final ActivityContext activityContext) {
    return new PollJobActivityImpl(activityDescription, activityContext);
  }

}
