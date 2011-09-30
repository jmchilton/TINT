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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class PersistentGramExecutionJobServiceImpl implements GramExecutionJobService {
  private EntityManager entityManager;

  @PersistenceContext(unitName = "gramqueue")
  public void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void delete(final String localJobId) {
    final GramExecutionJobImpl job = entityManager.find(GramExecutionJobImpl.class, localJobId);
    entityManager.remove(job);
    entityManager.flush();
    entityManager.clear();
  }

  public GramExecutionJob loadJob(final String localJobId) {
    final GramExecutionJobImpl job = entityManager.find(GramExecutionJobImpl.class, localJobId);
    GramExecutionJobImpl jobCopy = null;
    if(job != null) {
      jobCopy = new GramExecutionJobImpl(job);
    }
    return jobCopy;
  }

  public Collection<GramExecutionJob> loadJobs() {
    final Query query = entityManager.createQuery("select job from GramExecutionJobImpl job");
    @SuppressWarnings("unchecked")
    final List<GramExecutionJobImpl> persistentJobs = query.getResultList();
    final List<GramExecutionJob> copiedJobs = new ArrayList<GramExecutionJob>(persistentJobs.size());
    for(final GramExecutionJobImpl job : persistentJobs) {
      copiedJobs.add(new GramExecutionJobImpl(job));
    }
    return copiedJobs;
  }

  public GramExecutionJob persistJob(final String handle, final String description, final String localJobId, final String proxy, final String state) {
    final GramExecutionJobImpl job = new GramExecutionJobImpl(localJobId, description, handle, proxy, state);
    entityManager.persist(job);
    entityManager.flush();
    entityManager.clear();
    return job;
  }

  public void updateState(final String localJobId, final String state) {
    final GramExecutionJobImpl job = entityManager.find(GramExecutionJobImpl.class, localJobId);
    job.setState(state);
    entityManager.flush();
    entityManager.clear();
  }

}
