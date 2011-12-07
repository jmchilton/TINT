package edu.umn.msi.tropix.galaxy;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.galaxy.tool.repository.GalaxyToolRepository;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/galaxy/client/proteomics/context.xml")
public class ProteomicsGalaxyIntegrationTest extends BaseGalaxyIntegrationTest {

  @Inject
  private GalaxyToolRepository testToolSource;

  @Test(groups = "spring")
  public void testFilterById() throws InterruptedException {
    testTool("filter_by_an_id", testToolSource);
  }

  @Test(groups = "spring")
  public void testPep2Pro() {
    testTool("pep2pro", testToolSource);
  }
  
  @Test(groups = "spring")
  public void testCDNATranslation() {
    testTool("cdna_translation", testToolSource);
  }

  // @Test(groups = "spring")
  public void testMsConvert() {
    testTool("msconvert", testToolSource);
  }

}
