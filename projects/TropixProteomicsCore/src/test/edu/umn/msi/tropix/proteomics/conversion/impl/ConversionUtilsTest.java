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

package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.util.LinkedList;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun.ParentFile;

import org.testng.annotations.Test;

public class ConversionUtilsTest {
  public static final String MOO_SHA1 = "24a56b37819e0452df9c07432e5dd2e2b5cebf48";

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void unknownAlogrithm() {
    ConversionUtils.getDigest("moo".getBytes(), "MOOCOW");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void findParentFile() {
    ConversionUtils.findParentFile(new LinkedList<ParentFile>(), "433452345");
  }

  @Test(groups = "unit")
  public void getSHA1() {
    assert ConversionUtils.getSHA1("moo").equals(MOO_SHA1);
  }

  private static void assertSanitizedWithExtensionIs(final String name, final String extension, final String expected) {
    final String result = ConversionUtils.getSanitizedName(name, extension);
    assert result.equals(expected) 
      : String.format("Expecting sanitized version of %s would be %s, but it was %s ", name, expected, result);
  }
  
  @Test(groups = "unit")
  public void testGetSanitizedName() {
    assertSanitizedWithExtensionIs("moo.RAW", ".RAW", "moo.RAW");
    assertSanitizedWithExtensionIs("moo.raw", ".RAW", "moo.RAW");
    assertSanitizedWithExtensionIs("mo..o.raw", ".RAW", "mo__o.RAW");
    assertSanitizedWithExtensionIs("foo/moo/moo.raw", ".RAW", "moo.RAW");
  }
  
}
