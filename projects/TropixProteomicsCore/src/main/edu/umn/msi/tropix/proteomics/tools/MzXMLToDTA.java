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
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAConverter;
import edu.umn.msi.tropix.proteomics.conversion.impl.MzXMLToDTAConverterStreamingImpl;

public class MzXMLToDTA {
  private static final Pattern DTA_PATTERN = Pattern.compile("(.*)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.[dD][tT][aA]$");
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  public static void main(final String[] args) throws Exception {
    final MzXMLToDTAConverter converter = new MzXMLToDTAConverterStreamingImpl();
    final InputStream mzxml = FILE_UTILS.getFileInputStream(args[0]);
    try {
      final DTAList dtaList = converter.mzxmlToDTA(mzxml, null);
      for(final DTAList.Entry entry : dtaList) {
        final byte[] contents = entry.getContents();
        final String name = entry.getName();
        final Matcher matcher = DTA_PATTERN.matcher(name);
        matcher.matches();
        final String base = matcher.group(1);
        final String lowNum = matcher.group(2);
        final String highNum = matcher.group(3);
        final String charge = matcher.group(4);
        final String newName = base + "." + pad(lowNum) + "." + pad(highNum) + "." + charge + ".dta";
        FILE_UTILS.writeByteArrayToFile(new File(newName), contents);
      }
    } finally {
      IO_UTILS.closeQuietly(mzxml);
    }
  }

  private static String pad(final String input) {
    return pad(input, 5, '0');
  }

  private static String pad(final String iInput, final int length, final char pad) {
    String input = iInput;
    while(input.length() < length) {
      input = pad + input;
    }
    return input;
  }

}
