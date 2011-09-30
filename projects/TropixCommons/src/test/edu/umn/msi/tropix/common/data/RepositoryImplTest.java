package edu.umn.msi.tropix.common.data;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContexts;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/common/data/context.xml")
public class RepositoryImplTest extends AbstractTestNGSpringContextTests {
  
  private Repository repository = Repositories.getInstance();
  
  @Test(groups = "unit")
  public void testRemoteFetch() {    
    final String contents = 
      InputContexts.toString(InputContexts.forInputStream(repository.getResource(getClass(), "test")));
    assert contents.equals("Hello World!");
  }

}
