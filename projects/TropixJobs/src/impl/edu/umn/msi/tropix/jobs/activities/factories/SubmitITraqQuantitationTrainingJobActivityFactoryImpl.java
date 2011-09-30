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

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitITraqQuantitationTrainingDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.service.ITraqQuantitationJobQueueContext;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean @ActivityFactoryFor(SubmitITraqQuantitationTrainingDescription.class)
class SubmitITraqQuantitationTrainingJobActivityFactoryImpl implements ActivityFactory<SubmitITraqQuantitationTrainingDescription> {
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitFactorySupport;
  
  @Inject
  SubmitITraqQuantitationTrainingJobActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitFactorySupport) {
    this.factorySupport = factorySupport;
    this.submitFactorySupport = submitFactorySupport;
  }

  class SubmitITraqQuantitationTrainingJobActivityImpl extends BaseSubmitJobActivityImpl<SubmitITraqQuantitationTrainingDescription> {

    SubmitITraqQuantitationTrainingJobActivityImpl(final SubmitITraqQuantitationTrainingDescription activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
      super(activityDescription, activityContext, factorySupport, submitJobFactorySupport);
    }

    public void run() throws ShutdownException {
      final ITraqQuantitationJobQueueContext context = createContext(ITraqQuantitationJobQueueContext.class);

      final QuantificationTrainingOptions options = new QuantificationTrainingOptions();
      ParameterUtils.setParametersFromMap(getDescription().getParameterSet().toMap(), options);            
      
      final QuantificationType type = QuantificationType.fromString(getDescription().getQuantificationType());
      
      final TropixObject[] runsAsObjects = factorySupport.getTropixObjectService().load(getUserId(), Iterables.toArray(getDescription().getRunIdList().toList(), String.class), TropixObjectTypeEnum.PROTEOMICS_RUN);
      final List<TransferResource> mzxmlResources = Lists.newArrayList();
      for(TropixObject runAsObject : runsAsObjects) {
        final ProteomicsRun run = (ProteomicsRun) runAsObject;
        final String mzxmlFileId = run.getMzxml().getId();
        mzxmlResources.add(factorySupport.getStorageDataFactory().getPersistedStorageData(mzxmlFileId, getCredential()).prepareDownloadResource());
      }
      
      final StorageData dataReportStorage = factorySupport.getStorageDataFactory().getPersistedStorageData(getDescription().getReportFileId(), getCredential());
      final TransferResource dataReportResource = dataReportStorage.prepareDownloadResource();      
      context.submitTrainingJob(Iterables.toArray(mzxmlResources, TransferResource.class), dataReportResource, type, options, getDelegatedCredential());
      getDescription().setTicket(context.getTicket().getValue());
    }
    
  }
  
  public Activity getActivity(final SubmitITraqQuantitationTrainingDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitITraqQuantitationTrainingJobActivityImpl(activityDescription, activityContext, factorySupport, submitFactorySupport);
  }

}
