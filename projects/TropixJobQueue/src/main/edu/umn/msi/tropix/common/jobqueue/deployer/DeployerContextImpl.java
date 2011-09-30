package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import com.google.common.annotations.VisibleForTesting;

import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;

/**
 * Default implementation of DeployerContext.
 * 
 * @author John Chilton
 *
 */
public class DeployerContextImpl implements DeployerContext {
  private OperatingSystemDetector osDetector = new DefaultOperatingSystemDetectorImpl();
  private SimpleExecutor simpleExecutor = new DefaultSimpleExecutorImpl();
  private Decompresser decompresser = new DecompresserImpl();
  private File deploymentDirectory;

  @VisibleForTesting
  void setOperationSystemDetector(final OperatingSystemDetector osDetector) {
    this.osDetector = osDetector;
  }
  
  @VisibleForTesting
  void setSimpleExecutor(final SimpleExecutor simpleExecutor) {
    this.simpleExecutor = simpleExecutor;
  }

  public File getDeploymentDirectory() {
    return deploymentDirectory;
  }

  public void setDeploymentDirectory(final File deploymentDirectory) {
    this.deploymentDirectory = deploymentDirectory;
  }


  public void decompress(final File compressedFile) {
    decompresser.decompress(compressedFile);
  }

  public void makeExecutable(final File file) {
    if(isUnix()) {
      final int returnVal = execute("/bin/chmod", "+x", file.getAbsolutePath());
      if(returnVal != 0) {
        throw new RuntimeException("Failed to make file " + file.getAbsolutePath() + " executable. chmod returned " + returnVal);
      }
    }
  }

  public int execute(final ProcessConfiguration processConfiguration) {
    return simpleExecutor.execute(processConfiguration);
  }

  public int execute(final String application, final String... arguments) {
    return simpleExecutor.execute(application, arguments);
  }

  public int executeInDirectory(final File directory, final String application, final String... arguments) {
    return simpleExecutor.executeInDirectory(directory, application, arguments);
  }
  
  public File getFile(final String relativePath) {
    return new File(deploymentDirectory, relativePath);
  }

  public OutputContext getOutputContext(final String relativePath) {
    final File file = getFile(relativePath);
    file.getParentFile().mkdirs();
    return OutputContexts.forFile(file);
  }

  public boolean isLinux() {
    return osDetector.isLinux();
  }

  public boolean isMacOsX() {
    return osDetector.isMacOsX();
  }

  public boolean isUnix() {
    return osDetector.isUnix();
  }

  public boolean isWindows() {
    return osDetector.isWindows();
  }
  
}
