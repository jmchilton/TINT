package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.io.OutputContext;

public interface DeployerContext {

  File getDeploymentDirectory();

  void setDeploymentDirectory(final File deploymentDirectory);

  void decompress(final File compressedFile);

  void makeExecutable(final File file);

  int execute(final ProcessConfiguration processConfiguration);

  int execute(final String application, final String... arguments);

  int executeInDirectory(final File directory, final String application, final String... arguments);

  File getFile(final String relativePath);

  OutputContext getOutputContext(final String relativePath);

  boolean isLinux();

  boolean isMacOsX();

  boolean isUnix();

  boolean isWindows();
  
}
