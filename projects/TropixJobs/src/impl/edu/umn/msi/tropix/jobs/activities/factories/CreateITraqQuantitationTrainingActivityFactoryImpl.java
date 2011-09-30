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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateITraqQuantitationTrainingDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationTrainingService;

@ManagedBean @ActivityFactoryFor(CreateITraqQuantitationTrainingDescription.class)
class CreateITraqQuantitationTrainingActivityFactoryImpl implements ActivityFactory<CreateITraqQuantitationTrainingDescription> {
  private final ITraqQuantitationTrainingService service;
  private final FactorySupport factorySupport;

  @Inject
  CreateITraqQuantitationTrainingActivityFactoryImpl(final ITraqQuantitationTrainingService service, final FactorySupport factorySupport) {
    this.service = service;
    this.factorySupport = factorySupport;
  }
  
  class CreateITraqQuantitationTrainingActivityImpl extends BaseTropixObjectJobActivityImpl<CreateITraqQuantitationTrainingDescription, ITraqQuantitationTraining> {

    protected CreateITraqQuantitationTrainingActivityImpl(final CreateITraqQuantitationTrainingDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final ITraqQuantitationTraining analysis = getModelObject();
      updateId(service.createQuantitationTraining(getUserId(), getDescription().getDestinationId(), analysis, getDescription().getReportFileId(), getDescription().getRunIdList().toList().toArray(new String[0]), getDescription().getOutputFileId()));
    }
    
  }

  public Activity getActivity(final CreateITraqQuantitationTrainingDescription description, final ActivityContext activityContext) {
    return new CreateITraqQuantitationTrainingActivityImpl(description, activityContext);
  }
  
}
