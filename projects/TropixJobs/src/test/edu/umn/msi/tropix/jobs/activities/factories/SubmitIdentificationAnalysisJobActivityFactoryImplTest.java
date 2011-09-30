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

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
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
import edu.umn.msi.tropix.transfer.types.TransferResource;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

public class SubmitIdentificationAnalysisJobActivityFactoryImplTest extends BaseSubmitActivityFactoryImplTest {
  private SubmitIdentificationAnalysisJobActivityFactoryImpl factory;
  private ParametersService parametersService;
  private SubmitIdentificationAnalysisDescription description;
  private String parametersId, runFileId, databaseFileId;
  private TransferResource mzxmlResource, dbResource;
  private String serviceUrl;
  private IdentificationParametersDeserializer deserializer;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    parametersService = createMock(ParametersService.class);
    deserializer = createMock(IdentificationParametersDeserializer.class);
    factory = new SubmitIdentificationAnalysisJobActivityFactoryImpl(getFactorySupport(), getSubmitSupport(), parametersService, deserializer);

    description = TestUtils.init(new SubmitIdentificationAnalysisDescription());
    parametersId = UUID.randomUUID().toString();
    description.setParametersId(parametersId);

    final Database database = new Database();
    final TropixFile databaseFile = TestUtils.getNewTropixFile();
    databaseFileId = databaseFile.getId();
    database.setDatabaseFile(databaseFile);

    final ProteomicsRun run = new ProteomicsRun();
    final TropixFile runFile = TestUtils.getNewTropixFile();
    runFileId = runFile.getId();
    run.setMzxml(runFile);
    getFactorySupport().saveObjects(database, databaseFile, run, runFile);

    description.setDatabaseId(database.getId());
    description.setRunId(run.getId());

    mzxmlResource = getDownload(runFileId);
    dbResource = getDownload(databaseFileId);
  }

  private void run(final FileJobQueueContext queueContext) {
    preRun(queueContext);
    final Activity activity = factory.getActivity(description, getContext());
    activity.run();
    postRun(description);
  }

  @Test(groups = "unit")
  public void testSequest() {
    setServiceUrl("http://test/Sequest");
    setType(IdentificationType.SEQUEST);
    final SequestParameters parameters = new SequestParameters();
    parameters.setInternalCleavageSites(9);
    EasyMock.expect(parametersService.loadSequestParameters(parametersId)).andReturn(parameters);
    final SequestJobQueueContext sequestContext = expectCreateJob(serviceUrl, SequestJobQueueContext.class);
    final Capture<edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters> parametersCapture = EasyMockUtils.newCapture();
    sequestContext.submitJob(expectMzXML(), expectDatabase(), expectCredentialResource(), EasyMock.capture(parametersCapture));
    run(sequestContext);
    assert parametersCapture.getValue().getInternalCleavageSites().intValue() == 9;
  }

  @Test(groups = "unit")
  public void testXTandem() {
    setServiceUrl("http://test/XTandem");
    setType(IdentificationType.XTANDEM);
    final XTandemParameters parameters = new XTandemParameters();
    parameters.setRefine(true);
    EasyMock.expect(parametersService.loadXTandemParameters(parametersId)).andReturn(parameters);
    final XTandemJobQueueContext xTandemContext = expectCreateJob(serviceUrl, XTandemJobQueueContext.class);
    final Capture<edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters> parametersCapture = EasyMockUtils.newCapture();
    xTandemContext.submitJob(expectMzXML(), expectDatabase(), expectCredentialResource(), EasyMock.capture(parametersCapture));
    run(xTandemContext);
    assert parametersCapture.getValue().isRefine();
  }

  @Test(groups = "unit")
  public void testMyriMatch() {
    setServiceUrl("http://test/MyriMatch");
    setType(IdentificationType.MYRIMATCH);
    final MyriMatchParameters parameters = new MyriMatchParameters();

    expectLoadParametersFileWithObject(parameters);

    final MyriMatchJobQueueContext myriMatchContext = expectCreateJob(serviceUrl, MyriMatchJobQueueContext.class);
    myriMatchContext.submitJob(expectMzXML(), expectDatabase(), expectCredentialResource(), EasyMock.same(parameters));
    run(myriMatchContext);
  }
  
  @Test(groups = "unit")
  public void testTagRecon() {
    setServiceUrl("http://TagRecon");
    setType(IdentificationType.TAGRECON);
    final TagParameters parameters = new TagParameters();
    expectLoadParametersFileWithObject(parameters);
    final TagReconJobQueueContext tagReconContext = expectCreateJob(serviceUrl, TagReconJobQueueContext.class);
    tagReconContext.submitJob(expectMzXML(), expectDatabase(), expectCredentialResource(), EasyMock.same(parameters));
    run(tagReconContext);
  }

  private void setType(final IdentificationType identificationType) {
    description.setParameterType(identificationType.getParameterType());
  }

  @Test(groups = "unit")
  public void testOmssa() {
    setServiceUrl("http://test/Omssa");
    setType(IdentificationType.OMSSA);
    final MSSearchSettings settings = new MSSearchSettings();
    settings.setMSSearchSettings_automassadjust(3.0);

    expectLoadParametersFileWithObject(settings);

    final OmssaJobQueueContext omssaContext = expectCreateJob(serviceUrl, OmssaJobQueueContext.class);
    omssaContext.submitJob(expectMzXML(), expectDatabase(), expectCredentialResource(), EasyMock.same(settings));
    run(omssaContext);
  }

  private void setServiceUrl(final String serviceUrl) {
    this.serviceUrl = serviceUrl;
    description.setServiceUrl(serviceUrl);
  }

  private TransferResource expectDatabase() {
    return EasyMock.eq(dbResource);
  }

  private TransferResource expectMzXML() {
    return EasyMock.eq(mzxmlResource);
  }

  private void expectLoadParametersFileWithObject(final Object result) {
    final TropixFile parametersFile = TestUtils.getNewTropixFile();
    parametersFile.setId(parametersId);
    getFactorySupport().saveObject(parametersFile);
    deserializer.loadParameters(EasyMock.eq(description.getType()), EasyMock.isA(InputContext.class), EasyMock.eq(result.getClass()));
    EasyMock.expectLastCall().andReturn(result);
  }

}
