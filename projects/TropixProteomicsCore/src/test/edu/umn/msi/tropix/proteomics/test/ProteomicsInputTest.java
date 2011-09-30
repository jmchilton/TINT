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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.BiomlWriter;

public class ProteomicsInputTest {

  @Test(groups = {"unit"})
  public void testProteomicsInput() throws Exception {
    testProteomicsInput(false);
  }

  @Test(groups = {"unit"})
  public void testProteomicsInputFromFile() throws Exception {
    testProteomicsInput(true);
  }

  public void testProteomicsInput(final boolean readFromFile) throws Exception {
    BiomlWriter input = null;
    if(readFromFile) {
      final File tempFile = File.createTempFile("tpxtest", "");
      tempFile.deleteOnExit();
      FileUtils.writeStringToFile(tempFile, IOUtils.toString(getClass().getResourceAsStream("proteomicsInput1.xml")));
      input = new BiomlWriter(tempFile.getAbsolutePath());
    } else {
      input = new BiomlWriter(getClass().getResourceAsStream("proteomicsInput1.xml"));

    }
    boolean wasException = false;
    try {
      input.addVariable("test heading", "test");
    } catch(final IllegalArgumentException e) {
      wasException = true;
    }
    assert wasException;
    input.addHeader("test heading 2");
    input.addVariable("test heading 2", "param1", "param1 value again");
    input.addVariable("test heading 2", "param2", "param2 value");
    final String xml = input.toString();
    assert xml.contains("label=\"test heading 2\"") : "XML did not contain heading 2 label";
    assert xml.contains("label=\"test heading 2, param1\"") : "XML did not contain param1 input";
    assert xml.contains("label=\"test heading 2, param2\"") : "XML did not contain param2 input";
    assert xml.contains(">param1 value again<") : "XML did not contain param1 value";
    assert xml.contains(">param2 value<") : "XML did not contain param2 value";
    assert xml.contains("label=\"test heading, param1\"") : "XML did not contain old param1 value";
    assert xml.contains(">param1 value<") : "XML did not contain old param1 value";
  }
}
