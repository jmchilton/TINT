package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.io.InputStream;
import java.util.Map;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class BiomlParameterReaderImplTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  
  @Test(groups = "unit")
  public void testReaderParameters() {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream("tandem-output-truncated.xml");
    try {
      final BiomlParameterReaderImpl reader = new BiomlParameterReaderImpl();
      final Map<String, String> map = reader.buildMap(inputStream);
      assert map.get("spectrum, neutral loss window").equals("15.0");
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }  
  }
  
}
