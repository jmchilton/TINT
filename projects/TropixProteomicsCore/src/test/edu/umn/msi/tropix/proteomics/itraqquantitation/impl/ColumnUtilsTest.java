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

import java.io.StringWriter;
import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class ColumnUtilsTest {

  @Immutable
  private static class TestColumn implements Column {
    private final int mult;

    TestColumn(final int mult) {
      this.mult = mult;
    }

    public String getHeader() {
      return "headerx" + mult;
    }

    public int getLength() {
      return 2;
    }

    public String getValue(final int i) {
      return "" + i * mult;
    }
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void emptyColumns() {
    ColumnUtils.writeColumns(new StringWriter(), Lists.<Column>newArrayList(), "\t", "\n");
  }

  @Test(groups = "unit")
  public void writeColumns() {
    final Column columnX1 = new TestColumn(1);
    final Column columnX2 = new TestColumn(2);

    final StringWriter writer = new StringWriter();
    ColumnUtils.writeColumns(writer, Arrays.asList(columnX1, columnX2), "\t", "\n");

    final StringBuilder builder = new StringBuilder();
    builder.append("headerx1\theaderx2\n0\t0\n1\t2");
    assert writer.toString().equals(builder.toString()) : writer.toString();
  }
}
