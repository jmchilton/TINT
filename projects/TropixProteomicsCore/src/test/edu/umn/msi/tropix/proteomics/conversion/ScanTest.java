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

package edu.umn.msi.tropix.proteomics.conversion;

import org.testng.annotations.Test;

public class ScanTest {

  @Test(groups = "unit")
  public void testClone() {
    Scan scan = new Scan(1, 0, 100, new double[] {1.0, 1.0});
    scan.setParentFileName("moo.123.123.3.dta");
    assert scan.getPrecursorCharge() == 3;

    assert scan.clone().getPrecursorCharge() == 3;
  }

  @Test(groups = "unit")
  public void defaults() {
    final Scan scan = new Scan(1, 0, 1, new double[] {1.0, 1.0});

    // Default properties...
    assert scan.getPrecursorMz() == 0.0f;
    assert scan.getPrecursorCharge() == 0;

    assert !scan.isPrecursorChargeSet();
    assert !scan.isPrecursorIntensitySet();
    assert !scan.isPrecursorMzSet();

    scan.setPrecursorMz(1.0f);
    assert scan.getPrecursorMz() == 1.0f;
    scan.setPrecursorCharge((short) 1);
    assert scan.getPrecursorCharge() == 1;
    scan.setPrecursorIntensity(2.5f);
    assert scan.getPrecursorIntensity() == 2.5f;

    assert scan.isPrecursorChargeSet();
    assert scan.isPrecursorMzSet();
    assert scan.isPrecursorIntensitySet();

  }

  @Test(groups = "unit")
  public void alt() {
    Scan scan = new Scan(1, 0, 100, new double[] {1.0, 1.0});
    // When no alt has been specified, the scan number should be used
    assert scan.getAlt() == 100;

    // When an alt has been specified, that number should be returned
    scan.setAlt(97);
    assert scan.getAlt() == 97;

    scan = new Scan(1, 0, 125, new double[] {1.0, 1.0});
    scan.setParentFileName("C:\\moo\\cow\\a_name_for_the_file.124.125.3.dta");
    assert scan.getAlt() == 124;
  }

  @Test(groups = "unit")
  public void charge() {
    Scan scan = new Scan(1, 0, 1, new double[] {1.0, 1.0});
    assert scan.getPrecursorCharge() == 0;

    scan.setPrecursorCharge((short) 9);
    assert scan.getPrecursorCharge() == 9;

    scan = new Scan(1, 0, 1, new double[] {1.0, 1.0});
    scan.setParentFileName("/home/john/file.123.123.5.dta");
    assert scan.getPrecursorCharge() == 5;
  }

  @Test(groups = "unit")
  public void parentNames() {
    Scan scan = new Scan(1, 0, 1, new double[] {1.0, 1.0});
    scan.setParentFileName("C:\\moo\\cow\\mrr.123.125.3.dta");
    assert scan.getParentFileName().equals("mrr.123.125.3.dta");
    assert scan.getParentName().equals("mrr") : scan.getParentName();

    scan = new Scan(1, 0, 1, new double[] {1.0, 1.0});
    scan.setParentFileName("/home/john/mrr.RAW");
    assert scan.getParentFileName().equals("mrr.RAW");
    assert scan.getParentName().equals("mrr") : scan.getParentName();
  }

}
