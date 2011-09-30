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

package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Properties;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.PropertiesUtils;
import edu.umn.msi.tropix.common.io.PropertiesUtilsFactory;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.Sample;

public class IdPickerJobProcessorFactoryImplTest extends JobProcessorFactoryTest<IdPickerJobProcessorFactoryImpl> {
  private static final PropertiesUtils PROPERTIES_UTILS = PropertiesUtilsFactory.getInstance();
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private InputContext databaseContext;
  private List<InputContext> pepXmlContexts;
  private IdPickerParameters parameters;
  private JobDescriptionType jobDescription;
  private FileMaskCreator fileMaskCreator;
  private List<Sample> samples = Lists.newArrayList();

  @BeforeMethod(groups = "unit")
  public void init() {
    setFactory(new IdPickerJobProcessorFactoryImpl());
    super.init();
    parameters = new IdPickerParameters();
    samples.clear();
  }

  @Test(groups = "unit")
  public void testNamesOneSample() {
    addSample("test_samp", "moo", "cow");
    pepXmlContexts = getDownloadContexts(2);
    pepXmlContexts.get(0).get(getDirMockOutputContext("test_samp/moo.pepXML"));
    pepXmlContexts.get(1).get(getDirMockOutputContext("test_samp/cow.pepXML"));

    expectMakeDirectory("test_samp");

    expectWriteConfigurationFiles();

    preprocess();

    getRecordedOutputAsString("idpQonvert-files").equals(fileMaskCreator.getQonvertLines());
    getRecordedOutputAsString("idpAssemble-files").equals(fileMaskCreator.getAssembleLines());
  }

  @Test(groups = "unit")
  public void testNamesEscaped() {
    addSample("test_samp", "../moo");
    pepXmlContexts = getDownloadContexts(1);
    pepXmlContexts.get(0).get(getDirMockOutputContext("test_samp/moo.pepXML"));

    expectMakeDirectory("test_samp");

    expectWriteConfigurationFiles();

    preprocess();

    getRecordedOutputAsString("idpQonvert-files").equals(fileMaskCreator.getQonvertLines());
    getRecordedOutputAsString("idpAssemble-files").equals(fileMaskCreator.getAssembleLines());
  }

  @Test(groups = "unit")
  public void testTwoSamples() {
    addSample("test_samp", "moo", "cow");
    addSample("test_samp2", "foo");

    pepXmlContexts = getDownloadContexts(3);
    pepXmlContexts.get(0).get(getDirMockOutputContext("test_samp/moo.pepXML"));
    pepXmlContexts.get(1).get(getDirMockOutputContext("test_samp/cow.pepXML"));
    pepXmlContexts.get(2).get(getDirMockOutputContext("test_samp2/foo.pepXML"));

    expectMakeDirectory("test_samp");
    expectMakeDirectory("test_samp2");

    expectWriteConfigurationFiles();

    preprocess();

    getRecordedOutputAsString("idpQonvert-files").equals(fileMaskCreator.getQonvertLines());
    getRecordedOutputAsString("idpAssemble-files").equals(fileMaskCreator.getAssembleLines());
  }

  @Test(groups = "unit")
  public void testQuonvertDatabaseCfg() {
    preprocessOneInput();

    final Properties qonvertCfg = loadWrittenProperties("idpQonvert.cfg");
    assert qonvertCfg.getProperty("ProteinDatabase").equals("db.fasta");
  }

  @Test(groups = "unit")
  public void testQonvertPresevesHierarchyCfg() {
    preprocessOneInput();

    final Properties qonvertCfg = loadWrittenProperties("idpQonvert.cfg");
    assert qonvertCfg.getProperty("PreserveInputHierarchy").equals("true");
  }

  @Test(groups = "unit")
  public void testQonvertDefaultRevCfg() {
    parameters.setDecoyPrefix(null);
    preprocessOneInput();

    final Properties qonvertCfg = loadWrittenProperties("idpQonvert.cfg");
    assert qonvertCfg.getProperty("DecoyPrefix") == null;
  }

  @Test(groups = "unit")
  public void testQonvertCfg() {
    parameters.setHasDecoyDatabase(true);
    parameters.setNormalizeSearchScores(true);
    parameters.setOptimizeScoreWeights(true);
    parameters.setDecoyPrefix("moo");
    parameters.setMaxResultRank(3);
    parameters.setMaxFDRQonvert(0.78);
    parameters.setOptimizeScorePermutations(450);
    preprocessOneInput();

    final Properties qonvertCfg = loadWrittenProperties("idpQonvert.cfg");
    assert Boolean.parseBoolean(qonvertCfg.getProperty("HasDecoyDatabase")) == parameters.getHasDecoyDatabase();
    assert qonvertCfg.getProperty("DecoyPrefix").equals(parameters.getDecoyPrefix());
    assert Boolean.parseBoolean(qonvertCfg.getProperty("NormalizeSearchScores")) == parameters.getNormalizeSearchScores();
    assert Boolean.parseBoolean(qonvertCfg.getProperty("OptimizeScoreWeights")) == parameters.getOptimizeScoreWeights();
    assert Double.parseDouble(qonvertCfg.getProperty("MaxFDR")) == parameters.getMaxFDRQonvert();
    assert Integer.parseInt(qonvertCfg.getProperty("MaxResultRank")) == parameters.getMaxResultRank();
    assert Integer.parseInt(qonvertCfg.getProperty("OptimizeScorePermutations")) == parameters.getOptimizeScorePermutations();
  }

