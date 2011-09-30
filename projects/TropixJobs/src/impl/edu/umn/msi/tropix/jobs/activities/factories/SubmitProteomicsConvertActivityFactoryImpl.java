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
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitProteomicsConvertDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.proteomics.convert.input.ConvertParameters;
import edu.umn.msi.tropix.proteomics.convert.input.Format;
import edu.umn.msi.tropix.proteomics.service.ProteomicsConvertJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean @ActivityFactoryFor(SubmitProteomicsConvertDescription.class)
class SubmitProteomicsConvertActivityFactoryImpl implements ActivityFactory<SubmitProteomicsConvertDescription> {
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitFactorySupport;
 
  @Inject
  SubmitProteomicsConvertActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitFactorySupport) {
    this.factorySupport = factorySupport;
    this.submitFactorySupport = submitFactorySupport;
  }
  
  class SubmitProteomicsConvertActivityImpl extends BaseSubmitJobActivityImpl<SubmitProteomicsConvertDescription> {

    protected SubmitProteomicsConvertActivityImpl(final SubmitProteomicsConvertDescription activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
      super(activityDescription, activityContext, factorySupport, submitJobFactorySupport);
    }

    public void run() throws ShutdownException {
      final TransferResource inputResource = getDownloadResource(getDescription().getInputFileId());

      final ProteomicsConvertJobQueueContext context = createContext(ProteomicsConvertJobQueueContext.class);

      final ConvertParameters convertParameters = new ConvertParameters();
      convertParameters.setInputFormat(Format.fromString(getDescription().getInputFormat()));
      convertParameters.setOutputFormat(Format.fromString(getDescription().getOutputFormat()));
      convertParameters.setInputName(getDescription().getInputName());

      context.submitJob(inputResource, getDelegatedCredential(), convertParameters);
      getDescription().setTicket(context.getTicket().getValue());
    }
    
  }

  public Activity getActivity(final SubmitProteomicsConvertDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitProteomicsConvertActivityImpl(activityDescription, activityContext, factorySupport, submitFactorySupport);
  }

}
