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

package edu.umn.msi.tropix.jobs.impl;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.ManagedBean;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.MapMaker;

import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

// Test Both Orders - register -> update and update -> register
@ManagedBean @Named("resultTransferJobUpdateListener")
class ResultTransferLauncherImpl implements ResultTransferLauncher, JobUpdateListener {
  @ThreadSafe
  private class JobState {
    @GuardedBy(value = "this")
    private PollJobDescription pollJobDescription;
    @GuardedBy(value = "this")
    private Credential credential;
    @GuardedBy(value = "this")
    private boolean postProcessed = false;
    @GuardedBy(value = "this")
    private boolean transferTriggered = false;
        
    synchronized void attemptTrigger() {
      if(postProcessed && !transferTriggered && pollJobDescription != null) {
        final String ticket = pollJobDescription.getTicket();
        final FileJobQueueContext context = jobContextClientFactory.getJobClient(credential, pollJobDescription.getServiceUrl(), ticket, FileJobQueueContext.class);
        final String[] ids = transferClient.transferResults(null, context, pollJobDescription.getStorageServiceUrl(), credential);
        pollJobDescription.setFileIds(IdList.forIterable(Arrays.asList(ids)));
        transferTriggered = true;
      }
    }
    
    synchronized void setDescription(final PollJobDescription pollJobDescription, final Credential credential) {
      this.pollJobDescription = pollJobDescription;
      this.credential = credential;
    }

    synchronized void markPostProcessed() {
      this.postProcessed = true;
    }
    
  }
  
  private JobState getJobState(final String ticket) {
    final JobState newState = new JobState();
    stateMap.putIfAbsent(ticket, newState);
    return stateMap.get(ticket);
  }
  
  private final ConcurrentMap<String, JobState> stateMap = new MapMaker().makeMap();
  private final TransferClient transferClient;
  private final JobContextClientFactory jobContextClientFactory;
  
  @Inject
  ResultTransferLauncherImpl(final TransferClient transferClient, final JobContextClientFactory jobContextClientFactory) {
    this.transferClient = transferClient;
    this.jobContextClientFactory = jobContextClientFactory;
  }
  
  public void jobComplete(final Ticket ticketObject, final boolean finishedProperly, final Status finalStatus) {
    final String ticket = ticketObject.getValue();
    stateMap.remove(ticket);
  }
  
  public void register(final Credential credential, @Nonnull final PollJobDescription pollJobDescription) {
    final String ticket = pollJobDescription.getTicket();
    final JobState jobState = getJobState(ticket);
    jobState.setDescription(pollJobDescription, credential);
    jobState.attemptTrigger();
  }
  
  public void update(final Ticket ticketObject, final Status status) {
    if(status.getStage().getValue().equals(StageEnumeration.Postprocessed)) {
      final JobState jobState = getJobState(ticketObject.getValue());
      jobState.markPostProcessed();
      jobState.attemptTrigger();
    }
  }

}
