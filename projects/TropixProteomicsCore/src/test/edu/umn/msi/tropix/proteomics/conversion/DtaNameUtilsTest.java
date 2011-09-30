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

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;

public class DtaNameUtilsTest {

  @Test(groups = "unit")
  public void isDtaName() {
    assert DtaNameUtils.isDtaName("moo.123.123.4.dta");
    assert DtaNameUtils.isDtaName("mrr_name.123.124.3.dta");
    assert !DtaNameUtils.isDtaName("moo.123.123.4.dat");
    assert !DtaNameUtils.isDtaName("moo.123.124.dta");
    assert !DtaNameUtils.isDtaName("moo.4.dta");
    assert DtaNameUtils.isDtaName("C:\\My Documents\\moo.123.123.4.dta");
    assert !DtaNameUtils.isDtaName("C:\\My Documents\\moo.123.123.4.dat");
    assert DtaNameUtils.isDtaName("/home/john/moo.123.123.4.dta");
  }

  @Test(groups = "unit")
  public void getDtaNameSummary() {
    final String[] dtaNames = new String[] {"mrr_name.123.124.3.dta", "C:\\mrr_name.123.124.3.dta", "/home/john/mrr_name.123.124.3.dta"};
    for(final String dtaName : dtaNames) {
      final DtaNameSummary summary = DtaNameUtils.getDtaNameSummary(dtaName);
      assert summary.getBasename().equals("mrr_name");
      assert summary.getCharge() == 3;
      assert summary.getStart() == 123;
      assert summary.getEnd() == 124;
    }
  }

}
