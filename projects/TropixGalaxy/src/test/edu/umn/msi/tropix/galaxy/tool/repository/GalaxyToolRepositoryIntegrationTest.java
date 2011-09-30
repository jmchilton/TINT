package edu.umn.msi.tropix.galaxy.tool.repository;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.galaxy.tool.Tool;

@ContextConfiguration(locations = "testContext.xml")
public class GalaxyToolRepositoryIntegrationTest extends FreshConfigTest {

  @Inject
  private GalaxyToolRepository repository;
  
  @Test(groups = "spring")
  public void testEverythingLoadedProperly() {
    final Tool tool = repository.loadForToolId("out2xml");
    Assert.assertNotNull(tool);
    
    Assert.assertNotNull(repository.loadForToolId("hello_world"));
  }
  
  
}
