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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser.MzxmlInfo;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MzXMLToMGFConverterStreamingImpl implements MzXMLToMGFConverter {
  static final String NEWLINE = "\n";
  private MzxmlParser mzxmlParser = new MzxmlParserImpl();

  public void mzxmlToMGF(final InputStream mzxmlStream, final OutputStream mgfStream, final MgfConversionOptions options) {
    final MgfScanWriter scanWriter = MgfScanWriterFactory.get(mgfStream, options);
    final MzxmlInfo mzxmlInfo = mzxmlParser.parse(mzxmlStream);
    final Formatter formatter = new Formatter(mgfStream);
    formatter.format("COM=Conversion to mascot generic%s", NEWLINE);
    formatter.format("CHARGE=2+ and 3+%s", NEWLINE);
    formatter.flush();
    if(Iterables.isEmpty(mzxmlInfo)) {
      return;
    }
    for(final Scan scan : mzxmlInfo) {
      checkNotNull(scan.getNumber(), "Scan number not found for a scan");
      scanWriter.writeScan(scan);
    }
  }

}
