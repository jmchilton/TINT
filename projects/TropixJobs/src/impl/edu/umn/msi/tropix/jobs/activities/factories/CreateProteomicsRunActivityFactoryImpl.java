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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.persistence.service.ProteomicsRunService;

// TODO: Implement rollback...
@ManagedBean @ActivityFactoryFor(CreateProteomicsRunDescription.class)
class CreateProteomicsRunActivityFactoryImpl implements ActivityFactory<CreateProteomicsRunDescription> {
  private ProteomicsRunService proteomicsRunService;
  private final FactorySupport factorySupport;

  @Inject
  CreateProteomicsRunActivityFactoryImpl(final ProteomicsRunService proteomicsRunService, final FactorySupport factorySupport) {
    this.proteomicsRunService = proteomicsRunService;
    this.factorySupport = factorySupport;
  }
  
  private class CreateProteomicsRunActivity extends BaseTropixObjectJobActivityImpl<CreateProteomicsRunDescription, ProteomicsRun> implements Activity {

    CreateProteomicsRunActivity(final CreateProteomicsRunDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final ProteomicsRun run = getModelObject();
      updateId(proteomicsRunService.createProteomicsRun(getUserId(), getDescription().getDestinationId(), run, getDescription().getMzxmlFileId(), getDescription().getSampleId(), getDescription().getSourceId()));
    }
    
  }
  
  public Activity getActivity(final CreateProteomicsRunDescription activityDescription, final ActivityContext activityContext) {
    return new CreateProteomicsRunActivity(activityDescription, activityContext);
  }

}
