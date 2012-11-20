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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqLabels.ITraqRatio;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.WeightedRatiosCalculator.Ratios;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.Protein;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.QuantificationResults;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.Ratio;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.RatioLabel;

class QuantifierImpl implements Quantifier {
  private WeightedRatiosCalculator weightedRatiosCalculator = new WeightedRatiosCalculatorImpl();

  @VisibleForTesting
  void setWeightedRatiosCalculator(final WeightedRatiosCalculator weightedRatiosCalculator) {
    this.weightedRatiosCalculator = weightedRatiosCalculator;
  }

  public QuantificationResults quantify(final Collection<ITraqRatio> iTraqRatios, final ReportSummary summary,
      @Nullable final Function<Double, Double> trainingFunction) {
    final QuantificationResults results = new QuantificationResults();

    // Fill in protein information
    for(final String proteinName : summary.getGroups()) {
      final GroupSummary proteinSummary = summary.getGroupSummary(proteinName);
      final Protein protein = new Protein();
      protein.setName(proteinName);
      protein.setNumSequences(proteinSummary.getNumEntries());
      final Set<String> peptides = Sets.newHashSet();
      for(final ITraqMatch iTraqMatch : proteinSummary.getDataEntries()) {
        peptides.add(iTraqMatch.getPeptideSequence());
      }
      protein.setNumPeptides(peptides.size());
      results.getProtein().add(protein);
    }

    for(final ITraqRatio iTraqRatio : iTraqRatios) {
      final ITraqLabel numLabel = iTraqRatio.getNumerator();
      final ITraqLabel denLabel = iTraqRatio.getDenominator();

      final RatioLabel label = new RatioLabel();
      label.setNumeratorLabel(numLabel.getLabel());
      label.setDenominatorLabel(denLabel.getLabel());
      results.getRatioLabel().add(label);
    }

    // Fill in ratios
    final Iterator<String> methods =
        Lists.newArrayList("Untrained Weighted Average", "Trained Weighted Average").iterator();
    @SuppressWarnings("unchecked")
    final Iterator<Function<Double, Double>> weightFunctions =
        Lists.newArrayList(Functions.<Double>identity(), trainingFunction).iterator();

    while(methods.hasNext()) {
      final String methodName = methods.next();
      final Function<Double, Double> weightFunction = weightFunctions.next();
      if(weightFunction == null) {
        continue;
      }
      for(final ITraqRatio iTraqRatio : iTraqRatios) {
        for(final boolean normalized : new boolean[] {false, true}) {
          final ITraqLabel numLabel = iTraqRatio.getNumerator();
          final ITraqLabel denLabel = iTraqRatio.getDenominator();

          final Ratios ratios = weightedRatiosCalculator.computeRatios(numLabel, denLabel, summary, weightFunction, normalized);
          final double[] weightedRatios = ratios.getRatios();
          final double[] pValues = ratios.getPValues();
          int i = 0;
          for(final Protein protein : results.getProtein()) {
            final Ratio ratio = new Ratio();
            ratio.setNumeratorLabel(numLabel.getLabel());
            ratio.setDenominatorLabel(denLabel.getLabel());
            if(!normalized) {
              ratio.setMethod(methodName);
            } else {
              ratio.setMethod(methodName + " (normalized)");
            }
            ratio.setRatio(weightedRatios[i]);
            final double pValue = pValues[i];
            if(pValue <= 1.0) {
              ratio.setPValue(pValue);
            }
            protein.getRatio().add(ratio);
            i++;
          }
        }
      }
      /*
       * final Ratios ratios = weightedRatiosCalculator.computeRatioOfRatios(summary, weightFunction);
       * final double[] weightedRatios = ratios.getRatios();
       * final double[] pValues = ratios.getPValues();
       * int i = 0;
       * for(final Protein protein : results.getProtein()) {
       * final Ratio ratio = new Ratio();
       * ratio.setNumeratorLabel("(i116:i117)");
       * ratio.setDenominatorLabel("(i114:i115)");
       * ratio.setMethod(methodName);
       * ratio.setRatio(weightedRatios[i]);
       * final double pValue = pValues[i];
       * if(pValue <= 1.0) {
       * ratio.setPValue(pValue);
       * }
       * protein.getRatio().add(ratio);
       * i++;
       * }
       */
    }
    return results;
  }
}
