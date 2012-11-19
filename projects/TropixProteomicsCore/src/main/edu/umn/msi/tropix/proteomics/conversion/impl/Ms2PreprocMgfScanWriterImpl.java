package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class Ms2PreprocMgfScanWriterImpl extends BaseMgfScanWriterImpl {

  public Ms2PreprocMgfScanWriterImpl(OutputStream outputStream, MgfConversionOptions options) {
    super(outputStream, options);
  }

  @Override
  protected boolean doWriteScanNumbers() {
    return false;
  }

  void writeTitle(Scan scan, short precursorCharge) {
    String title = getDefaultTitle(scan, precursorCharge);
    if(title == null) {
      title = "";
    }
    writeTitle(title + ",SCAN" + getScansText(scan));
  }
}
