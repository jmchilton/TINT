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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class ColumnsTest {

  @Test(groups = "unit")
  public void getNumericColumn() {
    final Column column = Columns.asNumericColumn("test", new double[] {1, 2, 3});
    assert column.getHeader().equals("test");
    assert column.getLength() == 3;
    assert Double.parseDouble(column.getValue(0)) == 1.0;
    assert Double.parseDouble(column.getValue(2)) == 3.0;
  }

  @Test(groups = "unit")
  public void asStringColumn() {
    final Column column = Columns.asStringColumn("strTest", Lists.newArrayList("moo", "cow"));
    assert column.getHeader().equals("strTest");
    assert column.getValue(0).equals("moo");
    assert column.getValue(1).equals("cow");
    assert column.getLength() == 2;
  }
}
