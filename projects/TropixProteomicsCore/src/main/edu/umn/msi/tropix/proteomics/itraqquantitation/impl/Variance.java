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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.util.MathUtils;

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqLabels.ITraqRatio;

class Variance {

  /*
   * Finds the index of {@code value} in {@code vector} while replacing that value in the matrix with {@code Double.NaN} so that the index won't be returned again in cases of duplicates.
   */
  private static int indexOf(final double value, final double[] vector) {
    int index = -1;
    for(int i = 0; i < vector.length; i++) {
      if(vector[i] == value) {
        vector[i] = Double.NaN;
        index = i;
        break;
      }
    }
    return index;
  }

  static double[][] createVarianceMatrix(final List<ITraqLabel> labels, final Map<ITraqLabel, double[]> intensities, final double[] ds, final int binSize) {
    final int n = intensities.values().iterator().next().length;
    final double[] reference = new double[n];
    for(int i = 0; i < n; i++) {
      double intensitiesProduct = 1.0d;
      for(final ITraqLabel label : labels) {
        final double intensity = intensities.get(label)[i];
        intensitiesProduct *= intensity;
      }
      reference[i] = MathUtils.log(2.0, intensitiesProduct);
    }

    // final double[] sortedReference = Arrays.copyOf(reference, reference.length);
    final double[] sortedReference = new double[reference.length];
    for(int i = 0; i < reference.length; i++) {
      sortedReference[i] = reference[i];
    }

    Arrays.sort(sortedReference);

    final List<ITraqRatio> ratios = ITraqLabels.buildRatios(labels);
    final int numRatios = ratios.size();
    final int numRows = numRatios * n;

    // Precompute ratios
    final double[] actualRatios = new double[numRatios];
    final double[][] numerators = new double[numRatios][];
    final double[][] denominators = new double[numRatios][];
    for(int ratioIndex = 0; ratioIndex < numRatios; ratioIndex++) {
      final ITraqRatio ratio = ratios.get(ratioIndex);
      final int numeratorIndex = labels.indexOf(ratio.getNumerator());
      final int denominatorIndex = labels.indexOf(ratio.getDenominator());

      numerators[ratioIndex] = intensities.get(ratio.getNumerator());
      denominators[ratioIndex] = intensities.get(ratio.getDenominator());
      actualRatios[ratioIndex] = MathUtils.log(2.0, ds[numeratorIndex] / ds[denominatorIndex]);
    }

    final double[][] samplePoints = new double[numRows][2];
    for(int sortedReferenceIndex = 0; sortedReferenceIndex < n; sortedReferenceIndex++) {
      final double referenceValue = sortedReference[sortedReferenceIndex];
      final int referenceIndex = indexOf(referenceValue, reference);
      final int rowOffset = sortedReferenceIndex * numRatios;
      for(int ratioIndex = 0; ratioIndex < numRatios; ratioIndex++) {
        final double actualRatio = actualRatios[ratioIndex];
        final double estimatedRatio = MathUtils.log(2.0, numerators[ratioIndex][referenceIndex] / denominators[ratioIndex][referenceIndex]);
        final double diff = (estimatedRatio - actualRatio);
        samplePoints[rowOffset + ratioIndex][0] = referenceValue;
        samplePoints[rowOffset + ratioIndex][1] = diff * diff;
      }
    }

    final int numBins = numRows / binSize;
    final double[][] matrixXV = new double[numBins][2];

    double maxV = Double.MIN_VALUE;
    double sumX = 0.0, sumV = 0.0;
    int curBin = 0;
    for(int i = 0; i < numRows; i++) {
      sumX += samplePoints[i][0];
      sumV += samplePoints[i][1];
      if((i + 1) % binSize == 0) {
        final double x = sumX / binSize;
        final double v = sumV / binSize;
        final double binWeight = 1 / v;
        if(!Double.isInfinite(binWeight)) {
          maxV = Math.max(binWeight, maxV);
        }

        matrixXV[curBin][0] = x;
        matrixXV[curBin][1] = binWeight;
        curBin++;

        sumX = 0.0;
        sumV = 0.0;
      }
    }

    for(int i = 0; i < numBins; i++) {
      if(Double.isInfinite(matrixXV[i][1])) {
        matrixXV[i][1] = maxV;
      }
    }

    for(int i = 0; i < numBins - 1; i++) {
      matrixXV[i][0] = (matrixXV[i][0] + matrixXV[i + 1][0]) / 2.0;
    }

    return matrixXV;
  }

}
