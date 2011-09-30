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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import org.apache.commons.math.util.MathUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAOptions;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.test.VerifyUtils;
import edu.umn.msi.tropix.proteomics.utils.DTAUtils;

public class MzXMLToDTAConverterStreamingImplTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void structureDefaultOptions() throws Exception {
    testStructure(null);
  }

  @Test(groups = "unit")
  public void structure() throws Exception {
    final MzXMLToDTAOptions options = new MzXMLToDTAOptions();
    testStructure(options);
  }

  @Test(groups = "unit")
  public void readw() {
    final MzXMLToDTAConverterStreamingImpl converter = new MzXMLToDTAConverterStreamingImpl();
    DTAList dtaList;
    InputStream mzxmlStream;
    mzxmlStream =  ProteomicsTests.getResourceAsStream("readw.mzXML");
    try {
      dtaList = converter.mzxmlToDTA(mzxmlStream, null);
      double[] contents78charge2 = null, contents78charge3 = null;
      for(DTAList.Entry entry : dtaList) {
        if(entry.getName().matches(".*0*78\\.0*78\\.2\\.dta$")) {
          assert contents78charge2 == null;
          contents78charge2 = DTAUtils.readDtaDoublePairs(entry.getContents());
        }
        if(entry.getName().matches(".*0*78\\.0*78\\.3\\.dta$")) {
          assert contents78charge3 == null;
          contents78charge3 = DTAUtils.readDtaDoublePairs(entry.getContents());
        }
        if(entry.getName().matches(".*0*1993\\.0*1993\\.1\\.dta$")) {
          final double[] values = DTAUtils.readDtaDoublePairs(entry.getContents());
          final Scanner scanner = new Scanner(new ByteArrayInputStream(entry.getContents()));
          assert MathUtils.equals(scanner.nextDouble(), 1515.390000); 
          assert scanner.nextInt() == 1; 
          assert Math.abs(662.267334 - values[0]) < .01 : values[0];
          assert Math.abs(4.004042 - values[values.length - 1]) < .01;
        }
      }
    assert contents78charge2 != null;
    assert contents78charge3 != null;
    assert contents78charge2.length == contents78charge3.length;
    for(int i = 2; i < contents78charge2.length; i++) {
      assert MathUtils.equals(contents78charge2[i], contents78charge3[i]);
    }
    } finally {
      IO_UTILS.closeQuietly(mzxmlStream);
    }
  }

  private void testStructure(final MzXMLToDTAOptions options) throws Exception {
    final MzXMLToDTAConverterStreamingImpl converter = new MzXMLToDTAConverterStreamingImpl();
    DTAList dtaList;
    InputStream mzxmlStream;
    mzxmlStream =  ProteomicsTests.getResourceAsStream("validMzXML.mzxml");
    try {
      dtaList = converter.mzxmlToDTA(mzxmlStream, options);
      VerifyUtils.verifyDTAList(dtaList);
    } finally {
      IO_UTILS.closeQuietly(mzxmlStream);
    }

    mzxmlStream = ProteomicsTests.getResourceAsStream("validMzXML.mzxml");
    try {
      dtaList = converter.mzxmlToDTA(mzxmlStream, options);
      final HashSet<String> originalNames = new HashSet<String>();
      originalNames.addAll(Arrays.asList(new String[] {"mrr.103.106.1.dta", "mrr.1105.1106.2.dta", "mrr.1105.1106.3.dta", "mrr.2025.2026.2.dta", "mrr.2025.2026.3.dta", "mrr.3009.3011.1.dta"}));

      final HashSet<String> mzxmlNames = new HashSet<String>();
      for(final DTAList.Entry entry : dtaList) {
        mzxmlNames.add(entry.getName());
      }
      assert mzxmlNames.equals(originalNames);
    } finally {
      IO_UTILS.closeQuietly(mzxmlStream);
    }

  }

}
