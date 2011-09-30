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

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * Tests certain aspects of ITraqScanSummary class. This class in too closely coupled to {@code ITraqLabels.get4PlexLabels()} due
 * to how the code involved. If time is available in the future this coupling should be factored out.

 * @author John Chilton
 *
 */
public class ITraqScanSummaryTest {

  /**
   * Tests constructor which takes in already computed data.
   */
  @Test(groups = "unit")
  public void testProps() {
    final List<ITraqLabel> labels = ITraqLabels.get4PlexLabels();    
    final ITraqScanSummary iTraqScanSummary = ITraqScanSummary.fromIntensities(1, 2, (short) 3, labels, Lists.newArrayList(1.0, 2.0, 3.0, 4.0));
    assert iTraqScanSummary.getNumber() == 1;
    assert iTraqScanSummary.getAlt() == 2;
    assert iTraqScanSummary.getCharge() == 3;
    assert iTraqScanSummary.getIntensity(labels.get(0)) == 1.0;
    assert iTraqScanSummary.getIntensity(labels.get(1)) == 2.0;
    assert iTraqScanSummary.getIntensity(labels.get(2)) == 3.0;
    assert iTraqScanSummary.getIntensity(labels.get(3)) == 4.0;
  }

  /**
   * Tests constructor which takes in raw peaks.
   */
  @Test(groups = "unit")
  public void peakCounting() {
    final List<ITraqLabel> labels = ITraqLabels.get4PlexLabels();
    final double[] peaks = new double[] {114.0, 42.0, 114.1, 2.0, 115.0, 3.0, 0.0, 1.0, 113.8, 4.0}; // Expected answer is 42 + 2 + 4 = 48
    for(int i = 0; i < 4; i++) {
      final ITraqScanSummary iTraqScanSummary = ITraqScanSummary.fromPeaks(1, 2, (short) 3, labels, peaks);
      assert iTraqScanSummary.getIntensity(labels.get(i)) == 48.0d;
      for(int j = 0; j + 1 < peaks.length; j = j + 2) {
        peaks[j] = peaks[j] + 1.0;
      }
    }
  }

}
