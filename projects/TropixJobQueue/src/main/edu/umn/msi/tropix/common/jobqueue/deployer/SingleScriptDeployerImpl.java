package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;


public abstract class SingleScriptDeployerImpl extends BaseDeployerImpl {

  public SingleScriptDeployerImpl() {
    super();
  }

  protected abstract String getResourceName();

  @Override
  protected void deploy() {
    final File scriptFile = getFile(getResourceName());
    if(!scriptFile.exists()) {
      copyResource(getResourceName());
      makeExecutable(scriptFile);
    }
    addProperty(scriptFile.getAbsolutePath());
  }

}