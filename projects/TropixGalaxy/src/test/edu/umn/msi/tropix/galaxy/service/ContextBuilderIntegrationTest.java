package edu.umn.msi.tropix.galaxy.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.tool.repository.GalaxyToolRepository;

@ContextConfiguration
public class ContextBuilderIntegrationTest extends AbstractTestNGSpringContextTests {
  
  @Inject
  private GalaxyToolRepository testToolSource;
  
  @Inject 
  private TestDataExtracter testDataExtractor;
  
  private Context buildContextForTest(final String resource, final int testIndex) {
    final Tool tool = testToolSource.loadForToolId(resource);
    final List<TestData> testDataList = testDataExtractor.getTestCases(resource);
    final TestData testData = testDataList.get(testIndex);
    
    final RootInput rootInput = testData.getRootInput();
    final Context context = new ContextBuilder().buildContext(tool, rootInput, ImmutableMap.<String, String>builder().build());
    return context;
  }
  
  @Test(groups = "spring")
  public void testCatRepeat() {
    final Context catContext = buildContextForTest("cat1", 0);    
    // Just make sure exception isn't thrown
    CheetahTemplateEvaluator.evaluate("#for $q in $queries\n  ${q.input2}#end for", catContext);
  }
  
}
