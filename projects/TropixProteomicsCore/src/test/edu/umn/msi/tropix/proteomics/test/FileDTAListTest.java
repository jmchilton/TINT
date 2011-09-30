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
import java.util.HashSet;
import java.util.Iterator;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.proteomics.FileDTAListImpl;
import edu.umn.msi.tropix.proteomics.DTAList.Entry;

public class FileDTAListTest {
  private static FileUtils fileUtils = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void write() throws IOException {
    File parentDirectory = null;
    try {
      parentDirectory = fileUtils.createTempDirectory();
      final FileDTAListImpl dtaList = new FileDTAListImpl(Directories.fromFile(parentDirectory));

      final String name = "moo.123.125.1.dta";
      final File file = new File(parentDirectory, name);
      dtaList.add("Moo".getBytes(), name);
      assert fileUtils.readFileToString(file).equals("Moo");
    } finally {
      fileUtils.deleteDirectory(parentDirectory);
    }
  }

  @Test(groups = "unit")
  public void populate() throws IOException {
    populate(true);
    populate(false);
  }

  public void populate(final boolean setBaseName) throws IOException {
    final String baseName = setBaseName ? "Nature2" : "file";
    File parentDirectory = null;
    try {
      parentDirectory = fileUtils.createTempDirectory();
      final FileDTAListImpl dtaList = new FileDTAListImpl(Directories.fromFile(parentDirectory));
      fileUtils.touch(new File(parentDirectory, "file.100.100.1.dta"));
      fileUtils.touch(new File(parentDirectory, "file.101.106.2.dta"));
      if(setBaseName) {
        dtaList.populate("Nature2");
      } else {
        dtaList.populate();
      }
      int count = 0;
      final HashSet<String> suffixes = new HashSet<String>();
      final HashSet<String> expectedSuffixes = new HashSet<String>();
      expectedSuffixes.add(".100.100.1.dta");
      expectedSuffixes.add(".101.106.2.dta");
      for(final Entry entry : dtaList) {
        count++;
        assert entry.getName().startsWith(baseName);
        suffixes.add(entry.getName().substring(baseName.length()));
      }
      assert count == 2 : "Incorrect number of files from iterator";
      assert !suffixes.isEmpty() : "Suffixes are empty.";
      assert expectedSuffixes.equals(suffixes) : "Incorrect suffixes";

      final Iterator<Entry> entryIterator = dtaList.iterator();
      entryIterator.next();
      entryIterator.remove();

      final Iterator<Entry> newEntryIterator = dtaList.iterator();
      newEntryIterator.next();
      assert !newEntryIterator.hasNext();
    } finally {
      fileUtils.deleteDirectory(parentDirectory);
    }
  }

}
