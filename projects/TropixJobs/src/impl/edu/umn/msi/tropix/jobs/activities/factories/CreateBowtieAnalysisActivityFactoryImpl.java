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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateBowtieAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.persistence.service.BowtieAnalysisService;

@ManagedBean @ActivityFactoryFor(CreateBowtieAnalysisDescription.class)
class CreateBowtieAnalysisActivityFactoryImpl implements ActivityFactory<CreateBowtieAnalysisDescription> {
  private final BowtieAnalysisService bowtieAnalysisService;
  private final FactorySupport factorySupport;
  
  @Inject
  CreateBowtieAnalysisActivityFactoryImpl(final BowtieAnalysisService bowtieAnalysisService, final FactorySupport factorySupport) {
    this.bowtieAnalysisService = bowtieAnalysisService;
    this.factorySupport = factorySupport;
  }

  class CreateBowtieAnalysisActivityImpl extends BaseTropixObjectJobActivityImpl<CreateBowtieAnalysisDescription, BowtieAnalysis> {

    protected CreateBowtieAnalysisActivityImpl(final CreateBowtieAnalysisDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final BowtieAnalysis bowtieAnalysis = getModelObject();
      updateId(bowtieAnalysisService.createBowtieAnalysis(getUserId(), getDescription().getDestinationId(), getDescription().getBowtieIndexId(), bowtieAnalysis, getDescription().getDatabaseIds().toArray(), getDescription().getOutputFileId()));
    }
    
  }

  public Activity getActivity(final CreateBowtieAnalysisDescription activityDescription, final ActivityContext activityContext) {
    return new CreateBowtieAnalysisActivityImpl(activityDescription, activityContext);
  }

}
