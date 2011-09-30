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

package edu.umn.msi.tropix.proteomics.omssa.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

public class OmssaJobProcessorFactoryImplTest extends JobProcessorFactoryTest<OmssaJobProcessorFactoryImpl> {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testCreate(final boolean zipOutput) {
    setFactory(new OmssaJobProcessorFactoryImpl());
    super.init();

    getFactory().setZipOutput(zipOutput);

    final MzXMLToMGFConverter converter = super.getMockObjects().createMock(MzXMLToMGFConverter.class);
    getFactory().setMxXMLToMGFConverter(converter);

    getStagingDirectory().setup();

    final InputContext databaseContext = getDownloadContext();
    final InputContext mzxmlContext = getDownloadContext();

    databaseContext.get(getDirMockOutputContext("db.fasta"));
    mzxmlContext.get(EasyMockUtils.writeContentsToFile("mzxml contents".getBytes()));

    // Refactory this call...

    expectGetDirMockOutputContextAndRecord("data.mgf");
    expectGetDirMockOutputContextAndRecord("input.xml");

    final MSSearchSettings parameters = new MSSearchSettings();
    parameters.setMSSearchSettings_automassadjust(123.45);
    converter.mzxmlToMGF(EasyMockUtils.inputStreamWithContents("mzxml contents".getBytes()),
        EasyMockUtils.copy(new ByteArrayInputStream("mgf contents".getBytes())), EasyMock.<MgfConversionOptions>isNull());

    getMockObjects().replay();

    final OmssaJobProcessorImpl jobProcessor = super.getFactory().create(getJobProcessorConfiguration());
    jobProcessor.setDatabase(databaseContext);
    jobProcessor.setInputMzXML(mzxmlContext);
    jobProcessor.setInputParameters(parameters);

    jobProcessor.preprocess();

    getMockObjects().verifyAndReset();
    assert getRecordedOutputAsString("data.mgf").equals("mgf contents");
    final MSSearchSettings savedSettings = SERIALIZATION_UTILS.deserialize(new StringReader(super.getRecordedOutputAsString("input.xml")),
        MSSearchSettings.class);
    assert savedSettings.getMSSearchSettings_automassadjust().equals(123.45);

    // Test Postprocessing
    getStagingDirectory().cleanUp();

    final InputContext outputOmxContext = InputContexts.forString("output contents");
    getDirMockInputContextAndReturn("output.xml", outputOmxContext);

    ByteArrayOutputStream zippedOutputStream = null;
    if(zipOutput) {
      zippedOutputStream = expectNewTrackerStream();
    } else {
      super.getTracker().add(EasyMock.same(outputOmxContext));
    }

    getMockObjects().replay();

    jobProcessor.postprocess(true);

    getMockObjects().verifyAndReset();
    if(zipOutput) {
      verifyZipStream(zippedOutputStream.toByteArray(), "output contents");
    }
  }

  private static void verifyZipStream(final byte[] byteArray, final String expectedContents) {
    final File tempDir = ZIP_UTILS.unzipToTempDirectory(new ByteArrayInputStream(byteArray));
    try {
      assert FILE_UTILS.readFileToString(new File(tempDir, "output.omx")).equals(expectedContents);
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(tempDir);
    }
  }

}
