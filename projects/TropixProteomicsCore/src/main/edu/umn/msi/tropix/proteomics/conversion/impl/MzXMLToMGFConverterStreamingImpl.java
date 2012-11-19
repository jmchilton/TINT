/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.conversion.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.impl.PeakListParser.PeakListParserOptions;

public class MzXMLToMGFConverterStreamingImpl implements MzXMLToMGFConverter, PeakListToMgfConverter {
  static final String NEWLINE = "\n";

  public void mzxmlToMGF(final InputStream mzxmlStream, final OutputStream mgfStream, final MgfConversionOptions options) {
    final XmlPeakListParser xmlParser = new XmlPeakListParserImpl();
    final UnmodifiableIterator<Scan> mzxmlInfo = xmlParser.parse(mzxmlStream);
    write(mzxmlInfo, mgfStream, options);
  }

  public void peakListToMgf(final Iterable<File> peakListFiles, final OutputStream stream, final MgfConversionOptions options,
      final PeakListParserOptions parserOptions) {
    final PeakListParser peakListParser = new PeakListParserImpl();
    boolean first = true;
    for(File peakListFile : peakListFiles) {
      final UnmodifiableIterator<Scan> scans = peakListParser.parse(peakListFile, parserOptions);
      write(scans, stream, options, first);
      first = false;
    }
  }

  private void write(UnmodifiableIterator<Scan> scans, final OutputStream stream, final MgfConversionOptions options) {
    write(scans, stream, options, true);
  }

  private void write(UnmodifiableIterator<Scan> scans, final OutputStream stream, final MgfConversionOptions options, boolean writeHeader) {
    final MgfScanWriter scanWriter = MgfScanWriterFactory.get(stream, options);
    if(writeHeader) {
      final Formatter formatter = new Formatter(stream);
      formatter.format("COM=Conversion to mascot generic%s", NEWLINE);
      formatter.format("CHARGE=2+ and 3+%s", NEWLINE);
      formatter.flush();
    }
    while(scans.hasNext()) {
      final Scan scan = scans.next();
      checkNotNull(scan.getNumber(), "Scan number not found for a scan");
      scanWriter.writeScan(scan);
    }
  }

}