  private void preprocessOneInput() {
    expectOneInput();
    expectWriteConfigurationFiles();
    preprocess();
  }

  private Properties loadWrittenProperties(final String resourceName) {
    return PROPERTIES_UTILS.load(getRecordedOutputAsString(resourceName));
  }

  @Test(groups = "unit")
  public void testAssembleCfg() {
    parameters.setMaxFDRAssemble(0.3);
    parameters.setMaxResultRank(345);
    preprocessOneInput();

    final Properties reportCfg = loadWrittenProperties("idpAssemble.cfg");
    assert Double.parseDouble(reportCfg.getProperty("MaxFDR")) == parameters.getMaxFDRAssemble();
    assert Integer.parseInt(reportCfg.getProperty("MaxResultRank")) == parameters.getMaxResultRank();
  }

  @Test(groups = "unit")
  public void testReportCfg() {
    parameters.setMinDistinctPeptides(23);
    parameters.setMaxAmbiguousIds(2);
    parameters.setMaxFDRReport(0.4);
    parameters.setMinAdditionalPeptides(24);
    parameters.setMaxResultRank(234);
    preprocessOneInput();

    final Properties reportCfg = loadWrittenProperties("idpReport.cfg");
    assert Integer.parseInt(reportCfg.getProperty("MinDistinctPeptides")) == parameters.getMinDistinctPeptides();
    assert Integer.parseInt(reportCfg.getProperty("MaxAmbiguousIds")) == parameters.getMaxAmbiguousIds();
    assert Double.parseDouble(reportCfg.getProperty("MaxFDR")) == parameters.getMaxFDRReport();
    assert Integer.parseInt(reportCfg.getProperty("MinAdditionalPeptides")) == parameters.getMinAdditionalPeptides();
    assert Integer.parseInt(reportCfg.getProperty("MaxResultRank")) == parameters.getMaxResultRank();
  }

  private void expectWriteConfigurationFiles() {
    expectDatabase();
    expectWriteQuonvertFileMask();
    expectWriteAssembleFileMask();

    expectWriteQuonvertConfig();
    expectWriteAssembleConfig();
    expectWriteReportConfig();
  }

  private void expectWriteQuonvertConfig() {
    expectGetDirMockOutputContextAndRecord("idpQonvert.cfg");
  }

  private void expectWriteAssembleConfig() {
    expectGetDirMockOutputContextAndRecord("idpAssemble.cfg");
  }

  private void expectWriteReportConfig() {
    expectGetDirMockOutputContextAndRecord("idpReport.cfg");
  }

  private void expectOneInput() {
    addSample("test", "input");
    pepXmlContexts = getDownloadContexts(1);
    pepXmlContexts.get(0).get(getDirMockOutputContext("test/input.pepXML"));
    expectMakeDirectory("test");
  }

  private void addSample(final String name, final String... inputs) {
    samples.add(new Sample(inputs, name));
  }

  @Test(groups = "unit")
  public void create() {
    getFactory().setApplicationPath("/usr/bin/idpicker");

    expectOneInput();
    expectWriteConfigurationFiles();

    final FileJobProcessor<ExecutableJobDescription> processor = preprocess();

    assert jobDescription.getExecutable().equals("/usr/bin/idpicker");
    expectZipHtmlOutput();
    getStagingDirectory().cleanUp();
    expectAddResource("combined-sources.xml");
    final ByteArrayOutputStream zippedHtmlStream = expectNewTrackerStream();
    replay();
    processor.postprocess(true);
    verifyAndReset();
    verifyZipHtmlOutput(zippedHtmlStream.toByteArray());
  }

  private void verifyZipHtmlOutput(final byte[] zippedBytes) {
    final File tempDirectory = ZIP_UTILS.unzipToTempDirectory(new ByteArrayInputStream(zippedBytes));
    try {
      assert FILE_UTILS.readFileToString(new File(tempDirectory, "index.html")).equals("index contents");
      assert FILE_UTILS.readFileToString(new File(tempDirectory, "idpicker-scripts.js")).equals("<script>JavaScript</script>");
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(tempDirectory);
    }
  }

  private void expectZipHtmlOutput() {
    EasyMock.expect(getStagingDirectory().getResourceNames("combined-sources")).andReturn(Lists.newArrayList("index.html", "idpicker-scripts.js"));
    getDirMockInputContextAndReturn("combined-sources/index.html", InputContexts.forString("index contents"));
    getDirMockInputContextAndReturn("combined-sources/idpicker-scripts.js", InputContexts.forString("<script>JavaScript</script>"));
  }

  private FileJobProcessor<ExecutableJobDescription> preprocess() {
    parameters.setSample(Iterables.toArray(samples, Sample.class));
    fileMaskCreator = new FileMaskCreator(parameters);
    getStagingDirectory().setup();
    replay();
    final FileJobProcessor<ExecutableJobDescription> processor = getFactory().create(getJobProcessorConfiguration(), parameters, databaseContext,
        pepXmlContexts);
    final ExecutableJobDescription description = processor.preprocess();
    verifyAndReset();
    jobDescription = description.getJobDescriptionType();
    return processor;
  }

  private void expectWriteQuonvertFileMask() {
    expectGetDirMockOutputContextAndRecord("idpQonvert-files");
  }

  private void expectWriteAssembleFileMask() {
    expectGetDirMockOutputContextAndRecord("idpAssemble-files");
  }

  private void expectDatabase() {
    databaseContext = getDownloadContext();
    databaseContext.get(getDirMockOutputContext("db.fasta"));
  }

}
