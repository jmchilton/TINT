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
import java.io.IOException;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.proteomics.impl.ByteDTAListImpl;
import edu.umn.msi.tropix.proteomics.impl.DTAListWriterImpl;

public class DTAListWriterImplTest {
  private static FileUtils fileUtils = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void writeDTAList() throws IOException {
    final DTAListWriterImpl writer = new DTAListWriterImpl();
    final ByteDTAListImpl list = new ByteDTAListImpl();
    list.add("Hello1".getBytes(), "abc.123.125.2.dta");
    list.add("Hello2".getBytes(), "abc.123.125.3.dta");
    final File tempDir = FileUtilsFactory.getInstance().createTempDirectory();
    try {
      writer.writeFiles(Directories.fromFile(tempDir), list);
      assert "Hello1".equals(fileUtils.readFileToString(new File(tempDir, "abc.123.125.2.dta")));
      assert "Hello2".equals(fileUtils.readFileToString(new File(tempDir, "abc.123.125.3.dta")));
    } finally {
      fileUtils.deleteDirectoryQuietly(tempDir);
    }
  }
}
