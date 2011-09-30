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

package edu.umn.msi.tropix.proteomics.utils;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.Directory;
import edu.umn.msi.tropix.proteomics.DTAList;

public class DTAUtils {

  public static List<String> writeDTAFiles(final Directory directory, final DTAList dtaList) {
    final List<String> resources = Lists.newArrayList();
    for(final DTAList.Entry entry : dtaList) {
      final String name = entry.getName();
      final byte[] bytes = entry.getContents();
      directory.getOutputContext(name).put(bytes);
      resources.add(directory.getAbsolutePath() + directory.getSep() + name);
    }
    return resources;
  }

  /*
  public static void dtaToMS2(final DTAList dtaList, final OutputStream outputStream) throws IOException {
    for(final DTAList.Entry entry : dtaList) {
      final String name = entry.getName();
      final byte[] bytes = entry.getContents();

      final Matcher matcher = DTA_NAME_PATTERN.matcher(name);
      if(!matcher.matches()) {
        throw new IllegalArgumentException("Invalid DTA File name found " + name);
      }
      outputStream.write((":" + matcher.group(2) + "." + matcher.group(3) + "." + matcher.group(4) + System.getProperty("line.separator")).getBytes());
      IOUtils.write(bytes, outputStream);
      outputStream.write(System.getProperty("line.separator").getBytes());
    }
    outputStream.flush();
  }
  */

  private static int getNumPairs(final byte[] dtaContents) {
    final Scanner scanner = new Scanner(new ByteArrayInputStream(dtaContents));
    // Save memory and scan twice, saves constantly having to reallocate array or storing
    // linked list of Double objects. Besides dtaContents is already loaded into memory.
    int count = 0;
    scanner.nextDouble();
    scanner.nextDouble();
    while(scanner.hasNextDouble()) {
      scanner.nextDouble();
      scanner.nextDouble();
      count++;
    }
    return count;
  }

  public static double[] readDtaDoublePairs(final byte[] dtaContents) {
    final int count = getNumPairs(dtaContents);
    final Scanner scanner = new Scanner(new ByteArrayInputStream(dtaContents));
    scanner.nextDouble();
    scanner.nextDouble();
    final double[] doubles = new double[count * 2];
    for(int i = 0; i < count; i++) {
      doubles[2 * i] = scanner.nextDouble();
      doubles[2 * i + 1] = scanner.nextDouble();
    }
    return doubles;
  }

  public static float[] readDtaFloatPairs(final byte[] dtaContents) {
    final int count = getNumPairs(dtaContents);
    final Scanner scanner = new Scanner(new ByteArrayInputStream(dtaContents));
    scanner.nextFloat();
    scanner.nextFloat();
    final float[] floats = new float[count * 2];
    for(int i = 0; i < count; i++) {
      floats[2 * i] = scanner.nextFloat();
      floats[2 * i + 1] = scanner.nextFloat();
    }
    return floats;
  }

  public static final Pattern DTA_NAME_PATTERN = Pattern.compile("(.*)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.[dD][tT][aA]$");

}
