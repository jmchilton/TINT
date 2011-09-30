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

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;

/**
 * JobTracker implementations will be able to support concurrent reads (getTickets) and writes (addJob, completeJob, completeAllJobs).
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
public interface JobTracker {
  boolean hasJobs();

  void addJob(Ticket ticket, Job job);

  void updateJob(Ticket ticket, Status status);

  // public void completeJob(Ticket ticket, boolean finishedProperly);

  void completeAllJobs(boolean finishedProperly);

  /**
   * @return A snapshot of the tickets. Changing this collection will not effect the JobTracker.
   */
  Collection<Ticket> getTickets();
}
