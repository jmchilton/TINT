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

public class MathAsserts {

  public static void assertWithin(final double x, final double y, final double delta) {
    assert Math.abs(x - y) < delta : "Expected " + x + " == " + y + " within " + delta + " [Actual difference = " + Math.abs(x - y) + "]";
  }

  public static void assertWithin(final double[] x, final double[] y, final double delta) {
    assert x.length == y.length : " First array has length " + x.length + " second has length " + y.length;
    for(int i = 0; i < x.length; i++) {
      assertWithin(x[i], y[i], delta);
    }
  }

}
