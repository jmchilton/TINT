package edu.umn.msi.tropix.proteomics.conversion.impl;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions.MgfStyle;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MsmMgfScanWriterImplTest extends BaseMgfScanWriterImplTest {

  public MsmMgfScanWriterImplTest() {
    this.setMgfStyle(MgfStyle.MSM);
  }

  @Test(groups = "unit")
  public void testConversion() {
    final Scan scan = new Scan(2, 0, 45, new double[] {1.0, 2.0});
    scan.setParentFileName("foo.raw");
    scan.setPrecursorIntensity(1.4f);
    final String mgfScanContents = convertScan(scan);
    String expectedTitle = String.format(
        "TITLE=Elution from: 26.21 to 26.87 period: foo.raw experiment: 1 cycles: 1 precIntensity: %f FinneganScanNumber 45", 1.4f);
    assert mgfScanContents.contains(expectedTitle) : String.format("Expected %s obtained %s", expectedTitle, mgfScanContents);
  }

}
