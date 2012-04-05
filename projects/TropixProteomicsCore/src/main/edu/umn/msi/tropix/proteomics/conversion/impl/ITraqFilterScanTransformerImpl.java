package edu.umn.msi.tropix.proteomics.conversion.impl;

import com.google.common.base.Function;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class ITraqFilterScanTransformerImpl implements Function<Scan, Scan> {

  public Scan apply(final Scan input) {
    final double[] unfilteredPeaks = input.getPeaks();
    final boolean[] filter = new boolean[unfilteredPeaks.length];

    for(int mz = 112; mz <= 121; mz++) {
      final double lowerMz = mz - .5, upperMz = mz + .5;
      double windowMaxIntensity = 0.0d;
      int peaksInWindow = 0;
      for(int i = 0; i < unfilteredPeaks.length; i += 2) {
        final double peakMz = unfilteredPeaks[i];
        final double peakIntensity = unfilteredPeaks[i + 1];
        if(peakMz >= lowerMz & peakMz < upperMz) {
          peaksInWindow++;
          if(peakIntensity > windowMaxIntensity) {
            windowMaxIntensity = peakIntensity;
          }
        }
      }
      if(peaksInWindow > 1) {
        // Time to filter
        for(int i = 0; i < unfilteredPeaks.length; i += 2) {
          final double peakMz = unfilteredPeaks[i];
          final double peakIntensity = unfilteredPeaks[i + 1];
          if(peakMz >= lowerMz & peakMz < upperMz) {
            if(peakIntensity < windowMaxIntensity) {
              filter[i] = true;
              filter[i + 1] = true;
            }
          }
        }
      }
    }
    final Scan filteredScan = input.clone();
    filteredScan.setPeaks(copyFiltered(unfilteredPeaks, filter));
    return filteredScan;
  }

  private static final double[] copyFiltered(final double[] input, final boolean[] filtered) {
    int numToFilter = 0;
    for(boolean filter : filtered) {
      if(filter) {
        numToFilter++;
      }
    }
    final double[] output = new double[input.length - numToFilter];
    int indexInput = 0, indexOutput = 0;
    while(indexInput < input.length) {
      final boolean filter = filtered[indexInput];
      if(!filter) {
        output[indexOutput++] = input[indexInput];
      }
      indexInput++;
    }
    return output;
  }

}
