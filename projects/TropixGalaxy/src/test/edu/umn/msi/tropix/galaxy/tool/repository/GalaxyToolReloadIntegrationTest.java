package edu.umn.msi.tropix.galaxy.tool.repository;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "testLoadContext.xml")
public class GalaxyToolReloadIntegrationTest extends FreshConfigTest {
  @Inject
  private TestToolSourceImpl testToolSourceImpl;
  
  @Inject
  private GalaxyToolLoader toolLoader;
  
  @Test(groups = "spring")
  public void testReload() {
    testToolSourceImpl.addParam();
    toolLoader.updatePersistentGalaxyToolStore();
  }
}
