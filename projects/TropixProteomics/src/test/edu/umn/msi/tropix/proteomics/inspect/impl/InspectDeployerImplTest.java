package edu.umn.msi.tropix.proteomics.inspect.impl;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.deployer.DeployerTest;

public class InspectDeployerImplTest extends DeployerTest {

  /*
   * @Test(groups = "unit")
   * public void testDeployWindows() {
   * setWindows();
   * final InspectDeployerImpl inspectDeployer = new InspectDeployerImpl();
   * inspectDeployer.setPythonPath("C:\\Python24\\");
   * deploy(inspectDeployer);
   * 
   * assertInspectUnziped();
   * 
   * final String batContents = getContents("inspect.bat");
   * final String expectedText = "call \"C:\\Python24\\python\" \"" + new File(getDeploymentDirectory(), "InspectToPepXML.py\"").getAbsolutePath();
   * assert batContents.contains(expectedText) : batContents + " does not contain " + expectedText;
   * assertPropertyValueIs(new File(getDeploymentDirectory(), "inspect.bat").getAbsolutePath());
   * }
   */

  private void assertInspectUnziped() {
    assert new File(getDeploymentDirectory(), "Inspect.exe").exists();
  }

  @Test(groups = "unit")
  public void testDeployLinux() {
    setLinux();
    expectExecuteInDirectory(getDeploymentDirectory(), "make");
    deploy(new InspectDeployerImpl());

    assertInspectUnziped();
    assert getContents("inspect.sh").contains("python " + new File(getDeploymentDirectory(), "InspectToPepXML.py")) : getContents("inspect.sh");
    assertPropertyValueIs(new File(getDeploymentDirectory(), "inspect.sh").getAbsolutePath());
  }

}
