package edu.umn.msi.tropix.galaxy.test;

import java.util.List;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData.TestInputFile;

@ContextConfiguration(locations = "testContext.xml")
public class TestDataExtracterTest extends FreshConfigTest {

  @Inject
  private TestDataExtracter testDataExtracter;

  private TestData testData;
  private RootInput rootInput;
  
  @BeforeMethod(groups = "unit")
  public void setupTestData() {
    final List<TestData> testCases = testDataExtracter.getTestCases("cat1");
    testData = testCases.get(0);
    rootInput = testData.getRootInput();
  }
 
  @Test(groups = "unit")
  public void testInputFiles() {
    final List<TestInputFile> inputFiles = testData.getInputFiles();
    assert inputFiles.size() == 2 : inputFiles.size();
  }
  
  @Test(groups = "unit")
  public void testRootInput() {
    assert GalaxyDataUtils.findInput("input1", rootInput.getInput()) != null; 
  }
  
}
