package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MsmMgfScanWriterImpl extends BaseMgfScanWriterImpl {

  protected MsmMgfScanWriterImpl(OutputStream outputStream, MgfConversionOptions options) {
    super(outputStream, options);
  }

  protected void writeTitle(Scan scan, short precursorCharge) {
    final Float intensity = scan.getPrecursorIntensity();
    final String intensityString = (intensity > 0.0) ? String.format("precIntensity: %f ", intensity) : "";
    final String title = String.format("Elution from: 26.21 to 26.87 period: %s experiment: 1 cycles: 1 %sFinneganScanNumber %d",
        scan.getParentFileName(), intensityString, scan.getNumber());
    writeTitle(title);
  }
}
