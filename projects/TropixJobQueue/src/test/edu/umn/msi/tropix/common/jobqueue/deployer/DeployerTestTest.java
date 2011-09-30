package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeployerTestTest {

  private ConcreteDeployerTest deployerTest;
  private ConcreteDeployerImpl deployer;
  private File deploymentDirectory;

  // Allows for the testing of DeployerTest's protected methods
  public static class ConcreteDeployerTest extends DeployerTest {

    @Override
    public void initializeDeployer(final BaseDeployerImpl baseDeployerImpl) {
      super.initializeDeployer(baseDeployerImpl);
    }

    @Override
    public void deploy(final BaseDeployerImpl baseDeployerImpl) {
      super.deploy(baseDeployerImpl);
    }

    @Override
    public void assertPropertyValueIs(final String propertyValue) {
      super.assertPropertyValueIs(propertyValue);
    }

  }

  class ConcreteDeployerImpl extends BaseDeployerImpl {

    protected void deploy() {
      addProperty("foo");
    }

    @Override
    public File getDeploymentDirectory() {
      return super.getDeploymentDirectory();
    }

    @Override
    public boolean isWindows() {
      return super.isWindows();
    }

  }

  @BeforeMethod(groups = "unit")
  public void setupTestClassAndDeployer() {
    deployerTest = new ConcreteDeployerTest();
    deployerTest.initDeploymentContext();

    deployer = new ConcreteDeployerImpl();
    deployerTest.initializeDeployer(deployer);
    deploymentDirectory = deployer.getDeploymentDirectory();

    assert deploymentDirectory != null;
    deploymentDirectory.mkdirs();
    assert deploymentDirectory.exists();
  }

  @Test(groups = "unit")
  public void testSetWindows() {
    deployerTest.setWindows();
    assert deployer.isWindows();
  }

  @Test(groups = "unit")
  public void testSetLinux() {
    deployerTest.setLinux();
    assert !deployer.isWindows();
  }

  @Test(groups = "unit")
  public void testAssertPropertyValueIs() {
    deployerTest.deploy(deployer);
    deployerTest.assertPropertyValueIs("foo");
    AssertionError error = null;
    try {
      deployerTest.assertPropertyValueIs("bar");
    } catch(AssertionError e) {
      error = e;
    }
    assert error != null;

  }

  @AfterMethod(groups = "unit")
  public void tearDownTestClassAndVerifyDirectoryDeleted() {
    deployerTest.cleanUpDeploymentContext();
    assert !deploymentDirectory.exists();
  }

}
