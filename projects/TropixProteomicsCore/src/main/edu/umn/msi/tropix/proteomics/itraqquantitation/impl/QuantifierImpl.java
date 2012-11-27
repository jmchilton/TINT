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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.testng.collections.Maps;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqLabels.ITraqRatio;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.WeightedRatiosCalculator.Ratios;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.PeptideGroup;
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

  private static int getNumPeptides(GroupSummary groupSummary) {
    final Set<String> peptides = Sets.newHashSet();
    for(final ITraqMatch iTraqMatch : groupSummary.getDataEntries()) {
      peptides.add(iTraqMatch.getPeptideSequence());
    }
    return peptides.size();
  }

  public QuantificationResults quantify(final Collection<ITraqRatio> iTraqRatios, final ReportSummary summary,
      @Nullable final Function<Double, Double> trainingFunction) {
    final QuantificationResults results = new QuantificationResults();
    final List<List<Ratio>> ratioLists = Lists.newArrayList();
    // Fill in protein information
    Map<String, Protein> addedProteins = Maps.newHashMap();
    for(final String groupLabel : summary.getGroups()) {
      final GroupSummary groupSummary = summary.getGroupSummary(groupLabel);
      final ProteinInformation proteinInformation = summary.getProteinInformationForPeptideGroup(groupLabel);
      final String proteinLabel = proteinInformation.getProteinAccession();

      final int numSpectra = groupSummary.getNumEntries();
      final int numPeptides = getNumPeptides(groupSummary);

      final Protein protein;
      if(addedProteins.containsKey(proteinLabel)) {
        protein = addedProteins.get(proteinLabel);
      } else {
        protein = new Protein();
        protein.setName(proteinLabel);
        results.getProtein().add(protein);
      }

      if(summary.getGroupType() != GroupType.PROTEIN) {
        final PeptideGroup peptideGroup = new PeptideGroup();
        peptideGroup.setGroupType(summary.getGroupType().toString());
        peptideGroup.setGroupLabel(groupLabel);
        peptideGroup.setNumPeptides(numPeptides);
        peptideGroup.setNumSpectra(numSpectra);

        protein.getPeptideGroup().add(peptideGroup);
        ratioLists.add(peptideGroup.getRatio());
      } else {
        ratioLists.add(protein.getRatio());
      }

      protein.setNumSequences(protein.getNumSequences() + numSpectra);
      protein.setNumPeptides(protein.getNumPeptides() + numPeptides);
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
          if(weightedRatios.length != ratioLists.size()) {
            throw new IllegalStateException(String.format(
                "Grouping problems - number of computed ratios (%d) does not match number of ratio lists (%d).",
                weightedRatios.length,
                ratioLists.size()));
          }
          for(final List<Ratio> ratioList : ratioLists) {
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
            ratioList.add(ratio);
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
