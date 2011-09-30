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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeight;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

class TrainingWeightFunctionImpl implements Function<Double, Double> {  
  private final double[][] trainingMatrix;
  
  /*
  TrainingWeightFunctionImpl(final List<ITraqMatch> trainingMatches, final List<ITraqLabel> labels, QuantiationTrainingParameters trainingParameters) {
    final Map<ITraqLabel, double[]> intensities = Maps.newHashMap();
    final int numMatches = trainingMatches.size();
    for(ITraqLabel label : labels) {
      intensities.put(label, new double[numMatches]);
    }
    int i = 0;
    for(ITraqMatch match : trainingMatches) {
      for(ITraqLabel label : labels) {
        intensities.get(label)[i] = match.getScan().getIntensity(label);
      }
      i++;
    }
    this.trainingMatrix = Variance.createVarianceMatrix(labels, intensities, trainingParameters.getExpectedRatios(), trainingParameters.getNumBins());  
  }
  */
  
  TrainingWeightFunctionImpl(final QuantificationWeights weights) {
    final Map<Double, Double> weightMap = Maps.newHashMapWithExpectedSize(weights.getWeight().length);
    for(QuantificationWeight weight : weights.getWeight()) {
      weightMap.put(weight.getIntensity(), weight.getWeight());
    }
    final List<Double> sortedIntensities = Lists.newArrayList(weightMap.keySet());
    Collections.sort(sortedIntensities);
    this.trainingMatrix = new double[sortedIntensities.size()][2];
    int i = 0;
    for(final Double intensity : sortedIntensities) {
      trainingMatrix[i][0] = intensity;
      trainingMatrix[i][1] = weightMap.get(intensity);
      i++;
    }
  }

  public Double apply(final Double weightIndex) {
    final int numBins = trainingMatrix.length;
    double weight = trainingMatrix[numBins-1][1];
    for(int i = numBins - 1; i >= 0; i--) {
      if(weightIndex <= trainingMatrix[i][0]) {
        weight = trainingMatrix[i][1];
      } else {
        break;
      }
    }
    return weight;
  }
  
}
