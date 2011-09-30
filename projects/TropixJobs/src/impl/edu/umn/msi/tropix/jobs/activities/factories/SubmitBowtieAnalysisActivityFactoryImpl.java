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

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;
import edu.umn.msi.tropix.genomics.service.BowtieJobQueueContext;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitBowtieAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean @ActivityFactoryFor(SubmitBowtieAnalysisDescription.class)
class SubmitBowtieAnalysisActivityFactoryImpl implements ActivityFactory<SubmitBowtieAnalysisDescription> {
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitFactorySupport;
 
  @Inject
  SubmitBowtieAnalysisActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitFactorySupport) {
    this.factorySupport = factorySupport;
    this.submitFactorySupport = submitFactorySupport;
  }
  
  class SubmitBowtieAnalysisActivityImpl extends BaseSubmitJobActivityImpl<SubmitBowtieAnalysisDescription> {

    protected SubmitBowtieAnalysisActivityImpl(final SubmitBowtieAnalysisDescription activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
      super(activityDescription, activityContext, factorySupport, submitJobFactorySupport);
    }

    public void run() throws ShutdownException {
      final TropixObject[] databases = factorySupport.getTropixObjectService().load(getUserId(), Iterables.toArray(getDescription().getDatabaseIds().toList(), String.class), TropixObjectTypeEnum.DATABASE);

      final TransferResource[] databaseReferences = new TransferResource[databases.length];
      int i = 0;
      for(final TropixObject database : databases) {
        final ModelStorageData databaseStorageData = factorySupport.getStorageDataFactory().getStorageData(((Database) database).getDatabaseFile(), getCredential());
        databaseReferences[i++] = databaseStorageData.prepareDownloadResource();
      }

      final BowtieIndex index = (BowtieIndex) factorySupport.getTropixObjectService().load(getUserId(), getDescription().getIndexId(), TropixObjectTypeEnum.BOWTIE_INDEX);
      final ModelStorageData indexStorageData = factorySupport.getStorageDataFactory().getStorageData(index.getIndexesFile(), getCredential());
      final TransferResource indexRef = indexStorageData.prepareDownloadResource();
      
      final BowtieJobQueueContext context = createContext(BowtieJobQueueContext.class);

      final BowtieInput bowtieInput = new BowtieInput();
      ParameterUtils.setParametersFromMap(getDescription().getParameterSet().toMap(), bowtieInput);
      bowtieInput.setInputsFormat(getDescription().getInputFromat());
      bowtieInput.setInputsType(getDescription().getInputType());
      context.submitJob(indexRef, databaseReferences, getDelegatedCredential(), bowtieInput);
      getDescription().setTicket(context.getTicket().getValue());
    }
    
  }

  public Activity getActivity(final SubmitBowtieAnalysisDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitBowtieAnalysisActivityImpl(activityDescription, activityContext, factorySupport, submitFactorySupport);
  }

}
