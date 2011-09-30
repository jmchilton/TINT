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

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Immutable
final class ITraqScanSummary {
  private final int number;
  private final int alt;
  private final short charge;
  private final ImmutableMap<ITraqLabel, Double> labelIntensitiesDerived;
  private final ImmutableList<ITraqLabel> labels;
  
  public static ITraqScanSummary fromPeaks(final int number, final int alt, final short charge, final Iterable<ITraqLabel> inputLabels, final double[] peaks) {
    return new ITraqScanSummary(number, alt, charge, peaks, inputLabels);
  }

  public static ITraqScanSummary fromIntensities(final int number, final int alt, final short charge, final Iterable<ITraqLabel> inputLabels, final Iterable<Double> intensities) {
    return new ITraqScanSummary(number, alt, charge, inputLabels, intensities);
  }
  
  private ITraqScanSummary(final int number, final int alt, final short charge, final Iterable<ITraqLabel> inputLabels, final Iterable<Double> intensities) {
    this.number = number;
    this.alt = alt;
    this.charge = charge;
    this.labels = ImmutableList.copyOf(inputLabels);
    final Map<ITraqLabel, Double> labelIntensitiesDerived = Maps.newHashMap();
    final Iterator<Double> intensityIter = intensities.iterator();
    for(ITraqLabel label : labels) {
      final double intensity = intensityIter.next();
      labelIntensitiesDerived.put(label, intensity);
    }
    this.labelIntensitiesDerived = ImmutableMap.copyOf(labelIntensitiesDerived);
  }

  private ITraqScanSummary(final int number, final int alt, final short charge, final double[] peaks, final Iterable<ITraqLabel> inputLabels) {
    this.number = number;
    this.alt = alt;
    this.charge = charge;
    final Map<ITraqLabel, Double> labelIntensitiesDerived = Maps.newHashMap();
    this.labels = ImmutableList.copyOf(inputLabels);
    for(ITraqLabel label : labels) {
      labelIntensitiesDerived.put(label, 0.0);
    }
    for(int i = 0; (i + 1) < peaks.length; i = i + 2) {
      final double mtoz = peaks[i];
      final double intensity = peaks[i + 1];
      for(ITraqLabel label : labelIntensitiesDerived.keySet()) {
        if(label.fitsPeak(mtoz)) {
          final double previousTotal = labelIntensitiesDerived.get(label);
          labelIntensitiesDerived.put(label, previousTotal + intensity);
        }          
      }
    }
    this.labelIntensitiesDerived = ImmutableMap.copyOf(labelIntensitiesDerived);
  }

  public boolean intensitiesGreaterThan(final double x) {
    boolean allGreater = true;
    for(Double intensityDerived : labelIntensitiesDerived.values()) {
      allGreater = allGreater && intensityDerived > x;
    }
    return allGreater;
  }

  public int getNumber() {
    return number;
  }

  public int getAlt() {
    return alt;
  }

  public short getCharge() {
    return charge;
  }

  public double getIntensity(@Nonnull final ITraqLabel label) {
    return labelIntensitiesDerived.get(label);
  }
  
  public ImmutableList<ITraqLabel> getLabels() {
    return labels;
  }
}
