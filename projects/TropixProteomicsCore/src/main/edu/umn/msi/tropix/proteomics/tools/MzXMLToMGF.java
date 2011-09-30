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

package edu.umn.msi.tropix.proteomics.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.FilenameUtils;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.conversion.impl.MzXMLToMGFConverterStreamingImpl;

public class MzXMLToMGF {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public static void main(final String[] args) throws Exception {
    final MzXMLToMGFConverter converter = new MzXMLToMGFConverterStreamingImpl();    
    final String baseName = FilenameUtils.getBaseName(args[0]);
    final File mgfFile = new File(baseName + ".mgf");
    final FileInputStream inputStream = FILE_UTILS.getFileInputStream(args[0]); 
    final FileOutputStream outputStream = new FileOutputStream(mgfFile);
    try {
      converter.mzxmlToMGF(inputStream, outputStream, null);
    } finally {
      IO_UTILS.closeQuietly(outputStream);
      IO_UTILS.closeQuietly(inputStream);
    }
  }

}
