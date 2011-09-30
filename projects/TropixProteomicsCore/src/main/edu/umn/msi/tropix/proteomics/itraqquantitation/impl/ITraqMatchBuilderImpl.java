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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser.MzxmlInfo;

class ITraqMatchBuilderImpl implements ITraqMatchBuilder {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private ScaffoldReportParser scaffoldReportParser;
  private MzxmlParser mzxmlParser;
  private ITraqMatcher iTraqMatcher;

  private void addSummariesForFile(final File mzxmlFile, final Map<ScanIndex, ITraqScanSummary> scanMap, final ITraqMatchBuilderOptions options) {
    // Parse the mzxml files
    final InputStream inputStream = FILE_UTILS.getFileInputStream(mzxmlFile);
    try {
      final MzxmlInfo mzxmlInfo = mzxmlParser.parse(inputStream);

      for(final Scan mzxmlScan : mzxmlInfo) {
        final int number = mzxmlScan.getNumber();
        final short charge = mzxmlScan.getPrecursorCharge();
        final double[] peaks = mzxmlScan.getPeaks();
        scanMap.put(new ScanIndex(mzxmlScan.getParentName(), number, charge), ITraqScanSummary.fromPeaks(number, number, charge, options.getITraqLabels(), peaks));
      }
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }
  }

  /*
   * Returns a Map with the names of the runs defined in the MzXML files as keys, and a array of scans corresponding to that run as the values.
   */
  private Map<ScanIndex, ITraqScanSummary> parseScans(final Iterable<File> mzxmlInputs, final ITraqMatchBuilderOptions options) {
    final Map<ScanIndex, ITraqScanSummary> scanMap = Maps.newHashMap();

    // Loop through the mzxml files
    for(final File file : mzxmlInputs) {
      addSummariesForFile(file, scanMap, options);
    }

    return scanMap;
  }

  public List<ITraqMatch> buildDataEntries(final Iterable<File> mzxmlInputs, final File scaffoldReport, final ITraqMatchBuilderOptions options) {
    // Parse the specified Scaffold spectrum report
    final Iterable<ScaffoldEntry> scaffoldEntries = scaffoldReportParser.parse(FILE_UTILS.getFileReader(scaffoldReport));

    // Parse the MzXML files and obtain scan arrays for each
    final Map<ScanIndex, ITraqScanSummary> scansMap = parseScans(mzxmlInputs, options);

    return iTraqMatcher.match(scaffoldEntries, new ScanFunctionImpl(scansMap));
  }
  
  private final class ScanFunctionImpl implements Function<ScanIndex, ITraqScanSummary> {
    private final Map<ScanIndex, ITraqScanSummary> scansMap;
    
    private ScanFunctionImpl(final Map<ScanIndex, ITraqScanSummary> scansMap) {
      this.scansMap = scansMap;
    }
    
    public ITraqScanSummary apply(final ScanIndex scanIndex) {
      ITraqScanSummary summary = scansMap.get(scanIndex);
      // Try a little harder if an exact match cannot be found...
      if(summary == null) {
        for(final Map.Entry<ScanIndex, ITraqScanSummary> entry : scansMap.entrySet()) {
          if(scanIndex.numberAndChargeMatch(entry.getKey())) {
            if(summary == null) {
              summary = entry.getValue();
            } else {
              throw new IllegalStateException("Ambigious match for scan " + scanIndex + " possible matches include " + entry.getKey());
            }
          }
        }
      }
      if(summary == null) {
        throw new IllegalStateException("Found no match for scan " + scanIndex);
      }
      return summary;
    }
    
  }

  public void setScaffoldReportParser(final ScaffoldReportParser scaffoldReportParser) {
    this.scaffoldReportParser = scaffoldReportParser;
  }

  public void setMzxmlParser(final MzxmlParser mzxmlParser) {
    this.mzxmlParser = mzxmlParser;
  }

  public void setItraqMatcher(final ITraqMatcher iTraqMatcher) {
    this.iTraqMatcher = iTraqMatcher;
  }

}
