package edu.umn.msi.tropix.proteomics.tagrecon.impl;

import java.util.Properties;

import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.PropertiesUtils;
import edu.umn.msi.tropix.common.io.PropertiesUtilsFactory;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.DirecTagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagReconParameters;
import edu.umn.msi.tropix.proteomics.test.IdentificationJobProcessorFactoryTest;

public class TagReconJobProcessorImplTest extends IdentificationJobProcessorFactoryTest<TagParameters, TagReconJobProcessorFactoryImpl> {
  private static final String DIREC_TAG_CONFIG_PATH = "directag.cfg";
  private static final String TAG_RECON_CONFIG_PATH = "tagrecon.cfg";
  private static final PropertiesUtils PROPERTIES_UTILS = PropertiesUtilsFactory.getInstance();
  private TagParameters parameters;

  @BeforeMethod(groups = "unit")
  public void init() {
    setFactoryAndInit(new TagReconJobProcessorFactoryImpl());
    parameters = new TagParameters();
    setParameters(parameters);
  }

  @Test(groups = "unit")
  public void testTagReconParameters() {
    final TagReconParameters tagReconParameters = new TagReconParameters();
    parameters.setTagReconParameters(tagReconParameters);
    tagReconParameters.setClassSizeMultiplier(4);
    tagReconParameters.setDeisotopingMode(2);

    expectStagingAndPreprocess();

    final Properties properties = PROPERTIES_UTILS.load(getRecordedOutputAsString(TAG_RECON_CONFIG_PATH));
    assert properties.getProperty("DeisotopingMode").equals("2");
    assert properties.getProperty("ClassSizeMultiplier").equals("4");
  }

  @Test(groups = "unit")
  public void testDirecTagParameters() {
    final DirecTagParameters dParameters = new DirecTagParameters();
    parameters.setDirecTagParameters(dParameters);
    dParameters.setUseChargeStateFromMS(true);
    dParameters.setTagLength(4);

    expectStagingAndPreprocess();

    final Properties properties = PROPERTIES_UTILS.load(getRecordedOutputAsString(DIREC_TAG_CONFIG_PATH));
    System.out.println(PROPERTIES_UTILS.toString(properties));
    assert properties.getProperty("UseChargeStateFromMS").equals("true");
    assert properties.getProperty("TagLength").equals("4");

  }

  @Test(groups = "unit")
  public void testDirectoryAndExecutable() {
    getFactory().setApplicationPath("/usr/bin/tag");
    expectStagingAndPreprocess();
    final JobDescriptionType jobDescription = getJobDescriptionType();
    assert jobDescription.getExecutable().equals("/usr/bin/tag");
    assert jobDescription.getDirectory().equals(getPath());
    final String[] arguments = jobDescription.getArgument();
    assert arguments.length == 3;
    assert arguments[0].equals("-ProteinDatabase");
    assert arguments[1].equals("db.fasta");
    assert arguments[2].equals("input-tags.tags");
  }

  @Test(groups = "unit")
  public void testPostprocessing() {
    expectStagingAndPreprocess();
    expectAddResource("input-tags.pepXML");
    replayPostprocessAndVerify();
  }

  private void expectStaging() {
    expectDownloadDatabaseAndInput();
    expectDownloadDatabaseTo("db.fasta");
    expectDownloadInputTo("input.mzXML");

    expectGetDirMockOutputContextAndRecord(DIREC_TAG_CONFIG_PATH);
    expectGetDirMockOutputContextAndRecord(TAG_RECON_CONFIG_PATH);
  }

  private void expectStagingAndPreprocess() {
    expectStaging();
    replayBuildJobProcessorPreprocessAndVerify();
  }

}
