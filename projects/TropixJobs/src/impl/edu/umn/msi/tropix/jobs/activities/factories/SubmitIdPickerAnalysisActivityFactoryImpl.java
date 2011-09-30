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

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdPickerAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.proteomics.client.IdPickerUtils;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.service.IdPickerJobQueueContext;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean @ActivityFactoryFor(SubmitIdPickerAnalysisDescription.class)
class SubmitIdPickerAnalysisActivityFactoryImpl implements ActivityFactory<SubmitIdPickerAnalysisDescription> {
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitJobFactorySupport;
  
  @Inject
  SubmitIdPickerAnalysisActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
    this.factorySupport = factorySupport;
    this.submitJobFactorySupport = submitJobFactorySupport;
  }
  
  private class SubmitIdPickerAnalysisActivityImpl extends BaseSubmitJobActivityImpl<SubmitIdPickerAnalysisDescription> {

    protected SubmitIdPickerAnalysisActivityImpl(final SubmitIdPickerAnalysisDescription activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
      super(activityDescription, activityContext, factorySupport, submitJobFactorySupport);
    }

    public void run() throws ShutdownException {
      final TropixObject[] databases = factorySupport.getTropixObjectService().load(getUserId(), getDescription().getDatabaseIds().toArray(), TropixObjectTypeEnum.DATABASE);
      final TropixObject[] analyses = factorySupport.getTropixObjectService().load(getUserId(), getDescription().getIdentificationIds().toArray(), TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS);

      final List<TransferResource> pepXmlReferences = Lists.newLinkedList();
      for(final TropixObject curAnalysis : analyses) {
        pepXmlReferences.add(factorySupport.getStorageDataFactory().getStorageData(((IdentificationAnalysis) curAnalysis).getOutput(), getCredential()).prepareDownloadResource());
      }

      final Database database = (Database) databases[0];
      final TransferResource databaseResource = factorySupport.getStorageDataFactory().getStorageData(database.getDatabaseFile(), getCredential()).prepareDownloadResource();
      
      final ModelStorageData inputData = factorySupport.getStorageDataFactory().getPersistedStorageData(getDescription().getDriverFileId(), getCredential());
      final IdPickerParameters parameters = IdPickerUtils.deserialize(inputData.getDownloadContext());

      final IdPickerJobQueueContext context = createContext(IdPickerJobQueueContext.class);
      context.submitJob(pepXmlReferences.toArray(new TransferResource[pepXmlReferences.size()]), databaseResource, getDelegatedCredential(), parameters);
      getDescription().setTicket(context.getTicket().getValue());     
    }
    
  }

  public Activity getActivity(final SubmitIdPickerAnalysisDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitIdPickerAnalysisActivityImpl(activityDescription, activityContext, factorySupport, submitJobFactorySupport);
  }
  
}
