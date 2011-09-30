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
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

@ManagedBean @ActivityFactoryFor(CommitObjectDescription.class)
class CommitObjectActivityFactoryImpl implements ActivityFactory<CommitObjectDescription> {
  private final FactorySupport factorySupport;
  private final TropixObjectService tropixObjectService;
  
  private class CommitObjectActivityImpl extends BaseActivityImpl<CommitObjectDescription> {

    protected CommitObjectActivityImpl(final CommitObjectDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext);
    }

    public void run() {
      final String objectId = getDescription().getObjectId();
      tropixObjectService.commit(getUserId(), objectId);
      final String destinationId = getDescription().getDestinationId();
      if(destinationId != null) {
        factorySupport.getEventSupport().objectAdded(getEventBase(), objectId, destinationId);
      }
    }
    
  }
  
  
  @Inject
  CommitObjectActivityFactoryImpl(final TropixObjectService tropixObjectService, final FactorySupport factorySupport) {
    this.tropixObjectService = tropixObjectService;
    this.factorySupport = factorySupport;
  }


  public Activity getActivity(final CommitObjectDescription activityDescription, final ActivityContext activityContext) {
    return new CommitObjectActivityImpl(activityDescription, activityContext);
  }

}
