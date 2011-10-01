package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class ProteinPilotScanWriterImpl extends BaseMgfScanWriterImpl {

  protected ProteinPilotScanWriterImpl(final OutputStream outputStream, final MgfConversionOptions options) {
    super(outputStream, options);
  }

  @Override
  protected void writeTitle(final Scan scan, final short precursorCharge) {
    final String title = String.format("File: %s, Sample: Sample001 (sample number 1), Elution: 2.11 min, Period: 1, Cycle(s): %d (Experiment 1)",
        scan.getParentFileName(), scan.getNumber());
    writeTitle(title);
  }

}
