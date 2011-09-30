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

package edu.umn.msi.tropix.proteomics.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.Instruments;

public class InstrumentsTest {

  @Test(groups = {"unit"})
  public void testInstruments() throws IOException {
    testInstruments(false);
  }

  @Test(groups = {"unit"})
  public void testInstrumentsFromFile() throws IOException {
    testInstruments(true);
  }

  public void testInstruments(final boolean fromFile) throws IOException {
    Instruments instruments;
    if(fromFile) {
      final File tempFile = File.createTempFile("tpxtest", "");
      tempFile.deleteOnExit();
      final FileOutputStream stream = new FileOutputStream(tempFile);
      IOUtils.copy(getClass().getResourceAsStream("instruments.csv"), stream);
      instruments = new Instruments(tempFile.getAbsolutePath());
    } else {
      instruments = new Instruments(ProteomicsTests.getResourceAsStream("instruments.csv"));
    }
    assert instruments.ionUsed("Default", "1+ fragments") != null : "Null check 1";
    assert instruments.ionUsed("Default", "w or w' series ions") != null : "Null check 2";
    assert instruments.ionUsed("MALDI QIT TOF", "1+ fragments") != null : "Null check 3";
    assert instruments.ionUsed("MALDI QIT TOF", "w or w' series ions") != null : "Null check 4";
    assert instruments.ionUsed("ETD TRAP", "c series ions") != null : "Null check 5";
    assert instruments.ionUsed("Default", "c series ions") != null : "Null check 6";
    assert instruments.ionUsed("MALDI QUAD TOF", "c series ions") != null : "Null check 7";

    assert instruments.ionUsed("NOT AN INSTRUMENT", "NOT AN ION SERIES") == null;

    assert instruments.ionUsed("Default", "1+ fragments") : "Check 1";
    assert !instruments.ionUsed("Default", "w or w' series ions") : "Check 2";
    assert instruments.ionUsed("MALDI QIT TOF", "1+ fragments") : "Check 3";
    assert !instruments.ionUsed("MALDI QIT TOF", "w or w' series ions") : "Check 4";
    assert instruments.ionUsed("ETD TRAP", "c series ions") : "Check 5";
    assert !instruments.ionUsed("Default", "c series ions") : "Check 6";
    assert !instruments.ionUsed("MALDI QUAD TOF", "c series ions") : "Check 7";
  }
}