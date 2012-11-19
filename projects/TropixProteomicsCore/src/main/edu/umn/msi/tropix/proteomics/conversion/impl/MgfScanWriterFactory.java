package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions.MgfStyle;

public class MgfScanWriterFactory {

  public static MgfScanWriter get(final OutputStream outputStream, @Nullable final MgfConversionOptions optionsInput) {
    final MgfConversionOptions options = (optionsInput == null) ? new MgfConversionOptions() : optionsInput;
    final MgfStyle mgfStyle = options.getMgfStyle();
    MgfScanWriter writer;
    switch(mgfStyle) {
    case MSM:
      writer = new MsmMgfScanWriterImpl(outputStream, options);
      break;
    case PROTEIN_PILOT:
      writer = new ProteinPilotScanWriterImpl(outputStream, options);
      break;
    case MS2PREPROC:
      writer = new Ms2PreprocMgfScanWriterImpl(outputStream, options);
      break;
    default:
      writer = new DefaultMgfScanWriterImpl(outputStream, options);
      break;
    }
    return writer;
  }
}
