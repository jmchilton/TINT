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
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitThermofinniganRunJobDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.proteomics.service.RawExtractJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean @ActivityFactoryFor(SubmitThermofinniganRunJobDescription.class)
class SubmitThermofinniganRunJobActivityFactoryImpl implements ActivityFactory<SubmitThermofinniganRunJobDescription> {
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitJobFactorySupport;

  @Inject
  SubmitThermofinniganRunJobActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
    this.factorySupport = factorySupport;
    this.submitJobFactorySupport = submitJobFactorySupport;
  }
  
  class SubmitThermofinniganRunJobActivityImpl extends BaseSubmitJobActivityImpl<SubmitThermofinniganRunJobDescription> {

    SubmitThermofinniganRunJobActivityImpl(final SubmitThermofinniganRunJobDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport, submitJobFactorySupport);
    }

    public void run() throws ShutdownException {
      final String rawFileId = getDescription().getRawFileId();
      final String rawFileBaseName = getDescription().getRawFileBaseName();
      final TransferResource rawResource = getDownloadResource(rawFileId);
      final RawExtractJobQueueContext context = createContext(RawExtractJobQueueContext.class);
      context.submitJob(rawResource, getDelegatedCredential(), "", rawFileBaseName);
      getDescription().setTicket(context.getTicket().getValue());
    }
    
  }

  public Activity getActivity(final SubmitThermofinniganRunJobDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitThermofinniganRunJobActivityImpl(activityDescription, activityContext);
  }
  
}
