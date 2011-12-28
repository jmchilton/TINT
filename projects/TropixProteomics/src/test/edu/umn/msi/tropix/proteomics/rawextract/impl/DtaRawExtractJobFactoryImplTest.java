package edu.umn.msi.tropix.proteomics.rawextract.impl;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.DTAList.Entry;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;
import edu.umn.msi.tropix.proteomics.test.TestData;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

public class DtaRawExtractJobFactoryImplTest extends BaseRawExtractJobFactoryImplTest {
  public DTAList getDTAList(final String[] names) {
    final IArgumentMatcher argMatcher = new IArgumentMatcher() {

      public void appendTo(final StringBuffer buffer) {
        buffer.append("invalid dtaList received");
      }

      public boolean matches(final Object arg) {
        final DTAList dtaList = (DTAList) arg;
        final HashSet<String> expectedFiles = new HashSet<String>();
        expectedFiles.addAll(Arrays.asList(names));
        final HashSet<String> files = new HashSet<String>();
        for(final Entry entry : dtaList) {
          files.add(entry.getName());
        }
        return files.equals(expectedFiles);
      }
    };
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public DTAToMzXMLOptions getOptions(final Object options) {
    final IArgumentMatcher argMatcher = new IArgumentMatcher() {

      public void appendTo(final StringBuffer buffer) {
        buffer.append("invalid dtaList received");
      }

      public boolean matches(final Object arg) {
        return arg.equals(options);
      }
    };
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  @Test(groups = "unit")
  public void processTest() throws Exception {
    setDoPreprocessing(true);
    setCompletedNormally(true);
    runTest();
  }

  @Test(groups = "unit")
  public void processFailed() throws Exception {
    setDoPreprocessing(true);
    setCompletedNormally(false);
    runTest();
  }

  @Test(groups = "unit")
  public void processResume() throws Exception {
    setDoPreprocessing(false);
    setCompletedNormally(true);
    runTest();
  }

  @Test(groups = "unit")
  public void processResumeFailed() throws Exception {
    setDoPreprocessing(false);
    setCompletedNormally(false);
    runTest();
  }

  public void runTest() throws Exception {

    final MzXML mzxml = TestData.getMzXML("validMzXML.mzxml");

    final String[] dtaFileExts = new String[] {".100.100.1.dta", ".101.101.2.dta", ".101.101.3.dta"};
    final String[] dtaFileNames = new String[dtaFileExts.length];
    int i = 0;
    for(final String dtaFileExt : dtaFileExts) {
      dtaFileNames[i++] = BASE + dtaFileExt;
    }

    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    if(isCompletedNormally()) {
      EasyMock.expect(getStagingDirectory().getResourceNames(null)).andReturn(Arrays.asList(dtaFileNames));
      for(final String dtaFileName : dtaFileNames) {
        EasyMock.expect(getStagingDirectory().getInputContext(dtaFileName)).andReturn(InputContexts.forString("moo" + dtaFileName));
      }
      EasyMock.expect(getConverter().dtaToMzXML(getDTAList(dtaFileNames), getOptions(getOptions()))).andReturn(mzxml);
      EasyMock.expect(getTracker().newStream()).andReturn(stream);
    }

    expectPreprocessingAndReplayMocks();

    if(getDoPreprocessing()) {
      final ExecutableJobDescription outputDescription = buildJobAndPreprocess();
      assert JobDescriptionUtils.getExtensionParameter(outputDescription.getJobDescriptionType(), "rawextract_basename").equals(BASE);
      assert outputDescription.getJobDescriptionType().getArgument(0).equals(PARAMS);
    } else {
      final JobDescriptionType jobDescription = new JobDescriptionType();
      JobDescriptionUtils.setExtensionParameter(jobDescription, "rawextract_basename", BASE);
      JobDescriptionUtils.setStagingDirectory(jobDescription, PATH);
      JobDescriptionUtils.setProxy(jobDescription, PROXY);
      setJob(getFactory().recover(ExecutableJobDescriptions.forJobDescriptionType(jobDescription)));
    }
    postProcessAndVerify();

    if(isCompletedNormally()) {
      final MzXMLUtility util = new MzXMLUtility();
      assert util.serialize(mzxml).equals(new String(stream.toByteArray()));
    }
  }

}
