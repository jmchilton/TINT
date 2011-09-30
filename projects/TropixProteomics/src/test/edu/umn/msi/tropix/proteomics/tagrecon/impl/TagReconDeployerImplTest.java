package edu.umn.msi.tropix.proteomics.tagrecon.impl;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.deployer.DeployerTest;

public class TagReconDeployerImplTest extends DeployerTest {

  @Test(groups = "unit")
  public void testDeployWindows() {
    setWindows();
    deploy();
    assert new File(getDeploymentDirectory(), "tagrecon").isDirectory();

    final String batScriptContents = getContents("tagrecon.bat");
    assert batScriptContents.contains("call \"" + new File(new File(super.getDeploymentDirectory(), "tagrecon"), "directag\"").getAbsolutePath()) : batScriptContents;
    assert batScriptContents.contains("call \"" + new File(new File(super.getDeploymentDirectory(), "tagrecon"), "tagrecon\"").getAbsolutePath());
    assertPropertyValueIs(new File(getDeploymentDirectory(), "tagrecon.bat").getAbsolutePath());
  }

  private void deploy() {
    deploy(new TagReconDeployerImpl());
  }

  @Test(groups = "unit")
  public void testLinux() {
    setLinux();
    expectMakeExecutable(new File(getDeploymentDirectory(), "tagrecon.sh"));
    expectMakeExecutable(new File(getDeploymentDirectory(), "directag"));
    expectMakeExecutable(new File(getDeploymentDirectory(), "tagrecon"));
    deploy();

    assert new File(getDeploymentDirectory(), "blosum62.fas").exists();
    assert new File(getDeploymentDirectory(), "unimod.xml").exists();

    assert new File(getDeploymentDirectory(), "tagrecon").exists();
    assert new File(getDeploymentDirectory(), "directag").exists();

    final String shScriptContents = getContents("tagrecon.sh");
    shScriptContents.contains(new File(getDeploymentDirectory(), "directag").getAbsolutePath());
    shScriptContents.contains(new File(getDeploymentDirectory(), "tagrecon").getAbsolutePath());
    assertPropertyValueIs(new File(getDeploymentDirectory(), "tagrecon.sh").getAbsolutePath());
  }

}
