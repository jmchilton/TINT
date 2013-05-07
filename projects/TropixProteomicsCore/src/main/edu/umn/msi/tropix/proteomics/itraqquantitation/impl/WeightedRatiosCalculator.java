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

interface WeightedRatiosCalculator {
  static class Ratios {
    private final double[] ratios, pValues;

    Ratios(final double[] ratios, final double[] pValues) {
      this.ratios = ratios;
      this.pValues = pValues;
    }

    double[] getRatios() {
      return ratios;
    }

    double[] getPValues() {
      return pValues;
    }

  }

  Ratios computeRatios(final ITraqLabel numLabel, final ITraqLabel denLabel, final ReportSummary reportSummary,
      final Function<Double, Double> weightFunction);

  Ratios computeRatios(ITraqLabel numLabel, ITraqLabel denLabel, ReportSummary summary, Function<Double, Double> weightFunction, boolean normalized);

}
