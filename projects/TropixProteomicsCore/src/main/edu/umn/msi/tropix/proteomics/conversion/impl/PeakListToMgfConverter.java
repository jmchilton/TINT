package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.File;
import java.io.OutputStream;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.impl.PeakListParser.PeakListParserOptions;

public interface PeakListToMgfConverter {

  public void peakListToMgf(Iterable<File> peakListFiles, OutputStream stream, MgfConversionOptions options, PeakListParserOptions parserOptions);

}