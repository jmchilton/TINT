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

import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.math.util.MathUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class MgfParserTest {

  @Test(groups = "unit")
  public void testMrrConvertedByTpp() {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream("readw.mgf-shortened");
    final Iterator<Scan> scanIter = new MgfParser().parserMgf(inputStream);
    final Scan scan1 = scanIter.next();
    assert scan1.getNumber() == 78 : scan1.getNumber();
    assert scan1.getMsLevel() == 2;
    assert scan1.getPrecursorCharge() == 2;

    assert MathUtils.equals(scanIter.next().getPeaks()[0], 221.161362d);
    final int expectedNumScans = 4;
    int numScans = 2;
    while(scanIter.hasNext()) {
      scanIter.next();
      numScans++;
    }
    assert numScans == expectedNumScans : numScans;
  }
  
  /*
  @Test(groups = "unit")
  public void testLargeMrrConvertedByTpp() {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream("readw.mgf");
    final Iterator<Scan> scanIter = new MgfParser().parserMgf(inputStream);
    while(scanIter.hasNext()) {
      scanIter.next();
    }
    
  }
  
  
  @Test(groups = "unit")
  public void testOrbiConvertedByTpp() {
    
  }
  */
  
}
