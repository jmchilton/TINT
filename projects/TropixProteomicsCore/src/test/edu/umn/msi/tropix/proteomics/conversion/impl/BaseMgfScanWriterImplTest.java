package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.ByteArrayOutputStream;

import org.testng.annotations.BeforeMethod;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions.MgfStyle;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class BaseMgfScanWriterImplTest {
  protected MgfStyle mgfStyle;
  private ByteArrayOutputStream stream;
  private MgfScanWriter scanWriter;

  @BeforeMethod(groups = "unit")
  public void setupStream() {
    final MgfConversionOptions options = new MgfConversionOptions();
    options.setMgfStyle(mgfStyle);
    stream = new ByteArrayOutputStream();
    scanWriter = MgfScanWriterFactory.get(stream, options);
  }

  protected String convertScan(final Scan scan) {
    scanWriter.writeScan(scan);
    return new String(stream.toByteArray());
  }

}
