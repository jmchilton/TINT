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

import java.util.List;

import com.google.common.collect.Lists;

class Columns {
  private abstract static class AbstractColumn implements Column {
    private final String header;
    private final int length;

    AbstractColumn(final String header, final int length) {
      this.header = header;
      this.length = length;
    }

    public String getHeader() {
      return header;
    }

    public int getLength() {
      return length;
    }

    public abstract String getValue(int i);

  }

  public static Column asNumericColumn(final String header, final double[] values) {
    return new AbstractColumn(header, values.length) {
      public String getValue(final int i) {
        return "" + values[i];
      }
    };
  }

  public static Column asStringColumn(final String header, final Iterable<String> iterable) {
    final List<String> list = Lists.newArrayList(iterable);
    return new AbstractColumn(header, list.size()) {
      public String getValue(final int i) {
        return list.get(i);
      }
    };
  }
}
