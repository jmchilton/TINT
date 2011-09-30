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

import net.jmchilton.concurrent.CountDownLatch;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.jobqueue.client.JobUpdateListener;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;

public class DelegatingJobUpdateListenerImpl implements JobUpdateListener {
  private Iterable<JobUpdateListener> jobUpdateListeners;
  private CountDownLatch updateLatch = new CountDownLatch(1);
  
  public void jobComplete(final Ticket ticket, final boolean finishedProperly, final Status finalStatus) {
    updateLatch.await();
    for(JobUpdateListener jobUpdateListener : jobUpdateListeners) {
      jobUpdateListener.jobComplete(ticket, finishedProperly, finalStatus);
    }
  }

  public void update(final Ticket ticket, final Status status) {
    updateLatch.await();
    for(JobUpdateListener jobUpdateListener : jobUpdateListeners) {
      jobUpdateListener.update(ticket, status);      
    }
  }

  public void setJobUpdateListener(final JobUpdateListener jobUpdateListener) {
    this.jobUpdateListeners = Lists.newArrayList(jobUpdateListener);
    updateLatch.countDown();
  }
  
  public void setJobUpdateListeners(final Iterable<JobUpdateListener> jobUpdateListeners) {
    this.jobUpdateListeners = Lists.newArrayList(jobUpdateListeners);
    updateLatch.countDown();
  }
  
}
