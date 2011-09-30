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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityStatus;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/jobs/test/testDatabaseContext.xml"})
@TransactionConfiguration(transactionManager = "jobsTransactionManager", defaultRollback = true)
public class ActivityServiceImplTest extends AbstractTransactionalTestNGSpringContextTests {
  @PersistenceContext(unitName = "jobs")
  private EntityManager entityManager;

  @Inject
  private ActivityService activityService;

  private static ActivityDescription newDescription(final boolean complete) {
    final CreateTropixFileDescription d1 = new CreateTropixFileDescription();
    d1.setActivityStatus(complete ? ActivityStatus.COMPLETE : ActivityStatus.RUNNING);
    return d1;
  }

  /**
   * Tests that loadActivityContexts returns all active contexts and only active
   * contexts.
   */
  @Test(groups = "unit")
  public void testLoad() {
    // c1 has one incomplete description
    final ActivityContext c1 = new ActivityContext();
    c1.setActivityDescription(Sets.<ActivityDescription>newHashSet(newDescription(false)));

    // c2 has one incomplete description and complete description
    final ActivityContext c2 = new ActivityContext();
    c2.setActivityDescription(Sets.<ActivityDescription>newHashSet(newDescription(true), newDescription(false)));

    // c3 has 2 incomplete descriptions
    final ActivityContext c3 = new ActivityContext();
    c3.setActivityDescription(Sets.<ActivityDescription>newHashSet(newDescription(false), newDescription(false)));

    // c4 has 2 complete descriptions
    final ActivityContext c4 = new ActivityContext();
    c4.setActivityDescription(Sets.<ActivityDescription>newHashSet(newDescription(true), newDescription(true)));

    entityManager.persist(c1);
    entityManager.persist(c2);
    entityManager.persist(c3);
    entityManager.flush();
    entityManager.clear();

    final Collection<ActivityContext> loadedContexts = activityService.loadActivityContexts();
    final Collection<String> loadedIds = Collections2.transform(loadedContexts, new Function<ActivityContext, String>() {
      public String apply(final ActivityContext context) {
        return context.getId();
      }
    });
    assert loadedIds.contains(c1.getId());
    assert loadedIds.contains(c2.getId());
    assert loadedIds.contains(c3.getId());
    assert !loadedIds.contains(c4.getId());

  }

}
