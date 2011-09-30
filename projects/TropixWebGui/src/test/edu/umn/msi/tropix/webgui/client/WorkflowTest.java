package edu.umn.msi.tropix.webgui.client;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantProxies;

public class WorkflowTest {

  @Test(groups = "unit")
  public void testConstants() {
    final ComponentConstants constants = ConstantProxies.getProxy(ComponentConstants.class);

    System.out.println(constants.bulkDownload());
  }

}
