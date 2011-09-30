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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.persistence.service.IdentificationAnalysisService;

@ManagedBean @ActivityFactoryFor(CreateIdentificationAnalysisDescription.class)
class CreateIdentificationAnalysisActivityFactoryImpl implements ActivityFactory<CreateIdentificationAnalysisDescription> {
  private final IdentificationAnalysisService identificationAnalysisService;
  private final FactorySupport factorySupport;

  @Inject
  CreateIdentificationAnalysisActivityFactoryImpl(final IdentificationAnalysisService identificationAnalysisService, final FactorySupport factorySupport) {
    this.identificationAnalysisService = identificationAnalysisService;
    this.factorySupport = factorySupport;
  }  

  class CreateIdentificationAnalysisActivityImpl extends BaseTropixObjectJobActivityImpl<CreateIdentificationAnalysisDescription, IdentificationAnalysis> {

    protected CreateIdentificationAnalysisActivityImpl(final CreateIdentificationAnalysisDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final IdentificationAnalysis analysis = getModelObject();
      analysis.setIdentificationProgram(getDescription().getAnalysisType()); 
      updateId(identificationAnalysisService.createIdentificationAnalysis(getUserId(), getDescription().getDestinationId(), analysis, getDescription().getAnalysisType(), getDescription().getAnalysisFileId(), getDescription().getRunId(), getDescription().getDatabaseId(), getDescription().getParametersId()));
    }
    
  }
  
  public Activity getActivity(final CreateIdentificationAnalysisDescription activityDescription, final ActivityContext activityContext) {
    return new CreateIdentificationAnalysisActivityImpl(activityDescription, activityContext);
  }

}
