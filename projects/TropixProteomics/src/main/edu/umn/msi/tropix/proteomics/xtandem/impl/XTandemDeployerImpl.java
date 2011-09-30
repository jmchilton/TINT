/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.io.File;

import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class XTandemDeployerImpl extends BaseDeployerImpl {
  private static final String LINUX_APPLICATION_PREFIX = "tandem-linux-08-12-01-1";
  //private static final String UBUNTU_APPLICATION_PREFIX = "tandem-ubuntu-09-04-01-4";
  private static final String MACOSX_APPLICATION_PREFIX = "tandem-osx-intel-08-12-01-1";
  private static final String WINDOWS_APPLICATION_PREFIX = "tandem-win32-08-12-01-1";

  /*
  private boolean isUbuntu() {
    try {
      final ProcessConfiguration processConfiguration = new ProcessConfiguration();
      processConfiguration.setApplication("/bin/uname");
      processConfiguration.setArguments(Lists.newArrayList("-a"));
      final ProcessFactory processFactory = Processes.getDefaultFactory();
      final Process process = processFactory.createProcess(processConfiguration);
      process.waitFor();
      final String output = IOUtilsFactory.getInstance().toString(process.getInputStream()).toLowerCase();
      return output.contains("ubuntu");
    } catch(Exception e) {
      // That process should work fine on Ubuntu, so this exception
      // is an indication this is not Ubuntu (cannot say for sure though). 
      return false;
    }
  }
  */
  
  private String getApplicationPrefix() {
    String prefix = LINUX_APPLICATION_PREFIX;
    if(isMacOsX()) {
      prefix = MACOSX_APPLICATION_PREFIX;
    } else if(isWindows()) {
      prefix = WINDOWS_APPLICATION_PREFIX;
    }
    // else if(isUbuntu()) {
    //  prefix = UBUNTU_APPLICATION_PREFIX;
    //}
    return prefix;
  }

  @Override
  protected void deploy() {
    final File application = getFile(getApplicationPrefix() + File.separator + "bin" + File.separator + "tandem.exe");
    if(!application.exists()) {
      getFile(getApplicationPrefix()).mkdirs();
      final String compressedName = getApplicationPrefix() + (isWindows() ? ".zip" : ".tar.gz");
      final File compressedFile = getFile(compressedName);
      copyResource(compressedName);
      decompress(compressedFile);
      makeExecutable(application);
    }
    addProperty(application.getAbsolutePath());
  }

}
