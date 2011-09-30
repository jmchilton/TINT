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

import java.io.StringWriter;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.mascot.MascotMIMEWriter;

public class MascotMIMEWriterTest {
  private StringWriter sWriter;
  private MascotMIMEWriter mWriter;
  private final String boundary = "31415927";
  private final String fileName = "file.test";
  private final byte[] fileContents = "1 2\n3.0 4.0".getBytes();

  @BeforeClass(groups = {"unit"})
  public void init() throws Exception {
    sWriter = new StringWriter();
    mWriter = new MascotMIMEWriter(sWriter, boundary);
    mWriter.write("COM", "");
    mWriter.write("MODS", "SMA (K)");
    mWriter.writeFile(fileName, fileContents);
    mWriter.close();
  }

  @Test(groups = {"unit"})
  public void testFileContents() {
    final String output = sWriter.getBuffer().toString();
    assert output.contains("\n" + new String(fileContents) + "\n--");
  }

  @Test(groups = {"unit"})
  public void testFileName() {
    final String output = sWriter.getBuffer().toString();
    assert output.contains("name=\"FILE\"; filename=\"" + fileName + "\"");
  }

  int countInstances(final String query, final String string) {
    int count = 0;
    int start = 0;
    while((start = string.indexOf(query, start)) != -1) {
      start++;
      count++;
    }
    return count;
  }

  @Test(groups = {"unit"})
  public void testBoundaryCount() throws Exception {
    final String output = sWriter.getBuffer().toString();
    assert countInstances(boundary, output) == 4;
    assert countInstances("--" + boundary, output) == 4;
    assert countInstances("--" + boundary + "--", output) == 1;

  }

  @Test(groups = {"unit"})
  public void testHeader() {
    final String output = sWriter.getBuffer().toString();
    // assert output.startsWith("MIME-Version: 1.0");
    assert output.startsWith("--" + boundary + "\n");
  }

  @Test(groups = {"unit"})
  public void testFooter() {
    final String output = sWriter.getBuffer().toString();
    assert output.endsWith("--" + boundary + "--");
  }

  // public testHeader
}
