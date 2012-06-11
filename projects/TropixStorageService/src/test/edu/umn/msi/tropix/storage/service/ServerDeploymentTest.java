package edu.umn.msi.tropix.storage.service;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.storage.service.client.StorageServiceFactory;

@ContextConfiguration(locations = {"client/context.xml",
    "client/context.xml",
    "impl/httpsContext.xml"})
public class ServerDeploymentTest extends AbstractTestNGSpringContextTests {

  @Inject
  private StorageServiceFactory storageServiceFactory;

  @Test
  public void cleanTest() {
    final String address;
    address = "https://localhost:8743/";

    StorageService testI = storageServiceFactory.get(address);
    final String dataId = UUID.randomUUID().toString();
    testI.getData("User", dataId);
  }
}
