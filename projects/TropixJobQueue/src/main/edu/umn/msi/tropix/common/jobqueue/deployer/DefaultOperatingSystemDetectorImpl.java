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

package edu.umn.msi.tropix.common.jobqueue.deployer;

import com.google.common.annotations.VisibleForTesting;

class DefaultOperatingSystemDetectorImpl implements OperatingSystemDetector {
  DefaultOperatingSystemDetectorImpl() {
    this(System.getProperty("os.name"));
  }
  
  @VisibleForTesting
  DefaultOperatingSystemDetectorImpl(final String osNameString) {
    this.osNameString = osNameString.toLowerCase();
  }
  
  private final String osNameString;
  
  public boolean isUnix() {
    return !isWindows(); // Admittedly a bit of an exaggeration, but should suffice for most platforms Tropix will be installed on
  }

  public boolean isWindows() {
    return osNameString.contains("windows");
  }

  public boolean isLinux() {
    return osNameString.contains("linux");
  }

  public boolean isMacOsX() {
    return osNameString.contains("mac os x");
  }

}
