package edu.umn.msi.tropix.galaxy.test;

import java.util.List;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData.TestInputFile;

@ContextConfiguration(locations = "testContext.xml")
public class TestDataExtracterTest extends FreshConfigTest {

  @Inject
  private TestDataExtracter testDataExtracter;
 
  @Test(groups = "unit")
  public void testCatExtract() {
    final List<TestData> testCases = testDataExtracter.getTestCases("cat1");
    assert testCases.size() == 1;
    final TestData testData1 = testCases.get(0);
    final List<TestInputFile> inputFiles = testData1.getInputFiles();
    assert inputFiles.size() == 2 : inputFiles.size();
    
  }
  
}
