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
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdPickerParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.utils.ModelFunctions;
import edu.umn.msi.tropix.persistence.service.AnalysisService;
import edu.umn.msi.tropix.proteomics.client.IdPickerUtils;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.Sample;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

@ManagedBean @ActivityFactoryFor(CreateIdPickerParametersDescription.class)
class CreateIdPickerParametersActivityFactoryImpl implements ActivityFactory<CreateIdPickerParametersDescription> {
  private final AnalysisService analysisService;

  @Inject
  CreateIdPickerParametersActivityFactoryImpl(final AnalysisService analysisService, final FactorySupport factorySupport) {
    this.analysisService = analysisService;
    this.factorySupport = factorySupport;
  }

  private final FactorySupport factorySupport;
  
  
  class CreateIdPickerParametersActivityImpl extends BaseActivityImpl<CreateIdPickerParametersDescription> {
    
    private Map<String, IdentificationAnalysis> analysesMap;
    protected CreateIdPickerParametersActivityImpl(final CreateIdPickerParametersDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext);
    }
    
    private Database verifyAndGetOnlyDatabase(final Map<String, IdentificationAnalysis> analysesMap) {
      final Map<String, Database> databaseMap = ScaffoldSampleUtils.getDatabaseMap(analysisService, getUserId(), analysesMap);
      final Database firstDatabase = databaseMap.values().iterator().next();
      for(final Database database : databaseMap.values()) {
        Preconditions.checkState(database.equals(firstDatabase));
      }
      return firstDatabase;
    }

    private Sample buildIdPickerSample(final ScaffoldSample scaffoldSample) {
      Sample idPickerSample = new Sample();
      idPickerSample.setName(scaffoldSample.getSampleName());
      final List<String> inputs = Lists.newArrayList();
      for(String analysisId : scaffoldSample.getIdentificationAnalysisIds().toList()) {
        final IdentificationAnalysis analysis = analysesMap.get(analysisId);
        inputs.add(analysis.getName());
      }
      idPickerSample.setInput(Iterables.toArray(inputs, String.class));
      return idPickerSample;      
    }
    
    public void run() throws ShutdownException {
      final Map<String, String> parameters = getDescription().getParameterSet().toMap();     
      
      final List<ScaffoldSample> scaffoldSamples = getDescription().getScaffoldSamples();
      analysesMap = ScaffoldSampleUtils.loadIdentificationAnalyses(getUserId(), scaffoldSamples, factorySupport);
      Preconditions.checkState(analysesMap.values() != null && !Iterables.isEmpty(analysesMap.values())); 
      final Database database = verifyAndGetOnlyDatabase(analysesMap);
      
      final IdPickerParameters idPickerParameters = new IdPickerParameters();
      ParameterUtils.setParametersFromMap(parameters, idPickerParameters);
      final List<Sample> idPickerSamples = Lists.newArrayList();
      for(final ScaffoldSample scaffoldSample : scaffoldSamples) {
        idPickerSamples.add(buildIdPickerSample(scaffoldSample));        
      }
      idPickerParameters.setSample(Iterables.toArray(idPickerSamples, Sample.class));
      
      
      final ModelStorageData inputData = factorySupport.getStorageDataFactory().getStorageData(getDescription().getStorageServiceUrl(), getCredential());
      final OutputContext outputContext = inputData.getUploadContext();
      outputContext.put(IdPickerUtils.serialize(idPickerParameters).getBytes());
      getDescription().setDriverFileId(inputData.getDataIdentifier());
      getDescription().setDatabaseIds(IdList.forIterable(Lists.newArrayList(database.getId())));
      getDescription().setIdentificationAnalysisIds(IdList.forIterable(Collections.transform(analysesMap.values(), ModelFunctions.getIdFunction())));
    }
    
  }
  
  public Activity getActivity(final CreateIdPickerParametersDescription activityDescription, final ActivityContext activityContext) {
    return new CreateIdPickerParametersActivityImpl(activityDescription, activityContext);
  }

}
