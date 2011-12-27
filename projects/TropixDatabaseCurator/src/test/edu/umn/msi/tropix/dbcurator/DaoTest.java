package edu.umn.msi.tropix.dbcurator;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.mayo.mprc.database.DatabasePlaceholder;
import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "testContext.xml")
public class DaoTest extends FreshConfigTest {

  @Inject
  private DatabasePlaceholder databasePopulator;
  
  @Test(groups = "spring")
  public void testDaoOperations() {
    
  }
  
}
