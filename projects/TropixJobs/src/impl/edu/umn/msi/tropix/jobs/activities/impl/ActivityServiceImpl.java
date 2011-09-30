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

package edu.umn.msi.tropix.jobs.activities.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;

class ActivityServiceImpl implements ActivityService {
  private EntityManager entityManager;

  @PersistenceContext(unitName = "jobs")
  void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void save(final ActivityContext activityContext) {
    if(activityContext.getId() != null) {
      entityManager.merge(activityContext);

      entityManager.flush();

      entityManager.clear();

    } else {
      entityManager.persist(activityContext);
      
      entityManager.flush();

      entityManager.clear();
    }
  }

  public Collection<ActivityContext> loadActivityContexts() {
    Query query = entityManager.createQuery("select c from ActivityContext as c inner join c.activityDescriptions d where (d.activityStatus <= 1)");     
    @SuppressWarnings("unchecked")
    final List<ActivityContext> contexts = query.getResultList();
    entityManager.flush();
    entityManager.clear();
    return contexts;
  }
  
}
