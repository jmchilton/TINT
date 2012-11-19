package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

class DefaultMgfScanWriterImpl extends BaseMgfScanWriterImpl {

  DefaultMgfScanWriterImpl(final OutputStream outputStream, final MgfConversionOptions options) {
    super(outputStream, options);
  }

  protected void writeTitle(final Scan scan, final short precursorCharge) {
    final String title = getDefaultTitle(scan, precursorCharge);
    if(title != null) {
      writeTitle(title);
    }
  }
}