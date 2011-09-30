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

import org.testng.annotations.Test;

public class DefaultOperatingSystemDetectorImplTest {
  // From list http://lopica.sourceforge.net/os.html
  private static final String OS_X_NAME = "Mac OS X";
  private static final String LINUX_NAME = "Linux";
  private static final String WINDOWS_2000_NAME = "Windows 2000"; 
  private static final String WINDOWS_XP_NAME = "Windows XP";
  private static final String SOLARIS_NAME = "SunOS";
  
  @Test(groups = "unit")
  public void testIsUnix() {
    assert osDetectorFromName(OS_X_NAME).isUnix();
    assert osDetectorFromName(LINUX_NAME).isUnix();
    assert osDetectorFromName(SOLARIS_NAME).isUnix();
    assert !osDetectorFromName(WINDOWS_XP_NAME).isUnix();
    assert !osDetectorFromName(WINDOWS_2000_NAME).isUnix();
  }
  
  @Test(groups = "unit")
  public void testIsLinux() {
    assert osDetectorFromName(LINUX_NAME).isLinux();
    assert !osDetectorFromName(OS_X_NAME).isLinux();
    assert !osDetectorFromName(WINDOWS_XP_NAME).isLinux();
  }
  
  @Test(groups = "unit")
  public void testIsWindows() {
    assert !osDetectorFromName(LINUX_NAME).isWindows();
    assert !osDetectorFromName(OS_X_NAME).isWindows();
    assert osDetectorFromName(WINDOWS_XP_NAME).isWindows();
    assert osDetectorFromName(WINDOWS_2000_NAME).isWindows();    
  }
  
  @Test(groups = "unit")
  public void testIsMacOsX() {
    assert !osDetectorFromName(LINUX_NAME).isMacOsX();
    assert osDetectorFromName(OS_X_NAME).isMacOsX();
    assert !osDetectorFromName(WINDOWS_XP_NAME).isMacOsX();
  }

  private DefaultOperatingSystemDetectorImpl osDetectorFromName(final String name) {
    return new DefaultOperatingSystemDetectorImpl(name);
  }
  
}
