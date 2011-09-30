package edu.umn.msi.tropix.proteomics.conversion.impl;

import org.testng.annotations.Test;

public class MgfParseUtilsTest {

  @Test(groups = "unit")
  public void testScan() {
    final String line1 = "SCANS=1-4";
    assert MgfParseUtils.parseScanStart(line1) == 1;
    assert MgfParseUtils.parseScanEnd(line1) == 4;
    
    final String line2 = "SCANS= 4 ";
    assert MgfParseUtils.parseScanStart(line2) == 4;
    assert MgfParseUtils.parseScanEnd(line2) == 4;    
  }
  
}
