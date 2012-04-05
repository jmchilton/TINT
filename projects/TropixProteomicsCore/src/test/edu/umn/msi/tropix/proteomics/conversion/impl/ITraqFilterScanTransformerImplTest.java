package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.util.Arrays;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class ITraqFilterScanTransformerImplTest {

  @Test(groups = "unit")
  public void testDoesntFilterOutsideItraqRange() {
    final double[] testPeaks = {130.001, 1.0, 130.002, 200.0};
    assertTransformedPeaksEqual(testPeaks, testPeaks);
  }

  @Test(groups = "unit")
  public void testFilters114() {
    final double[] testPeaks = {114.001, 1.0, 113.964, 200.0};
    final double[] expectedPeaks = {113.964, 200.0};
    assertTransformedPeaksEqual(testPeaks, expectedPeaks);
  }

  @Test(groups = "unit")
  public void testFiltersConfined() {
    final double[] testPeaks = {114.001, 1.0, 113.964, 0.5, 114.9, 1009.4};
    final double[] expectedPeaks = {114.001, 1.0, 114.9, 1009.4};
    assertTransformedPeaksEqual(testPeaks, expectedPeaks);
  }

  private void assertTransformedPeaksEqual(final double[] inputPeaks, final double[] expectedPeaks) {
    final double[] filteredPeaks = transformPeaks(inputPeaks);
    assert Arrays.equals(expectedPeaks, filteredPeaks);
  }

  public double[] transformPeaks(final double[] peaks) {
    final Scan scan = new Scan(1, 1, peaks);
    ITraqFilterScanTransformerImpl transformer = new ITraqFilterScanTransformerImpl();
    final Scan transformedScan = transformer.apply(scan);
    return transformedScan.getPeaks();
  }

}
