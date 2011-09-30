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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class JobServiceImpl implements JobService {
  private EntityManager entityManager;

  @PersistenceContext(unitName = "jobclient")
  public synchronized void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public synchronized boolean jobExists(final Ticket ticket) {
    final String ticketValue = ticket.getValue();

    final PersistentJob job = entityManager.find(PersistentJob.class, ticketValue);

    entityManager.flush();

    entityManager.clear();

    return job != null;
  }

  public synchronized void dropJob(final Ticket ticket) {
    final String ticketValue = ticket.getValue();

    final PersistentJob job = entityManager.find(PersistentJob.class, ticketValue);

    entityManager.remove(job);

    entityManager.flush();

    entityManager.clear();
  }

  public synchronized Collection<Job> getJobs() {
    final Query query = entityManager.createQuery("select job from PersistentJob job");
    @SuppressWarnings("unchecked")
    final List<PersistentJob> persistentJobs = query.getResultList();

    final List<Job> jobs = new ArrayList<Job>(persistentJobs.size());
    for(final PersistentJob pJob : persistentJobs) {
      final Job job = new Job();
      job.setJobType(pJob.getJobType());
      job.setServiceAddress(pJob.getServiceUrl());
      final String proxy = pJob.getProxy();
      if(proxy != null) {
        job.setProxy(Credentials.fromString(pJob.getProxy()));
      }
      job.setTicket(new Ticket(pJob.getTicket()));
      jobs.add(job);
    }
    return jobs;
  }

  public synchronized void saveJob(final Job job) {
    final PersistentJob persistentJob = new PersistentJob();
    persistentJob.setJobType(job.getJobType());
    persistentJob.setServiceUrl(job.getServiceAddress());
    persistentJob.setTicket(job.getTicket().getValue());
    final Credential proxy = job.getProxy();
    if(proxy != null) {
      persistentJob.setProxy(job.getProxy().toString());
    }
    entityManager.persist(persistentJob);
    entityManager.flush();
    entityManager.clear();
  }

}
