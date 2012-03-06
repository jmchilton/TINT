package edu.umn.msi.tropix.storage.client.test;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.storage.service.StorageService;
import edu.umn.msi.tropix.storage.service.client.StorageServiceFactory;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/storage/client/context.xml")
public class CxfClientIntegrationTest extends FreshConfigTest {
  
  @Inject 
  private StorageServiceFactory storageServiceFactory;
    
  private boolean secure = true;

  @Test
  public void cleanTest() {
    final String address;
    if(secure) {
      address = "https://33.33.33.11:8743/";
    } else {
      address = "http://localhost:8780/";
    }

    StorageService testI = storageServiceFactory.get(address);
    final String dataId = UUID.randomUUID().toString();
    testI.getData("User", dataId);
  }
}
