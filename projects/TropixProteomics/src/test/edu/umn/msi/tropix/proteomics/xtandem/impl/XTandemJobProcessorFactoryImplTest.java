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

package edu.umn.msi.tropix.proteomics.xtandem.impl;

import static org.easymock.EasyMock.createMock;

import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.io.ByteArrayOutputContextImpl;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xtandem.XTandemParameterTranslator;

public class XTandemJobProcessorFactoryImplTest {
  private static final String PATH = "C:\\moo\\";
  private static final String SEP = "\\";
  private XTandemJobProcessorFactoryImpl factory;
  private CredentialedStagingDirectoryFactory stagingDirectoryFactory;
  private final Credential credential = Credentials.getMock();
  private StagingDirectory stagingDirectory;
  private DisposableResourceTracker tracker;
  private InputContext databasePopulator, mzxmlPopulator;
  private XTandemParameterTranslator mockTranslator = EasyMock.createMock(XTandemParameterTranslator.class);

  private MockObjectCollection mockObjects = new MockObjectCollection();

  @BeforeMethod(groups = "unit")
  public void init() {
    factory = new XTandemJobProcessorFactoryImpl();
    stagingDirectoryFactory = EasyMock.createMock(CredentialedStagingDirectoryFactory.class);
    stagingDirectory = EasyMock.createMock(StagingDirectory.class);

    EasyMock.expect(stagingDirectory.getAbsolutePath()).andStubReturn(PATH);
    EasyMock.expect(stagingDirectory.getSep()).andStubReturn(SEP);

    tracker = createMock(DisposableResourceTracker.class);
    databasePopulator = createMock(InputContext.class);
    mzxmlPopulator = createMock(InputContext.class);
    mockTranslator = createMock(XTandemParameterTranslator.class);

    factory.setDisposableResourceTrackerSupplier(Suppliers.ofInstance(tracker));
    factory.setParameterTranslator(mockTranslator);
    factory.setCredentialedStagingDirectoryFactory(stagingDirectoryFactory);

    mockObjects = MockObjectCollection.fromObjects(stagingDirectoryFactory, stagingDirectory, tracker, mockTranslator, databasePopulator, mzxmlPopulator);
  }

  @AfterMethod(groups = "unit")
  public void cleanUp() {
  }

  protected XTandemParameters getXTandemParameters() throws IOException {
    final InputStream xTandemPropertiesInputStream = ProteomicsTests.getResourceAsStream("xTandemSimple.properties");
    final XTandemParameters xTandemParameters = new XTandemParameters();
    ParameterUtils.setParametersFromProperties(xTandemPropertiesInputStream, xTandemParameters);
    return xTandemParameters;
  }

  @Test(groups = "unit")
  public void setup() throws IOException {
    test(false);
  }

  public void test(final boolean initialized) throws IOException {
    XTandemJobProcessorImpl jobProcessor;

    final ByteArrayOutputContextImpl dbContext = new ByteArrayOutputContextImpl();
    final ByteArrayOutputContextImpl mzxmlContext = new ByteArrayOutputContextImpl();
    final ByteArrayOutputContextImpl taxonContext = new ByteArrayOutputContextImpl();
    final ByteArrayOutputContextImpl inputContext = new ByteArrayOutputContextImpl();

    EasyMock.expect(stagingDirectory.getOutputContext("db.fasta")).andReturn(dbContext);
    EasyMock.expect(stagingDirectory.getOutputContext("data.mzxml")).andReturn(mzxmlContext);
    databasePopulator.get(dbContext);
    mzxmlPopulator.get(mzxmlContext);

    EasyMock.expect(stagingDirectory.getOutputContext("taxonomy.xml")).andReturn(taxonContext);
    EasyMock.expect(stagingDirectory.getOutputContext("input.xml")).andReturn(inputContext);

    EasyMock.expect(mockTranslator.getXTandemParameters(EasyMock.isA(XTandemParameters.class), EasyMock.and(EasyMock.startsWith(PATH), EasyMock.endsWith(".xml")), EasyMock.startsWith(PATH), EasyMock.isA(String.class), EasyMock.startsWith(PATH), (String) EasyMock.isNull()))
        .andReturn("Hello World!");

    EasyMock.expect(stagingDirectoryFactory.get(credential)).andReturn(stagingDirectory);
    stagingDirectory.setup();
    mockObjects.replay();
    final JobProcessorConfiguration configuration = JobProcessorConfigurationFactories.getInstance().get(credential);
    jobProcessor = factory.create(configuration);
    jobProcessor.setDatabase(databasePopulator);
    jobProcessor.setInputParameters(getXTandemParameters());
    jobProcessor.setInputMzXML(mzxmlPopulator);
    jobProcessor.preprocess();
    mockObjects.verifyAndReset();
    EasyMock.expect(stagingDirectory.getAbsolutePath()).andStubReturn(PATH);
    EasyMock.expect(stagingDirectory.getSep()).andStubReturn(SEP);
    stagingDirectory.cleanUp();
    final InputContext outputXmlContext = EasyMock.createMock(InputContext.class);
    EasyMock.expect(stagingDirectory.getInputContext("output.xml")).andReturn(outputXmlContext);
    tracker.add(outputXmlContext);
    mockObjects.replay();
    jobProcessor.postprocess(true);
    mockObjects.verifyAndReset();

    new String(inputContext.toByteArray()).equals("Hello World!");

  }

}
