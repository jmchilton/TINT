package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.File;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public interface PeakListParser {

  public static class PeakListParserOptions {
    private boolean splitChargeStates = true;

    public boolean isSplitChargeStates() {
      return splitChargeStates;
    }

    public void setSplitChargeStates(boolean splitChargeStates) {
      this.splitChargeStates = splitChargeStates;
    }

  }

  public UnmodifiableIterator<Scan> parse(final File peakFile, final PeakListParserOptions peakParserOptions);

}
