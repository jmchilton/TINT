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

import java.util.List;
import java.util.Map;

import org.apache.commons.math.util.MathUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class GroupSummary {
  private final List<ITraqMatch> iTraqMatchs;
  private final int numEntries;
  private final double[] weights;
  private final ImmutableMap<ITraqLabel, double[]> labelIntensities;

  public GroupSummary(final Iterable<ITraqMatch> inputDataEntries, final Iterable<ITraqLabel> labels) {
    this.iTraqMatchs = Lists.newArrayList(inputDataEntries);
    this.numEntries = iTraqMatchs.size();

    final Map<ITraqLabel, double[]> labelIntensities = Maps.newHashMap();
    for(ITraqLabel label : labels) {
      labelIntensities.put(label, new double[numEntries]);
    }

    weights = new double[numEntries];
    for(int i = 0; i < numEntries; i++) {
      final ITraqMatch entry = iTraqMatchs.get(i);
      final ITraqScanSummary scan = entry.getScan();
      double intensitiesProduct = 1.0;
      for(ITraqLabel label : labels) {
        final double intensity = scan.getIntensity(label);
        labelIntensities.get(label)[i] = scan.getIntensity(label);
        intensitiesProduct *= intensity;
      }
      weights[i] = MathUtils.log(2.0, intensitiesProduct);
    }

    this.labelIntensities = ImmutableMap.copyOf(labelIntensities);
  }

  public List<ITraqMatch> getDataEntries() {
    return iTraqMatchs;
  }

  public int getNumEntries() {
    return numEntries;
  }

  public double[] getLogIntensitiesProducts() {
    return weights;
  }

  public double[] getIntensities(final ITraqLabel label) {
    return labelIntensities.get(label);
  }

}
