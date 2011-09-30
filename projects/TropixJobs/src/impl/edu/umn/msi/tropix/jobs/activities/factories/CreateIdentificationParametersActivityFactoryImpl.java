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

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.persistence.service.ParametersService;
import edu.umn.msi.tropix.proteomics.client.IdentificationParametersSerializer;
import edu.umn.msi.tropix.proteomics.client.ParameterMapUtils;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

@ManagedBean
@ActivityFactoryFor(CreateIdentificationParametersDescription.class)
class CreateIdentificationParametersActivityFactoryImpl implements ActivityFactory<CreateIdentificationParametersDescription> {
  private final FactorySupport factorySupport;
  private final ParametersService parametersService;
  private final ParameterMapUtils sequestParameterUtils;
  private final ParameterMapUtils xTandemParameterUtils;
  private final IdentificationParametersSerializer parametersSerializer;

  @Inject
  public CreateIdentificationParametersActivityFactoryImpl(final FactorySupport factorySupport, final ParametersService parametersService,
      @Named("sequestParameterUtils") final ParameterMapUtils sequestParameterUtils,
      @Named("xTandemParameterUtils") final ParameterMapUtils xTandemParameterUtils, final IdentificationParametersSerializer parametersSerializer) {
    this.factorySupport = factorySupport;
    this.parametersService = parametersService;
    this.sequestParameterUtils = sequestParameterUtils;
    this.xTandemParameterUtils = xTandemParameterUtils;
    this.parametersSerializer = parametersSerializer;
  }

  private class CreateIdentificationParametersActivityImpl extends
      BaseTropixObjectJobActivityImpl<CreateIdentificationParametersDescription, IdentificationParameters> {

    protected CreateIdentificationParametersActivityImpl(final CreateIdentificationParametersDescription activityDescription,
        final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final IdentificationParameters identificationParameters = getModelObject();
      final String parameterTypeStr = getDescription().getParameterType();
      identificationParameters.setType(parameterTypeStr);
      final IdentificationType parameterType = IdentificationType.forParameters(identificationParameters);
      final Map<String, String> parametersMap = getDescription().getParameters().toMap();
      final IdentificationParameters savedParameters;
      // Someday I need to convert the Sequest and X! Tandem table to XML, so this isn't a special case.
      Preconditions.checkState(parameterType != null, "Instance of CreateIdentificationParametersDescription did not specify parameter type.");
      if(parameterType.equals(IdentificationType.SEQUEST)) {
        sequestParameterUtils.toRaw(parametersMap);
        final SequestParameters sequestParameters = new SequestParameters();
        ParameterUtils.setParametersFromMap(parametersMap, sequestParameters);
        savedParameters = parametersService.createSequestParameters(getUserId(), getDescription().getDestinationId(), identificationParameters,
            sequestParameters);
      } else if(parameterType.equals(IdentificationType.XTANDEM)) {
        xTandemParameterUtils.toRaw(parametersMap);
        final XTandemParameters xTandemParameters = new XTandemParameters();
        ParameterUtils.setParametersFromMap(parametersMap, xTandemParameters);
        savedParameters = parametersService.createXTandemParameters(getUserId(), getDescription().getDestinationId(), identificationParameters,
            xTandemParameters);
      } else {
        final String serializedParameters = parametersSerializer.serializeParameters(parameterType, parametersMap);
        final ModelStorageData paramData = factorySupport.getStorageDataFactory().getStorageData(getDescription().getStorageServiceUrl(),
            getCredential());
        paramData.getUploadContext().put(serializedParameters.getBytes());
        savedParameters = parametersService.createXmlParameters(getUserId(), getDestinationId(), identificationParameters, paramData.getTropixFile());
      }
      getDescription().setParametersId(savedParameters.getParametersId());
      updateId(savedParameters);
    }

  }

  public Activity getActivity(final CreateIdentificationParametersDescription activityDescription, final ActivityContext activityContext) {
    return new CreateIdentificationParametersActivityImpl(activityDescription, activityContext);
  }

}
