package edu.umn.msi.tropix.proteomics.rawextract.impl;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;

public class MzxmlRawExtractJobFactoryImplTest extends BaseRawExtractJobFactoryImplTest {
  private String mzxmlExt = ".mzxml";

  @Test(groups = "unit")
  public void mzxml() throws Exception {
    setDoPreprocessing(true);
    setCompletedNormally(true);
    this.mzxmlExt = ".mzxml";
    runTest();
  }

  @Test(groups = "unit")
  public void mzXML() throws Exception {
    setDoPreprocessing(true);
    setCompletedNormally(true);
    this.mzxmlExt = ".mzXML";
    runTest();
  }

  @Test(groups = "unit")
  public void extMZXML() throws Exception {
    setDoPreprocessing(true);
    setCompletedNormally(true);
    this.mzxmlExt = ".MzXML";
    runTest();
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testInvalidMzxml() {
    setDoPreprocessing(true);
    setCompletedNormally(true);
    setupMzXMLResult("<mzXML>");
    getFactory().setProducesMzxml(true);
    expectPreprocessingAndReplayMocks();
    buildJobAndPreprocess();
    postProcessAndVerify();
  }

  private InputContext setupMzXMLResult(final String contents) {
    final String mzxmlName = BASE + mzxmlExt;
    final InputContext mzxmlContext = InputContexts.forString(contents);
    EasyMock.expect(getStagingDirectory().getResourceNames(null)).andReturn(Arrays.asList(mzxmlName));
    EasyMock.expect(getStagingDirectory().getInputContext(mzxmlName)).andReturn(mzxmlContext).atLeastOnce();
    return mzxmlContext;
  }

  public void runTest() throws Exception {
    getFactory().setProducesMzxml(true);
    final InputContext mzxmlContext = setupMzXMLResult("<mzXML></mzXML>");
    getTracker().add(mzxmlContext);

    expectPreprocessingAndReplayMocks();

    if(getDoPreprocessing()) {
      final ExecutableJobDescription outputDescription = buildJobAndPreprocess();
      assert JobDescriptionUtils.getExtensionParameter(outputDescription.getJobDescriptionType(), "rawextract_basename").equals(BASE);
      assert outputDescription.getJobDescriptionType().getArgument(0).equals(PARAMS);
    } else {
      final JobDescriptionType jobDescription = new JobDescriptionType();
      JobDescriptionUtils.setProxy(jobDescription, PROXY);
      JobDescriptionUtils.setExtensionParameter(jobDescription, "rawextract_basename", BASE);
      JobDescriptionUtils.setStagingDirectory(jobDescription, PATH);
      setJob(getFactory().recover(ExecutableJobDescriptions.forJobDescriptionType(jobDescription)));
    }
    postProcessAndVerify();
  }

}
