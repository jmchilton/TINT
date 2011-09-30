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
    this.doPreprocessing = true;
    this.completedNormally = true;
    this.mzxmlExt = ".mzxml";
    runTest();
  }

  @Test(groups = "unit")
  public void mzXML() throws Exception {
    this.doPreprocessing = true;
    this.completedNormally = true;
    this.mzxmlExt = ".mzXML";
    runTest();
  }

  @Test(groups = "unit")
  public void extMZXML() throws Exception {
    this.doPreprocessing = true;
    this.completedNormally = true;
    this.mzxmlExt = ".MzXML";
    runTest();
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testInvalidMzxml() {
    this.doPreprocessing = true;
    this.completedNormally = true;
    setupMzXMLResult("<mzXML>");
    factory.setProducesMzxml(true);
    expectPreprocessingAndReplayMocks();
    buildJobAndPreprocess();
    postProcessAndVerify();
  }

  private InputContext setupMzXMLResult(final String contents) {
    final String mzxmlName = base + mzxmlExt;
    final InputContext mzxmlContext = InputContexts.forString(contents);
    EasyMock.expect(stagingDirectory.getResourceNames(null)).andReturn(Arrays.asList(mzxmlName));
    EasyMock.expect(stagingDirectory.getInputContext(mzxmlName)).andReturn(mzxmlContext).atLeastOnce();
    return mzxmlContext;
  }

  public void runTest() throws Exception {
    factory.setProducesMzxml(true);
    final InputContext mzxmlContext = setupMzXMLResult("<mzXML></mzXML>");
    tracker.add(mzxmlContext);

    expectPreprocessingAndReplayMocks();

    if(doPreprocessing) {
      final ExecutableJobDescription outputDescription = buildJobAndPreprocess();
      assert JobDescriptionUtils.getExtensionParameter(outputDescription.getJobDescriptionType(), "rawextract_basename").equals(base);
      assert outputDescription.getJobDescriptionType().getArgument(0).equals(params);
    } else {
      final JobDescriptionType jobDescription = new JobDescriptionType();
      JobDescriptionUtils.setProxy(jobDescription, proxy);
      JobDescriptionUtils.setExtensionParameter(jobDescription, "rawextract_basename", base);
      JobDescriptionUtils.setStagingDirectory(jobDescription, path);
      job = factory.recover(ExecutableJobDescriptions.forJobDescriptionType(jobDescription));
    }
    postProcessAndVerify();
  }

}
