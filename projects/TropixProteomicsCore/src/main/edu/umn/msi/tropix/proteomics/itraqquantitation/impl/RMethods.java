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

import org.apache.commons.math.stat.StatUtils;

class RMethods {

  /**
   * From the following R code.
   * 
   * <pre>
   * getPvalue &lt;- function(x){
   *  x_bar &lt;- mean(x)
   *  x_std &lt;- sd(x)
   *  z &lt;- x_bar/x_std
   *  pvalue &lt;- pnorm(-abs(z)) * 2
   *  return(pvalue)
   * }
   * </pre>
   * 
   * @param x
   * @return
   */
  public static double getPValue(final double[] x) {
    final double xBar = StatUtils.mean(x);
    final double xStd = RUtils.sd(x);
    final double z = xBar / xStd;
    return RUtils.pnorm(-1 * Math.abs(z)) * 2;
  }

  /**
   * From the following R code.
   * 
   * <pre>
   * getWeightedPvalue &lt;- function(x, w){
   *  x_bar &lt;- wtd.mean(x, w)
   *  x_std &lt;- sqrt(wtd.var(x, w))
   *  z &lt;- x_bar/x_std
   *  pvalue &lt;- pnorm(-abs(z)) * 2
   *  return(pvalue)
   * }
   * </pre>
   * 
   * @param x
   * @param w
   * @return
   */
  public static double getWeightedPValue(final double[] x, final double[] w) {
    final double xBar = RUtils.weightedMean(x, w);
    final double xStd = Math.sqrt(RUtils.weightedVariance(x, w));
    final double z = xBar / xStd;
    return RUtils.pnorm(-1 * Math.abs(z)) * 2;
  }

}
