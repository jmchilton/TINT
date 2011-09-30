package edu.umn.msi.tropix.proteomics.myrimatch.impl;

import java.io.File;

import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class MyriMatchDeployerImpl extends BaseDeployerImpl {

  protected void deploy() {
    if(isUnix()) {
      final File executable = getFile("myrimatch");
      if(!executable.exists()) {
        getDeploymentDirectory().mkdirs();
        copyResource("myrimatch");
        makeExecutable(executable);
      }
      addProperty(executable.getAbsolutePath());
    } else {
      File executable = getFile("myrimatch/myrimatch.exe");
      if(!executable.exists()) {
        getDeploymentDirectory().mkdirs();
        final File myriMatchZip = getFile("myrimatch.zip");
        copyResource("myrimatch.zip");
        decompress(myriMatchZip);
      }
      addProperty(executable.getAbsolutePath());
    }
  }

}
