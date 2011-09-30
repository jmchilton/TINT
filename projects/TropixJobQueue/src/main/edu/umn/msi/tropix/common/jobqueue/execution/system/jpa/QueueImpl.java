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

package edu.umn.msi.tropix.common.jobqueue.execution.system.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;

/**
 * Persistent bean representing the actual queue.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
@Entity
@Table(name = "QUEUE")
public class QueueImpl implements Queue {
  @Id
  @Column(name = "ID", nullable = false)
  private Long id;

  @Column(name = "NUM_CREATED_JOBS", nullable = false)
  private Long numCreatedJobs;

  @Column(name = "NUM_PENDING_JOBS", nullable = false)
  private Long numPendingJobs;

  @OneToMany(mappedBy = "queue", fetch = FetchType.EAGER)
  @OrderBy("position ASC")
  // jobs should appear in increasing order of job position
  private List<JobImpl> jobs;

  /**
   * Default constructor required by Hibernate, it does not leave the object in a consistent state. DO NOT USE!
   */
  protected QueueImpl() {
  }

  public QueueImpl(final Long id) {
    this();
    this.id = id;
    this.numCreatedJobs = 0L;
    this.numPendingJobs = 0L;
    this.jobs = new LinkedList<JobImpl>();
  }

  public QueueImpl(final Long id, final Long numCreatedJobs, final Long numPendingJobs, final List<JobImpl> jobImpls) {
    this();
    this.id = id;
    this.numCreatedJobs = numCreatedJobs;
    this.numPendingJobs = numPendingJobs;
    this.jobs = jobImpls;
  }

  public Long getId() {
    return id;
  }

  public Long getNumCreatedJobs() {
    return numCreatedJobs;
  }

  public Long getNumPendingJobs() {
    return numPendingJobs;
  }

  public List<Job> getJobs() {
    final ArrayList<Job> copiedJobs = new ArrayList<Job>(jobs.size());
    copiedJobs.addAll(jobs);
    return copiedJobs;
  }

  public Queue copy() {
    final LinkedList<JobImpl> jobsCopy = new LinkedList<JobImpl>();
    jobsCopy.addAll(jobs);
    return new QueueImpl(id, numCreatedJobs, numPendingJobs, jobsCopy);
  }

  public String toString() {
    return "QueueImpl[id=" + id + ",pending=" + numPendingJobs + ",created=" + numCreatedJobs + "]";
  }

  long getAndIncrementNumCreatedJobs() {
    return numCreatedJobs++;
  }

  JobImpl pushJob(final JobImpl jobImpl) {
    numPendingJobs++;
    jobs.add(jobImpl);
    return jobImpl;
  }

  JobImpl popJob() {
    if(!jobs.isEmpty()) {
      numPendingJobs--;
      return jobs.remove(0);
    } else {
      return null;
    }
  }

  JobImpl remove(final String ticket) {
    JobImpl removedJob = null;
    final Iterator<JobImpl> iter = jobs.iterator();
    while(iter.hasNext()) {
      final JobImpl jobImpl = iter.next();
      if(jobImpl.getTicket().equals(ticket)) {
        iter.remove();
        numPendingJobs--;
        removedJob = jobImpl;
        break;
      }
    }
    return removedJob;
  }

}
