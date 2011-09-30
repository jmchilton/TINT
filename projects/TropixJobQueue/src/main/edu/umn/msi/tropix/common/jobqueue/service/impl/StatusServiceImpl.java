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

package edu.umn.msi.tropix.common.jobqueue.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class StatusServiceImpl implements StatusService {
  private static final Log LOG = LogFactory.getLog(StatusServiceImpl.class);
  private JobProcessorQueue<?> jobQueue;

  public Status[] getStatuses(final Ticket[] tickets) {
    final Status[] statuses = new Status[tickets.length];
    for(int i = 0; i < tickets.length; i++) {
      final Ticket ticket = tickets[i];
      Status status = null;
      try {
        status = jobQueue.getStatus(ticket);
      } catch(final RuntimeException e) {
        ExceptionUtils.logQuietly(LOG, e, "Failed to obtain status for ticket " + ticket.getValue());
      }
      statuses[i] = status;
    }
    return statuses;
  }

  public void setJobProcessorQueue(final JobProcessorQueue<?> jobQueue) {
    LOG.debug("Setting JobProcessorQueue to  " + jobQueue);
    this.jobQueue = jobQueue;
  }

}
