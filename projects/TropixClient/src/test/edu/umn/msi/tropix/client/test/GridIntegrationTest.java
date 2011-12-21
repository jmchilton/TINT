package edu.umn.msi.tropix.client.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.client.credential.GlobusCredentialOptions;
import edu.umn.msi.tropix.client.credential.GlobusCredentialProvider;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;


@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/client/test/testApplicationContext.xml")
public class GridIntegrationTest extends AbstractTestNGSpringContextTests {

  @Inject
  private StorageDataFactory storageDataFactory;
  
  @Inject
  private AuthenticationSourceManager authManager;
  
  @Inject
  private GlobusCredentialProvider credentialProvider;
  
  @Test(groups = "interaction")
  public void testMoo() throws IOException {
    //System.out.print("Password:");
    //System.out.flush();
    Class clazz = javax.xml.parsers.DocumentBuilderFactory.class;
    final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    //final String password = System.getProperty("password");
    final String password = edu.umn.msi.tropix.common.io.FileUtilsFactory.getInstance().readFileToString("/home/john/password").trim();
    
    System.out.println("Using password [" + password + "]");
    final GlobusCredentialOptions options = authManager.getAuthenticationOptions("MSI Account");
    final Credential credential = credentialProvider.getGlobusCredential("chilton", password, options);
    final StorageData storageData = storageDataFactory.getStorageData("e03ce286-2778-4290-a38a-92cb236c602a", "https://tint-storage.msi.umn.edu:8443/caGridTransfer/services/cagrid/TropixStorage", credential);
    System.out.println(InputContexts.toString(storageData.getDownloadContext()));
  }
  
  
}
