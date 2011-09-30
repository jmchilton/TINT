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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.persistence.service.ScaffoldAnalysisService;

@ManagedBean
@ActivityFactoryFor(CreateScaffoldAnalysisDescription.class)
class CreateScaffoldAnalysisActivityFactoryImpl implements ActivityFactory<CreateScaffoldAnalysisDescription> {
  private final FactorySupport factorySupport;
  private final ScaffoldAnalysisService scaffoldService;

  @Inject
  CreateScaffoldAnalysisActivityFactoryImpl(final FactorySupport factorySupport, final ScaffoldAnalysisService scaffoldService) {
    this.factorySupport = factorySupport;
    this.scaffoldService = scaffoldService;
  }

  private class CreateScaffoldAnalysisActivityImpl extends BaseTropixObjectJobActivityImpl<CreateScaffoldAnalysisDescription, ScaffoldAnalysis> {

    CreateScaffoldAnalysisActivityImpl(final CreateScaffoldAnalysisDescription activityDescription, final ActivityContext activityContext,
        final FactorySupport factorySupport) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() {
      final ScaffoldAnalysis scaffoldAnalysis = getModelObject();
      updateId(scaffoldService.createScaffoldJob(getUserId(), getDestinationId(), scaffoldAnalysis,
          getDescription().getIdentificationIds().toArray(), getDescription().getDriverFileId(), getDescription().getOutputFileId(), getDescription()
              .getScaffoldVersion()));
    }

  }

  public Activity getActivity(final CreateScaffoldAnalysisDescription activityDescription, final ActivityContext activityContext) {
    return new CreateScaffoldAnalysisActivityImpl(activityDescription, activityContext, factorySupport);
  }

}
