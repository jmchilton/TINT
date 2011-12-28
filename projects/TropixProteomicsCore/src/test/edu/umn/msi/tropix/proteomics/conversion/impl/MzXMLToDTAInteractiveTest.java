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

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAOptions;
import edu.umn.msi.tropix.proteomics.utils.DTAUtils;

public class MzXMLToDTAInteractiveTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "interactive")
  public void testConvert() {
    final File directory = FILE_UTILS.createTempDirectory();
    //try {
    
      System.out.println(directory.getAbsolutePath());
      final MzXMLToDTAConverterStreamingImpl converter = new MzXMLToDTAConverterStreamingImpl();
      final MzXMLToDTAOptions inputOptions = new MzXMLToDTAOptions();
      System.out.println("Converting");
      final DTAList list = converter.mzxmlToDTA(
          FILE_UTILS.getFileInputStream("/home/msi/chilton/localhome/leeanntest/edj_sal_combined_MQmgf2.mzXML"), inputOptions);
      System.out.println("Writing dta files");
      DTAUtils.writeDTAFiles(Directories.fromFile(directory), list);
      System.out.println(directory.getAbsolutePath());
      /*
       * final MzXMLToMGFConverterStreamingImpl mgfConverter = new MzXMLToMGFConverterStreamingImpl();
       * final File mgfFile = FILE_UTILS.createTempFile();
       * mgfConverter.mzxmlToMGF(FILE_UTILS.getFileInputStream("/home/john/Downloads/TrpDig_001.mzXML"), FILE_UTILS.getFileOutputStream(mgfFile));
       * System.out.println(mgfFile.getAbsolutePath());
       */
    //} finally {
    //  FILE_UTILS.deleteDirectoryQuietly(directory);
    //}
  }

}
