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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTissueSampleDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.persistence.service.SampleService;

@ManagedBean @ActivityFactoryFor(CreateTissueSampleDescription.class)
class CreateTissueSampleActivityFactoryImpl  implements ActivityFactory<CreateTissueSampleDescription> {
  private final FactorySupport factorySupport;
  private final SampleService sampleService;
  
  @Inject
  CreateTissueSampleActivityFactoryImpl(final SampleService scaffoldService, final FactorySupport factorySupport) {
    this.factorySupport = factorySupport;
    this.sampleService = scaffoldService;
  }
  
  private class CreateScaffoldAnalysisActivityImpl extends BaseTropixObjectJobActivityImpl<CreateTissueSampleDescription, TissueSample> {

    CreateScaffoldAnalysisActivityImpl(final CreateTissueSampleDescription activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() {
      final TissueSample tissueSample = getModelObject();
      updateId(sampleService.createTissueSample(getUserId(), getDestinationId(), tissueSample)); 
    }
    
  }
  
  public Activity getActivity(final CreateTissueSampleDescription activityDescription, final ActivityContext activityContext) {
    return new CreateScaffoldAnalysisActivityImpl(activityDescription, activityContext, factorySupport);
  }


}
