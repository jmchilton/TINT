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

package edu.umn.msi.tropix.proteomics.omssa.impl;

import java.io.File;
import java.util.regex.Matcher;

import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.common.jobqueue.deployer.BaseDeployerImpl;

public class OmssaScriptDeployerImpl extends BaseDeployerImpl {
  private static final String LINUX_BLAST_SUFFIX = "ia32-linux.tar.gz";
  private static final String MACOSX_BLAST_SUFFIX = "universal-macosx.tar.gz";
  private static final String WINDOWS_BLAST_SUFFIX = "windows.zip";

  private String blastHome = null, omssaHome = null;

  private String getBlastApplication() {
    String application = LINUX_BLAST_SUFFIX;
    if(isMacOsX()) {
      application = MACOSX_BLAST_SUFFIX;
    } else if(isWindows()) {
      application = WINDOWS_BLAST_SUFFIX;
    }
    return "blast-2.2.21-" + application;
  }

  private String getOmssaApplication() {
    String suffix = "linux.tar.gz";
    if(isMacOsX()) {
      suffix = "macos.tar.gz";
    } else if(isWindows()) {
      suffix = "windows.zip";
    }
    return "omssa-" + suffix;
  }

  private void deployBlast() {
    final File blastHome = getFile("blast-2.2.21");
    if(!blastHome.exists()) {
      final String compressedBlast = getBlastApplication();
      final File compressedFile = getFile(compressedBlast);
      compressedFile.getParentFile().mkdirs();
      copyResource(compressedBlast);
      decompress(compressedFile);
    }
    this.blastHome = blastHome.getAbsolutePath();
  }

  private void deployOmssa() {
    final File omssaHome = getFile("omssa-2.1.4." + (isWindows() ? "win32" : (isMacOsX() ? "macos" : "linux")));
    if(!omssaHome.exists()) {
      final String compressedOmssa = getOmssaApplication();
      final File compressedFile = getFile(compressedOmssa);
      compressedFile.getParentFile().mkdirs();
      copyResource(compressedOmssa);
      decompress(compressedFile);
    }
    this.omssaHome = omssaHome.getAbsolutePath();
  }

  @Override
  protected void deploy() {
    if(!StringUtils.hasText(blastHome)) {
      deployBlast();
    }
    if(!StringUtils.hasText(omssaHome)) {
      deployOmssa();
    }
    final String omssaScript = "omssa." + (isWindows() ? "bat" : "sh");    
    final File omssaScriptFile = getFile(omssaScript);
    String omssaScriptContents = InputContexts.toString(getResource(omssaScript));
    omssaScriptContents = omssaScriptContents.replaceAll("BLAST_HOME", Matcher.quoteReplacement(blastHome));
    omssaScriptContents = omssaScriptContents.replaceAll("OMSSA_HOME", Matcher.quoteReplacement(omssaHome));
    OutputContexts.forFile(omssaScriptFile).put(omssaScriptContents.getBytes());
    makeExecutable(omssaScriptFile);
    addProperty(omssaScriptFile.getAbsolutePath());
  }

  public void setBlastHome(final String blastHome) {
    this.blastHome = blastHome;
  }

  public void setOmssaHome(final String omssaHome) {
    this.omssaHome = omssaHome;
  }

}
