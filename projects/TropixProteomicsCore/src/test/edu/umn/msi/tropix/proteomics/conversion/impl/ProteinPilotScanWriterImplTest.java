package edu.umn.msi.tropix.proteomics.conversion.impl;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions.MgfStyle;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class ProteinPilotScanWriterImplTest extends BaseMgfScanWriterImplTest {

  public ProteinPilotScanWriterImplTest() {
    this.setMgfStyle(MgfStyle.PROTEIN_PILOT);
  }

  @Test(groups = "unit")
  public void testConversion() {
    final Scan scan = new Scan(2, 0, 45, new double[] {1.0, 2.0});
    final long rtMilli = (long) (2.11d * 60000);
    scan.setRt(rtMilli);
    scan.setParentFileName("foo.raw");
    scan.setPrecursorIntensity(1.4f);
    final String mgfScanContents = convertScan(scan);
    String expectedTitle = String.format(
        "TITLE=File: foo.raw, Sample: Sample001 (sample number 1), Elution: 2.11 min, Period: 1, Cycle(s): 45 (Experiment 1)");
    assert mgfScanContents.contains(expectedTitle) : String.format("Expected %s obtained %s", expectedTitle, mgfScanContents);
    final String expectedRtHeader = String.format("RTINSECONDS=%f", rtMilli / 1000.0);
    assert mgfScanContents.contains(expectedRtHeader) : String.format("contents %s does not contain header %s", mgfScanContents, expectedRtHeader);
  }

  @Test(groups = "unit")
  public void testConversionWithoutRT() {
    final Scan scan = new Scan(2, 0, 45, new double[] {1.0, 2.0});
    scan.setParentFileName("foo.raw");
    scan.setPrecursorIntensity(1.4f);
    final String mgfScanContents = convertScan(scan);
    String expectedTitle = String.format(
        "TITLE=File: foo.raw, Sample: Sample001 (sample number 1), Period: 1, Cycle(s): 45 (Experiment 1)");
    assert mgfScanContents.contains(expectedTitle) : String.format("Expected %s obtained %s", expectedTitle, mgfScanContents);
  }

}
