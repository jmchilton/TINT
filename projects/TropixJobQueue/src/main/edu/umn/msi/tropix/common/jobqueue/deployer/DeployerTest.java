package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class DeployerTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File deploymentDirectory;
  private DeployerContextImpl deployerContext = new DeployerContextImpl();
  private SimpleExecutor simpleExecutor;
  private String propertyValue;

  protected void assertPropertyValueIs(final String propertyValue) {
    assert this.propertyValue.equals(propertyValue);
  }

  protected void deploy(final BaseDeployerImpl deployer) {
    EasyMock.replay(simpleExecutor);
    initializeDeployer(deployer);
    propertyValue = deployer.get();
    EasyMockUtils.verifyAndReset(simpleExecutor);
  }

  @BeforeMethod(groups = "unit")
  public void initDeploymentContext() {
    deploymentDirectory = FILE_UTILS.createTempDirectory();
    simpleExecutor = EasyMock.createMock(SimpleExecutor.class);
  }

  @AfterMethod(groups = "unit")
  public void cleanUpDeploymentContext() {
    FILE_UTILS.deleteQuietly(deploymentDirectory);
  }

  protected File getDeploymentDirectory() {
    return deploymentDirectory;
  }

  protected void setWindows() {
    deployerContext.setOperationSystemDetector(new DefaultOperatingSystemDetectorImpl("Windows XP"));
  }

  protected void setLinux() {
    deployerContext.setOperationSystemDetector(new DefaultOperatingSystemDetectorImpl("Linux"));
  }

  protected void initializeDeployer(final BaseDeployerImpl baseDeployerImpl) {
    deployerContext.setDeploymentDirectory(deploymentDirectory);
    deployerContext.setSimpleExecutor(simpleExecutor);
    baseDeployerImpl.setDeployerContext(deployerContext);
  }

  protected String getContents(final String relPath) {
    return InputContexts.toString(InputContexts.forFile(new File(getDeploymentDirectory(), relPath)));
  }

  protected void expectExecuteInDirectory(final File directory, final String command, final String... arguments) {
    EasyMock.expect(simpleExecutor.executeInDirectory(directory, command, arguments)).andReturn(0);
  }

  protected void expectMakeExecutable(final File script) {
    EasyMock.expect(simpleExecutor.execute("/bin/chmod", "+x", script.getAbsolutePath())).andReturn(0);
  }

}
