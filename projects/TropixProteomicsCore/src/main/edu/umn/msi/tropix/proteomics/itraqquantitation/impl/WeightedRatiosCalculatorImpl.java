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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;

class WeightedRatiosCalculatorImpl implements WeightedRatiosCalculator {

  public Ratios computeRatios(final ITraqLabel numLabel, final ITraqLabel denLabel, final ReportSummary reportSummary,
      final Function<Double, Double> weightFunction, final boolean normalized) {
    final double[] ratios = new double[reportSummary.getNumGroups()], pValues = new double[reportSummary.getNumGroups()];
    int groupNum = 0;
    for(final String groupLabel : reportSummary.getGroups()) {
      final GroupSummary proteinSummary = reportSummary.getGroupSummary(groupLabel);
      final double[] iRatio = new double[proteinSummary.getNumEntries()];
      final double[] num = proteinSummary.getIntensities(numLabel);
      final double[] den = proteinSummary.getIntensities(denLabel);

      double[] weights = proteinSummary.getLogIntensitiesProducts();
      if(weightFunction != null) {
        final double[] modifiedWeights = new double[weights.length];
        for(int i = 0; i < weights.length; i++) {
          modifiedWeights[i] = weightFunction.apply(weights[i]);
        }
        weights = modifiedWeights;
      }

      for(int i = 0; i < iRatio.length; i++) {
        iRatio[i] = Math.log(num[i] / den[i]);
      }

      // final String logRatioStr = Iterables.toString(Doubles.asList(iRatio));
      // System.out.println(logRatioStr);

      if(normalized) {
        final double median = RUtils.median(iRatio);
        for(int i = 0; i < iRatio.length; i++) {
          iRatio[i] = iRatio[i] / median;
        }
      }

      final double iRatioW1 = RUtils.weightedMean(iRatio, weights);
      final double iRatioW = Math.exp(iRatioW1);
      double pValue = 2.0;
      if(iRatio.length > 2) {
        pValue = RMethods.getWeightedPValue(iRatio, weights);
      }

      if(pValue < 0) {
        // This hasn't been a problem since upgrading commons-math
        final String ratioStr = Iterables.toString(Doubles.asList(iRatio));
        final String weightsStr = Iterables.toString(Doubles.asList(weights));
        System.out.println(String.format("Negative p-value found for ratios %s and weights %s.", ratioStr, weightsStr));
      }

      ratios[groupNum] = iRatioW;
      pValues[groupNum] = pValue;

      groupNum++;
    }

    return new Ratios(ratios, pValues);
  }

  public Ratios computeRatios(ITraqLabel numLabel, ITraqLabel denLabel, ReportSummary summary, Function<Double, Double> weightFunction) {
    return computeRatios(numLabel, denLabel, summary, weightFunction, false);
  }
}
