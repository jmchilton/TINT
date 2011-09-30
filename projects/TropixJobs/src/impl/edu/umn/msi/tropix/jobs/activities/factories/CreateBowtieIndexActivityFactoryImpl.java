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

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateBowtieIndexDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.persistence.service.BowtieIndexService;

@ManagedBean @ActivityFactoryFor(CreateBowtieIndexDescription.class)
class CreateBowtieIndexActivityFactoryImpl implements ActivityFactory<CreateBowtieIndexDescription> {
  private final BowtieIndexService bowtieIndexService;
  private final FactorySupport factorySupport;
  
  @Inject
  CreateBowtieIndexActivityFactoryImpl(final BowtieIndexService bowtieIndexService, final FactorySupport factorySupport) {
    this.bowtieIndexService = bowtieIndexService;
    this.factorySupport = factorySupport;
  }

  class CreateBowtieIndexActivityImpl extends BaseTropixObjectJobActivityImpl<CreateBowtieIndexDescription, BowtieIndex> {

    protected CreateBowtieIndexActivityImpl(final CreateBowtieIndexDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final BowtieIndex bowtieIndex = getModelObject();
      updateId(bowtieIndexService.createBowtieIndex(getUserId(), getDescription().getDestinationId(), bowtieIndex, getDescription().getIndexFileId()));
    }
    
  }

  public Activity getActivity(final CreateBowtieIndexDescription activityDescription, final ActivityContext activityContext) {
    return new CreateBowtieIndexActivityImpl(activityDescription, activityContext);
  }

}
