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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.rank.Median;

class RUtils {
  private static NormalDistributionImpl standardNormalDistribution = new NormalDistributionImpl();
  private static Median median = new Median();
  
  public static double pnorm(final double x) {
    try {
      return standardNormalDistribution.cumulativeProbability(x);
    } catch(final MathException e) {
      throw new RuntimeException(e);
    }
  }

  public static double median(final double[] x) {
    return median.evaluate(x);
  }

  public static double sd(final double[] x) {
    return Math.sqrt(StatUtils.variance(x));
  }

  /**
   * Implements wtd.mean from the Hmisc R library.
   * 
   * @param x
   * @param w
   * @return
   */
  public static double weightedMean(final double[] x, final double[] weights) {
    double weightedSum = 0.0, sumOfWeights = 0.0;
    for(int i = 0; i < x.length; i++) {
      weightedSum += x[i] * weights[i];
      sumOfWeights += weights[i];
    }
    return weightedSum / sumOfWeights;
  }

  /**
   * Implements wtd.var from the Hmisc R library.
   * 
   * @param x
   * @param weights
   * @return
   */
  public static double weightedVariance(final double[] x, final double[] weights) {
    final double xBar = weightedMean(x, weights);
    double weightedSumDiff = 0.0, sumOfWeights = 0.0;
    for(int i = 0; i < x.length; i++) {
      final double diff = x[i] - xBar;
      weightedSumDiff += weights[i] * diff * diff;
      sumOfWeights += weights[i];
    }
    return weightedSumDiff / (sumOfWeights - 1);
  }

}
