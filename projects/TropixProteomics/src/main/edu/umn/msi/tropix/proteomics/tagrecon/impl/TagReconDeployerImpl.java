package edu.umn.msi.tropix.proteomics.tagrecon.impl;

import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class TagReconDeployerImpl extends BaseDeployerImpl {

  protected void deploy() {
    getDeploymentDirectory().mkdirs();
    copyResource("blosum62.fas");
    copyResource("unimod.xml");
    if(isWindows()) {
      decompressResource("tagrecon.zip");

      copyResourceWithReplacements("tagrecon.bat", newReplacements().put("TAGRECON_PATH", asDirectoryPath(getFile("tagrecon").getAbsolutePath())));
      addProperty(getFile("tagrecon.bat").getAbsolutePath());
    } else {

      copyResource("tagrecon");
      copyResource("directag");

      copyResourceWithReplacements("tagrecon.sh", newReplacements().put("TAGRECON_CONFIG_PATH", getDeploymentDirectory().getAbsolutePath()));
      makeExecutable(getFile("tagrecon"));
      makeExecutable(getFile("directag"));
      makeExecutable(getFile("tagrecon.sh"));
      addProperty(getFile("tagrecon.sh").getAbsolutePath());
    }
  }

}
