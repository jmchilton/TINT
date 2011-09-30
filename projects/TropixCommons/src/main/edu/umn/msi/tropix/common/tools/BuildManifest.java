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

package edu.umn.msi.tropix.common.tools;

import java.io.File;
import java.util.Collection;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class BuildManifest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  public static void main(final String[] args) {
    final File scriptsDir = new File(args[0]);
    final File libDir = new File(scriptsDir, "lib");
    final File out = new File(args[1]);
    final Collection<File> jarFiles = FILE_UTILS.listFiles(libDir);
    final StringBuilder contents = new StringBuilder();
    contents.append("Manifest-Version: 1.0\nMain-Class: " + args[2] + "\nClass-Path:");
    for(final File jarFile : jarFiles) {
      contents.append(" lib/" + jarFile.getName());
    }
    FILE_UTILS.writeStringToFile(out, contents.toString());
  }

}
