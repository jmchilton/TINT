package edu.umn.msi.tropix.ssh;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "test-context.xml")
public class SshServerInteractionTest  extends FreshConfigTest {

  @Inject
  private SshServerWrapper sshServer;
  
  @Inject
  private TestDataCreator testDataCreator;
  
  @Test(groups = "interaction")
  public void launchServer() throws InterruptedException {
    testDataCreator.create();    
    sshServer.start();
    Thread.sleep(1000 * 60 * 60);
  }
  
}
