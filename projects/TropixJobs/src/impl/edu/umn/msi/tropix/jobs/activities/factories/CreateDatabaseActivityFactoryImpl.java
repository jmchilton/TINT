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

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.persistence.service.DatabaseService;

@ManagedBean @ActivityFactoryFor(CreateDatabaseDescription.class)
class CreateDatabaseActivityFactoryImpl implements ActivityFactory<CreateDatabaseDescription> {
  private final DatabaseService databaseService;
  private final FactorySupport factorySupport;
  
  @Inject
  CreateDatabaseActivityFactoryImpl(final DatabaseService databaseService, final FactorySupport factorySupport) {
    this.databaseService = databaseService;
    this.factorySupport = factorySupport;
  }  

  class CreateDatabaseActivityImpl extends BaseTropixObjectJobActivityImpl<CreateDatabaseDescription, Database> {

    protected CreateDatabaseActivityImpl(final CreateDatabaseDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() {
      final Database database = getModelObject();
      database.setType(getDescription().getDatabaseType());
      updateId(databaseService.createDatabase(getUserId(), getDescription().getDestinationId(), database, getDescription().getDatabaseFileId()));
    }
    
  }
  
  public Activity getActivity(final CreateDatabaseDescription activityDescription, final ActivityContext activityContext) {
    return new CreateDatabaseActivityImpl(activityDescription, activityContext);
  }
}
