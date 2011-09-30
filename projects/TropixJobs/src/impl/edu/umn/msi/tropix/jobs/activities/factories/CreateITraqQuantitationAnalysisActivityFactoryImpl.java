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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateITraqQuantitationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationAnalysisService;

@ManagedBean @ActivityFactoryFor(CreateITraqQuantitationAnalysisDescription.class)
class CreateITraqQuantitationAnalysisActivityFactoryImpl implements ActivityFactory<CreateITraqQuantitationAnalysisDescription> {
  private final ITraqQuantitationAnalysisService service;
  private final FactorySupport factorySupport;

  @Inject
  CreateITraqQuantitationAnalysisActivityFactoryImpl(final ITraqQuantitationAnalysisService service, final FactorySupport factorySupport) {
    this.service = service;
    this.factorySupport = factorySupport;
  }
  
  class CreateITraqQuantitationAnalysisActivityImpl extends BaseTropixObjectJobActivityImpl<CreateITraqQuantitationAnalysisDescription, ITraqQuantitationAnalysis> {

    protected CreateITraqQuantitationAnalysisActivityImpl(final CreateITraqQuantitationAnalysisDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final ITraqQuantitationAnalysis analysis = getModelObject();
      updateId(service.createQuantitationAnalysis(getUserId(), getDescription().getDestinationId(), analysis, getDescription().getReportFileId(), getDescription().getRunIdList().toArray(), getDescription().getTrainingId(), getDescription().getOutputFileId()));
    }
    
  }

  public Activity getActivity(final CreateITraqQuantitationAnalysisDescription description, final ActivityContext activityContext) {
    return new CreateITraqQuantitationAnalysisActivityImpl(description, activityContext);
  }
  
}
