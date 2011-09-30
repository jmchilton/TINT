package edu.umn.msi.tropix.proteomics.inspect.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class InspectDeployerImpl extends BaseDeployerImpl {
  private static final Log LOG = LogFactory.getLog(InspectDeployerImpl.class);
  private String pythonPath;
  
  public void setPythonPath(final String pythonPath) {
    this.pythonPath = pythonPath;
  }
  
  protected void deploy() {
    decompressResource("inspect.zip");
    final String inspectDirPath = asDirectoryPath(getDeploymentDirectory());
    if(isWindows()) {
      final String pythonDirPath = asDirectoryPath(pythonPath);
      copyResourceWithReplacements("inspect.bat", newReplacements().put("INSPECT_PATH", inspectDirPath).put("PYTHON_PATH", pythonDirPath));
      addProperty(getFile("inspect.bat").getAbsolutePath());
    } else {
      try {
        executeInDirectory(getDeploymentDirectory(), "make"); // Build inspect on this linux system
        copyResourceWithReplacements("inspect.sh", newReplacements().put("INSPECT_PATH", inspectDirPath));
      } catch(final RuntimeException e) {
        LOG.warn("Failed to compile InsPecT, local inspect implementation will not work.");
      }
      addProperty(getFile("inspect.sh").getAbsolutePath());
    }
    
  }

}
