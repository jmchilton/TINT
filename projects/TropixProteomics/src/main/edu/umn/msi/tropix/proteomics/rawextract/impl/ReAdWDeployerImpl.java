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

package edu.umn.msi.tropix.proteomics.rawextract.impl;

import java.io.File;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class ReAdWDeployerImpl extends BaseDeployerImpl {

  @Override
  protected void deploy() {
    final Iterable<String> fileNames = Lists.newArrayList("ReAdW.exe", "ReAdW.sh", "zlib1.dll");
    for(final String fileName : fileNames) {
      final File file = getFile(fileName);
      if(!file.exists()) {
        copyResource(fileName);
        makeExecutable(file);
      }
    }
    final String extension = isWindows() ? "exe" : "sh";
    final File application = getFile("ReAdW." + extension);
    addProperty(application.getAbsolutePath());
  }

}
