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

class WeightedRatiosCalculatorImpl implements WeightedRatiosCalculator {

  public Ratios computeRatioOfRatios(final ReportSummary reportSummary, final Function<Double, Double> weightFunction) {
    final double[] ratios = new double[reportSummary.getNumProteins()], pValues = new double[reportSummary.getNumProteins()];
    int proteinNum = 0;
    for(final String protein : reportSummary.getProteins()) {
      final ProteinSummary proteinSummary = reportSummary.getProteinSummary(protein);
      final double[] iRatio = new double[proteinSummary.getNumEntries()];
      final double[] i114 = proteinSummary.getIntensities(ITraqLabels.get4PlexLabels().get(0)); // 114
      final double[] i115 = proteinSummary.getIntensities(ITraqLabels.get4PlexLabels().get(1)); // 115

      final double[] i116 = proteinSummary.getIntensities(ITraqLabels.get4PlexLabels().get(2)); // 116
      final double[] i117 = proteinSummary.getIntensities(ITraqLabels.get4PlexLabels().get(3)); // 117

      final double[] num = new double[i114.length], den = new double[i114.length];
      for(int i = 0; i < num.length; i++) {
        num[i] = i116[i] / i117[i];
        den[i] = i114[i] / i115[i];
      }

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

      final double iRatioW1 = RUtils.weightedMean(iRatio, weights);
      final double iRatioW = Math.exp(iRatioW1);
      double pValue = 2.0;
      if(iRatio.length > 2) {
        pValue = RMethods.getWeightedPValue(iRatio, weights);
      }

      ratios[proteinNum] = iRatioW;
      pValues[proteinNum] = pValue;

      proteinNum++;
    }

    return new Ratios(ratios, pValues);
  }

  public Ratios computeRatios(final ITraqLabel numLabel, final ITraqLabel denLabel, final ReportSummary reportSummary,
      final Function<Double, Double> weightFunction) {
    final double[] ratios = new double[reportSummary.getNumProteins()], pValues = new double[reportSummary.getNumProteins()];
    int proteinNum = 0;
    for(final String protein : reportSummary.getProteins()) {
      final ProteinSummary proteinSummary = reportSummary.getProteinSummary(protein);
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

      final double iRatioW1 = RUtils.weightedMean(iRatio, weights);
      final double iRatioW = Math.exp(iRatioW1);
      double pValue = 2.0;
      if(iRatio.length > 2) {
        pValue = RMethods.getWeightedPValue(iRatio, weights);
      }

      ratios[proteinNum] = iRatioW;
      pValues[proteinNum] = pValue;

      proteinNum++;
    }

    return new Ratios(ratios, pValues);
  }

}
