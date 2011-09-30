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

public class RTest {
  /*
   * X1 <- c(1.0,2.0,3.0,4.0,5.0)
   */
  private static final double[] X1 = new double[] {1.0, 2.0, 3.0, 4.0, 5.0};

  /*
   * W1 <- c(0.0, 1.4, 2.5, 1.1, 10.4)
   */
  private static final double[] W1 = new double[] {0.0, 1.4, 2.5, 1.1, 10.4};

  private static final double EPSILON = 0.00000000001;

  private void almostEqual(final double x1, final double x2) {
    assert Math.abs(x1 - x2) < EPSILON : "Expected " + x1 + " found " + x2;
  }

  @Test(groups = "unit")
  public void median() {
    /**
     * > median(X1) [1] 3
     */
    almostEqual(RUtils.median(X1), 3.0);
    /**
     * > median(W1) [1] 1.4
     */
    almostEqual(RUtils.median(W1), 1.4);
    /**
     * > median(c(1.0,1.0)) [1] 1
     */
    almostEqual(RUtils.median(new double[] {1.0, 1.0}), 1.0);
    /**
     * median(c(1.0,2.0)) [1] 1.5
     */
    almostEqual(RUtils.median(new double[] {1.0, 2.0}), 1.5);
  }

  @Test(groups = "unit")
  public void sd() {
    /*
     * > sd(X1) [1] 1.58113883008419
     */
    almostEqual(RUtils.sd(X1), 1.58113883008419);
  }

  @Test(groups = "unit")
  public void pnorm() {
    /*
     * > pnorm(-1.4) [1] 0.0807566592337711 > pnorm(1.8) [1] 0.964069680887074
     */
    almostEqual(RUtils.pnorm(-1.4), 0.0807566592337711);
    almostEqual(RUtils.pnorm(1.8), 0.964069680887074);
  }

  @Test(groups = "unit")
  public void pValue() {
    /*
     * > getPvalue(X1) [1] 0.05777957112359725
     */
    almostEqual(RMethods.getPValue(X1), 0.05777957112359725);
  }

  @Test(groups = "unit")
  public void weightedSum() {
    /*
     * > wtd.mean(X1, W1) [1] 4.33116883116883
     */
    almostEqual(RUtils.weightedMean(X1, W1), 4.33116883116883);
  }

  @Test(groups = "unit")
  public void weightedVariable() {
    /*
     * > wtd.var(X1, W1) [1] 1.167433261183261
     */
    almostEqual(RUtils.weightedVariance(X1, W1), 1.167433261183261);
  }

  @Test(groups = "unit")
  public void weightedPValue() {
    /*
     * > getWeightedPvalue(X1, W1) [1] 6.10884633954227e-05
     */
    almostEqual(RMethods.getWeightedPValue(X1, W1), 6.10884633954227e-05);
  }

}
