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

package edu.umn.msi.tropix.proteomics.scaffold.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.proteomics.scaffold.input.BiologicalSample;
import edu.umn.msi.tropix.proteomics.scaffold.input.Experiment;
import edu.umn.msi.tropix.proteomics.scaffold.input.Export;
import edu.umn.msi.tropix.proteomics.scaffold.input.ExportType;
import edu.umn.msi.tropix.proteomics.scaffold.input.FastaDatabase;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xml.ScaffoldUtility;

public class ScaffoldJobProcessorImplTest extends JobProcessorFactoryTest<ScaffoldJobProcessorFactoryImpl> {
  private static final ScaffoldUtility XML_UTILITY = new ScaffoldUtility();
  private ScaffoldJobProcessorImpl processor;
  private List<InputContext> inputContexts;
  private Scaffold scaffold;
  private Experiment experiment;
  private ExecutableJobDescription executableJobDescription;
  private JobDescriptionType jobDescription;

  @BeforeMethod(groups = "unit")
  public void init() {
    setFactory(new ScaffoldJobProcessorFactoryImpl());
    super.init();
    scaffold = new Scaffold();
    experiment = new Experiment();
    scaffold.setExperiment(experiment);
  }

  @AfterMethod(groups = "unit")
  public void cleanUp() {
    inputContexts = null;
    scaffold = null;
  }

  @Test(groups = "unit")
  public void optionsOn() {
    options(true, true);
  }

  @Test(groups = "unit")
  public void optionsOff() {
    options(false, false);
  }

  private void executePreprocess() {
    getStagingDirectory().setup();
    replay();
    processor = getFactory().createScaffoldJob(getJobProcessorConfiguration(), scaffold, inputContexts);
    executableJobDescription = processor.preprocess();
    jobDescription = executableJobDescription.getJobDescriptionType();
    verifyAndReset();
  }

  @Test(groups = "unit")
  public void initialize() throws IOException {
    expectRecoverStagingDirectory();

    final Export export = new Export();
    export.setPath(getPath() + "/output/test.sfd");
    experiment.getExport().add(export);
    final String scaffoldXml = XML_UTILITY.serialize(scaffold);
    EasyMock.expect(getStagingDirectory().getInputContext("input.xml")).andReturn(InputContexts.forString(scaffoldXml));

    final JobDescriptionType jobDescription = new JobDescriptionType();
    JobDescriptionUtils.setProxy(jobDescription, getCredential());
    JobDescriptionUtils.setStagingDirectory(jobDescription, getPath());
    JobDescriptionUtils.setJobType(jobDescription, "scaffold");

    final InputContext sfdContext = EasyMock.createMock(InputContext.class);
    EasyMock.expect(getStagingDirectory().getInputContext("output/test.sfd")).andReturn(sfdContext);
    getStagingDirectory().cleanUp();
    getTracker().add(sfdContext);
    replay();
    processor = getFactory().recover(ExecutableJobDescriptions.forJobDescriptionType(jobDescription));
    processor.postprocess(true);
    verifyAndReset();

  }

  @Test(groups = "unit")
  public void testOmssaUnzipped() {
    final InputContext zippedInputContext = InputContexts.forInputStream(ProteomicsTests.getResourceAsStream("hello.zip"));
    expectGetDirMockOutputContextAndRecord("input/input.omx");

    inputContexts = Lists.newArrayList(zippedInputContext);
    addSample("moo", "input.omx");

    expectSetupDirectories();
    expectGetDirMockOutputContextAndRecord("input.xml");
    executePreprocess();

    assert getRecordedOutputAsString("input/input.omx").equals("Hello World!"); // the contents of hello.zip's only file

  }

  @Test(groups = "unit")
  public void testFileStaging() throws Exception {
    final String[] paths = new String[] {"db/db1.fasta", "db/db2.fasta", "input/input1.out", "input/input2.out", "input/o1.xml", "input/o2.xml"};

    inputContexts = super.getDownloadContexts(6);
    for(int i = 0; i < 6; i++) {
      inputContexts.get(i).get(getDirMockOutputContext(paths[i]));
    }

    initScaffoldSample1();
    expectSetupDirectories();
    expectGetDirMockOutputContextAndRecord("input.xml");

    executePreprocess();
    final Scaffold processedInput = XML_UTILITY.deserialize(getRecordedOutputContents("input.xml"));
    final List<Export> outputExport = processedInput.getExperiment().getExport();
    assert outputExport.size() == 1 : "Output files added or lost in preprocess.";
    assert outputExport.get(0).getPath().equals(getPath() + "/output/simple_test.sfd") : "Output PATH not adjusted correctly.";
    final List<BiologicalSample> samples = processedInput.getExperiment().getBiologicalSample();
    assert samples.size() == 1 : "Wrong number of samples";
    final List<String> inputFilePaths = samples.get(0).getInputFile();
    assert inputFilePaths.size() == 4;

    final InputContext inputContext = InputContexts.forString("hello world");
    EasyMock.expect(getStagingDirectory().getInputContext("output/simple_test.sfd")).andReturn(inputContext);
    getTracker().add(inputContext);
    getStagingDirectory().cleanUp();
    replay();
    processor.postprocess(true);
    verifyAndReset();
  }

  private void expectSetupDirectories() {
    getStagingDirectory().makeDirectory("input");
    getStagingDirectory().makeDirectory("output");
    getStagingDirectory().makeDirectory("db");
  }

  private void options(final boolean isQuiet, final boolean setKey) {
    inputContexts = new LinkedList<InputContext>();
    getFactory().setQuietMode(isQuiet);
    if(setKey) {
      getFactory().setKeyPath(new File("thekeypath").getAbsolutePath());
    }
    expectSetupDirectories();
    expectGetDirMockOutputContextAndRecord("input.xml");
    executePreprocess();
    boolean quiet = false;
    String keyPath = null;
    boolean nextIsKeyPath = false;
    for(final String argument : jobDescription.getArgument()) {
      if(argument.equals("-q")) {
        quiet = true;
      } else if(nextIsKeyPath) {
        keyPath = argument;
        nextIsKeyPath = false;
      } else if(argument.equals("-keypath")) {
        nextIsKeyPath = true;
      }
    }
    assert quiet == isQuiet;
    if(setKey) {
      assert new File("thekeypath").getAbsolutePath().equals(keyPath);
    }
  }

  private FastaDatabase addDatabase(final String id, final String path) {
    final FastaDatabase db1 = new FastaDatabase();
    db1.setId(id);
    db1.setPath(path);
    experiment.getFastaDatabase().add(db1);
    return db1;
  }

  private BiologicalSample addSample(final String databaseName, final String... inputs) {
    final BiologicalSample sample = new BiologicalSample();
    sample.setDatabase(databaseName);
    for(final String input : inputs) {
      sample.getInputFile().add(input);
    }
    experiment.getBiologicalSample().add(sample);
    return sample;
  }

  private void initScaffoldSample1() {
    addDatabase("index1", "db1.fasta");
    addDatabase("index2", "db2.fasta");
    experiment.setName("simple_test");
    addSample("human", "input1.out", "input2.out", "o1.xml", "o2.xml");
    final Export export = new Export();
    export.setType(ExportType.SFD);
    export.setThresholds("thresh");
    export.setPath("simple_test.sfd");
    experiment.getExport().add(export);
  }

}
