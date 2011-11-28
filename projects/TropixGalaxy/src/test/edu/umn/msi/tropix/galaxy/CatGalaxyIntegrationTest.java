package edu.umn.msi.tropix.galaxy;

import javax.inject.Inject;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.galaxy.tool.repository.GalaxyToolRepository;

public class CatGalaxyIntegrationTest extends BaseGalaxyIntegrationTest {
  
  @Inject
  private GalaxyToolRepository testToolSource;
    
  @Test(groups = "spring")
  public void testLoad() throws InterruptedException {
    testTool("cat1", testToolSource);
  }

}
