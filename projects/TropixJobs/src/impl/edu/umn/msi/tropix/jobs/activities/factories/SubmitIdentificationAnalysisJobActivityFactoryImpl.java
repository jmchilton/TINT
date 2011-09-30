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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.persistence.service.ParametersService;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagParameters;
import edu.umn.msi.tropix.proteomics.client.IdentificationParametersDeserializer;
import edu.umn.msi.tropix.proteomics.service.MyriMatchJobQueueContext;
import edu.umn.msi.tropix.proteomics.service.OmssaJobQueueContext;
import edu.umn.msi.tropix.proteomics.service.SequestJobQueueContext;
import edu.umn.msi.tropix.proteomics.service.TagReconJobQueueContext;
import edu.umn.msi.tropix.proteomics.service.XTandemJobQueueContext;
import edu.umn.msi.tropix.proteomics.xml.XMLConversionUtilities;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

@ManagedBean
@ActivityFactoryFor(SubmitIdentificationAnalysisDescription.class)
class SubmitIdentificationAnalysisJobActivityFactoryImpl implements ActivityFactory<SubmitIdentificationAnalysisDescription> {
  private static final Log LOG = LogFactory.getLog(SubmitIdentificationAnalysisJobActivityFactoryImpl.class);
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitFactorySupport;
  private final ParametersService parametersService;
  private final IdentificationParametersDeserializer deserializer;

  @Inject
  SubmitIdentificationAnalysisJobActivityFactoryImpl(final FactorySupport factorySupport, final SubmitJobFactorySupport submitFactorySupport,
      final ParametersService parametersService, final IdentificationParametersDeserializer deserializer) {
    this.factorySupport = factorySupport;
    this.submitFactorySupport = submitFactorySupport;
    this.parametersService = parametersService;
    this.deserializer = deserializer;
  }

  class SubmitIdentificationAnalysisJobActivityImpl extends BaseSubmitJobActivityImpl<SubmitIdentificationAnalysisDescription> {

    protected SubmitIdentificationAnalysisJobActivityImpl(final SubmitIdentificationAnalysisDescription activityDescription,
        final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport, submitFactorySupport);
    }

    public void run() throws ShutdownException {
      final String runFileId = ((ProteomicsRun) factorySupport.getTropixObjectService().load(getUserId(), getDescription().getRunId(),
          TropixObjectTypeEnum.PROTEOMICS_RUN)).getMzxml().getId();
      final String databaseFileId = ((Database) factorySupport.getTropixObjectService().load(getUserId(), getDescription().getDatabaseId(),
          TropixObjectTypeEnum.DATABASE)).getDatabaseFile().getId();
      final TransferResource mzxmlResource = getDownloadResource(runFileId);
      final TransferResource databaseResource = getDownloadResource(databaseFileId);
      final String parametersId = getDescription().getParametersId();
      Preconditions.checkNotNull(parametersId, "Parameters id is null");

      FileJobQueueContext context;
      final IdentificationType identificationType = getDescription().getType();
      LOG.debug("Submitting identification of type " + identificationType + " " + getDescription().getParameterType());
      if(identificationType == IdentificationType.SEQUEST) {
        final SequestJobQueueContext sequestContext = createContext(SequestJobQueueContext.class);
        final SequestParameters sequestParameters = parametersService.loadSequestParameters(parametersId);
        sequestContext.submitJob(mzxmlResource, databaseResource, getDelegatedCredential(), XMLConversionUtilities.convert(sequestParameters));
        context = sequestContext;
      } else if(identificationType == IdentificationType.XTANDEM) {
        final XTandemJobQueueContext xTandemContext = createContext(XTandemJobQueueContext.class);
        final XTandemParameters parameters = parametersService.loadXTandemParameters(parametersId);
        xTandemContext.submitJob(mzxmlResource, databaseResource, getDelegatedCredential(), XMLConversionUtilities.convert(parameters));
        context = xTandemContext;
      } else {
        final StorageData storageData = factorySupport.getStorageDataFactory().getPersistedStorageData(parametersId, getCredential());
        final InputContext downloadContext = storageData.getDownloadContext();
        if(identificationType == IdentificationType.OMSSA) {
          final OmssaJobQueueContext omssaContext = createContext(OmssaJobQueueContext.class);
          final MSSearchSettings settings = deserializer.loadParameters(identificationType, downloadContext, MSSearchSettings.class);
          omssaContext.submitJob(mzxmlResource, databaseResource, getDelegatedCredential(), settings);
          context = omssaContext;
        } else if(identificationType == IdentificationType.MYRIMATCH) {
          final MyriMatchJobQueueContext myriMatchContext = createContext(MyriMatchJobQueueContext.class);
          final MyriMatchParameters myriMatchParameters = deserializer.loadParameters(identificationType, downloadContext, MyriMatchParameters.class);
          myriMatchContext.submitJob(mzxmlResource, databaseResource, getDelegatedCredential(), myriMatchParameters);
          context = myriMatchContext;
        } else if(identificationType == IdentificationType.TAGRECON) {
          final TagReconJobQueueContext tagReconContext = createContext(TagReconJobQueueContext.class);
          final TagParameters tagParameters = deserializer.loadParameters(IdentificationType.TAGRECON, downloadContext, TagParameters.class);
          tagReconContext.submitJob(mzxmlResource, databaseResource, getDelegatedCredential(), tagParameters);
          context = tagReconContext;
        } else {
          throw new IllegalStateException("Unknown service type in SubmitIdentificationAnalysisJobActivityFactoryImpl -- "
              + getDescription().getServiceUrl());
        }
      }
      getDescription().setTicket(context.getTicket().getValue());
    }

  }

  public Activity getActivity(final SubmitIdentificationAnalysisDescription activityDescription, final ActivityContext activityContext) {
    return new SubmitIdentificationAnalysisJobActivityImpl(activityDescription, activityContext);
  }
}
