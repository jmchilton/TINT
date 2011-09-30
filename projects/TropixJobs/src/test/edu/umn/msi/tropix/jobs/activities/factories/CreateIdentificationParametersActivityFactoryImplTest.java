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

import java.util.UUID;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameter;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.persistence.service.ParametersService;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.ParameterType;
import edu.umn.msi.tropix.proteomics.client.IdentificationParametersSerializer;
import edu.umn.msi.tropix.proteomics.client.ParameterMapUtils;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class CreateIdentificationParametersActivityFactoryImplTest extends
    BaseCreateActivityFactoryImplTest<CreateIdentificationParametersDescription, IdentificationParameters> {
  private CreateIdentificationParametersActivityFactoryImpl factory;
  private ParametersService parametersService;
  private ParameterMapUtils sequestParameterUtils;
  private ParameterMapUtils xTandemParameterUtils;
  private MockObjectCollection mockObjects;
  private String parametersId;
  private StringParameterSet parameterSet;
  private IdentificationParametersSerializer serializer;

  @Override
  protected void initReturnedObject(final IdentificationParameters identificationParameters) {
    identificationParameters.setParametersId(parametersId);
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    parametersService = EasyMock.createMock(ParametersService.class);
    sequestParameterUtils = EasyMock.createMock(ParameterMapUtils.class);
    xTandemParameterUtils = EasyMock.createMock(ParameterMapUtils.class);
    parametersId = UUID.randomUUID().toString();
    serializer = EasyMock.createMock(IdentificationParametersSerializer.class);

    factory = new CreateIdentificationParametersActivityFactoryImpl(getFactorySupport(), parametersService, sequestParameterUtils,
        xTandemParameterUtils, serializer);

    mockObjects = MockObjectCollection.fromObjects(parametersService, sequestParameterUtils, xTandemParameterUtils, serializer);
    getDescription().setStorageServiceUrl("http://storage/" + UUID.randomUUID().toString());
    parameterSet = new StringParameterSet();
    getDescription().setParameters(parameterSet);
  }

  private void run() {
    mockObjects.replay();
    runAndVerify(factory);
    mockObjects.verifyAndReset();
    assert parametersId.equals(getDescription().getParametersId());
  }

  @Test(groups = "unit")
  public void testCreateSequest() {
    setParameterType(ParameterType.SequestBean);
    addParameter("internalCleavageSites", "4");
    sequestParameterUtils.toRaw(parameterSet.toMap());

    final Capture<SequestParameters> sequestParametersCapture = EasyMockUtils.newCapture();
    parametersService.createSequestParameters(matchId(), matchDestinationId(), captureObject(), EasyMock.capture(sequestParametersCapture));
    returnInitializedObject();

    run();

    final SequestParameters parameters = sequestParametersCapture.getValue();
    assert parameters.getInternalCleavageSites() == 4;
  }

  private void addParameter(final String key, final String value) {
    final StringParameter parameter = new StringParameter();
    parameter.setKey(key);
    parameter.setValue(value);
    parameterSet.addParameter(parameter);
  }

  @Test(groups = "unit")
  public void testCreateXTandem() {
    setParameterType(ParameterType.XTandemBean);
    addParameter("scoringMinimumIonCount", "42");
    xTandemParameterUtils.toRaw(parameterSet.toMap());

    final Capture<XTandemParameters> xTandemParametersCapture = EasyMockUtils.newCapture();
    parametersService.createXTandemParameters(matchId(), matchDestinationId(), captureObject(), EasyMock.capture(xTandemParametersCapture));
    returnInitializedObject();

    run();

    final XTandemParameters parameters = xTandemParametersCapture.getValue();
    assert parameters.getScoringMinimumIonCount() == 42;
  }

  @Test(groups = "unit")
  public void testCreateMyriMatch() {
    setParameterType(ParameterType.MyriMatch);
    addParameter("ClassSizeMultiplier", "13");

    final Capture<TropixFile> paramFileCapture = EasyMockUtils.newCapture();
    EasyMock.expect(serializer.serializeParameters(IdentificationType.MYRIMATCH, parameterSet.toMap())).andReturn("myrimoo");
    parametersService.createXmlParameters(matchId(), matchDestinationId(), captureObject(), EasyMock.capture(paramFileCapture));
    returnInitializedObject();

    run();

    final ModelStorageData storageData = getNewStorageData();
    assert paramFileCapture.getValue().equals(storageData.getTropixFile());
    assert InputContexts.toString(storageData.getDownloadContext()).equals("myrimoo");
  }

  @Test(groups = "unit")
  public void testCreateOmssa() {
    setParameterType(ParameterType.OmssaXml);
    EasyMock.expect(serializer.serializeParameters(IdentificationType.OMSSA, parameterSet.toMap())).andReturn("moo");

    final Capture<TropixFile> paramFileCapture = EasyMockUtils.newCapture();
    parametersService.createXmlParameters(matchId(), matchDestinationId(), captureObject(), EasyMock.capture(paramFileCapture));
    returnInitializedObject();

    run();
    final ModelStorageData storageData = getNewStorageData();
    assert storageData.getTropixFile().equals(paramFileCapture.getValue());
    InputContexts.toString(storageData.getDownloadContext()).equals("moo");
  }

  private ModelStorageData getNewStorageData() {
    return getFactorySupport().getStorageDataFactory().getNewlyIssuedStorageDataObject();
  }

  private void setParameterType(final ParameterType parameterType) {
    getDescription().setParameterType(parameterType.getValue());
  }

}
