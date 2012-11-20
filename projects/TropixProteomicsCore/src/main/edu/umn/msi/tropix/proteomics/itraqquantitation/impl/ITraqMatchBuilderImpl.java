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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.impl.XmlPeakListParser;
import edu.umn.msi.tropix.proteomics.conversion.impl.XmlPeakListParserImpl;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractor.ReportType;

class ITraqMatchBuilderImpl implements ITraqMatchBuilder {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private ReportExtractor reportParser = new ReportExtractorImpl();
  private XmlPeakListParser peakListParser = new XmlPeakListParserImpl();
  private ITraqMatcher iTraqMatcher;

  private void addSummariesForFile(final File mzxmlFile, final Integer fileIndex, final Map<ScanIndex, ITraqScanSummary> scanMap,
      final ITraqMatchBuilderOptions options,
      boolean useScanNumber) {
    // Parse the mzxml files
    final InputStream inputStream = FILE_UTILS.getFileInputStream(mzxmlFile);
    try {
      final Iterator<Scan> scans = peakListParser.parse(inputStream);
      while(scans.hasNext()) {
        final Scan mzxmlScan = scans.next();
        final int number = useScanNumber ? mzxmlScan.getNumber() : mzxmlScan.getIndex();
        final short charge = mzxmlScan.getPrecursorCharge();
        // System.out.println("UseNumber:" + useScanNumber + " " + number + " <- number charge ->" + charge + " " + mzxmlScan.getIndex() + " "
        // + mzxmlScan.getNumber());
        final double[] peaks = mzxmlScan.getPeaks();
        scanMap.put(
            new ScanIndex(mzxmlScan.getParentName(), fileIndex, number, charge, Lists.newArrayList(mzxmlScan.getParentName(),
                FilenameUtils.getBaseName(mzxmlFile.getName()), mzxmlFile.getName())),
            ITraqScanSummary.fromPeaks(number, number, charge, options.getITraqLabels(), peaks));
      }
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }
  }

  /*
   * Returns a Map with the names of the runs defined in the MzXML files as keys, and a array of scans corresponding to that run as the values.
   */
  private Map<ScanIndex, ITraqScanSummary> parseScans(final Iterable<File> mzxmlInputs, final ITraqMatchBuilderOptions options, boolean useScanNumber) {
    final Map<ScanIndex, ITraqScanSummary> scanMap = Maps.newHashMap();

    // Loop through the mzxml files
    int fileIndex = 0;
    for(final File file : mzxmlInputs) {
      addSummariesForFile(file, fileIndex++, scanMap, options, useScanNumber);
    }

    return scanMap;
  }

  public List<ITraqMatch> buildDataEntries(final Iterable<File> mzxmlInputs, final InputReport inputReport, final ITraqMatchBuilderOptions options) {
    // Parse the specified Scaffold spectrum report
    final Iterable<ReportEntry> scaffoldEntries = reportParser.parse(FILE_UTILS.getFileInputStream(inputReport.getReport()),
        inputReport.getReportType());

    // System.out.println("Found " + Iterables.size(scaffoldEntries) + " entries.");

    // Parse the MzXML files and obtain scan arrays for each
    final Map<ScanIndex, ITraqScanSummary> scansMap = parseScans(mzxmlInputs, options, inputReport.getReportType() == ReportType.SCAFFOLD);

    return iTraqMatcher.match(scaffoldEntries, new ScanFunctionImpl(scansMap), options);
  }

  interface ScanIndexMatcher {
    boolean match(final ScanIndex query, final ScanIndex target);
  }

  static class AlternativeNameExactScanIndexMatcher implements ScanIndexMatcher {

    public boolean match(ScanIndex query, ScanIndex target) {
      return query.numberChargeAndAlternativeNameMatch(target);
    }

  }

  private static final ScanIndexMatcher ALTERNATIVE_NAME_MATCHER = new AlternativeNameExactScanIndexMatcher();

  static class NameAndScanNumberMatcher implements ScanIndexMatcher {

    public boolean match(ScanIndex query, ScanIndex target) {
      return query.numberAndNameMatch(target);
    }

  }

  private static final ScanIndexMatcher NAME_AND_SCAN_NUMBER_MATCHER = new NameAndScanNumberMatcher();

  static class NumberAndChargeMatcher implements ScanIndexMatcher {

    public boolean match(ScanIndex query, ScanIndex target) {
      return query.numberAndChargeMatch(target);
    }

  }

  static class FileIndexMatcher implements ScanIndexMatcher {

    public boolean match(ScanIndex query, ScanIndex target) {
      return query.numberChargeAndFileIndexMatch(target);
    }

  }

  private static final ScanIndexMatcher NUMBER_CHARGE_AND_FILE_INDEX_MATCHER = new FileIndexMatcher();

  private static final ScanIndexMatcher NUMBER_AND_CHARGE_MATCHER = new NumberAndChargeMatcher();

  private static final ScanIndexMatcher[] MATCHING_ALGORITHMS = new ScanIndexMatcher[] {
      NUMBER_CHARGE_AND_FILE_INDEX_MATCHER,
      ALTERNATIVE_NAME_MATCHER,
      NAME_AND_SCAN_NUMBER_MATCHER,
      NUMBER_AND_CHARGE_MATCHER
  };

  private final class ScanFunctionImpl implements Function<ScanIndex, ITraqScanSummary> {
    private final Map<ScanIndex, ITraqScanSummary> scansMap;

    private ScanFunctionImpl(final Map<ScanIndex, ITraqScanSummary> scansMap) {
      this.scansMap = scansMap;
    }

    private List<ScanIndex> getMatches(final ScanIndex scanIndex, ScanIndexMatcher matcher) {
      final List<ScanIndex> matches = Lists.newArrayList();
      for(final Map.Entry<ScanIndex, ITraqScanSummary> entry : scansMap.entrySet()) {
        if(matcher.match(scanIndex, entry.getKey())) {
          matches.add(entry.getKey());
        }
      }
      return matches;
    }

    private ITraqScanSummary tryFindSummary(final ScanIndex scanIndex, final List<ScanIndex> potentialMatches) {
      ITraqScanSummary match = null;
      if(potentialMatches.size() > 1) {
        final String ambiguousMatches = Iterables.toString(potentialMatches);
        final String errorMessage = String.format("Ambigious match for scan %s possible matches include %s ", scanIndex, ambiguousMatches);
        throw new IllegalStateException(errorMessage);
      } else if(!potentialMatches.isEmpty()) {
        match = scansMap.get(potentialMatches.get(0));
      }
      return match;
    }

    public ITraqScanSummary apply(final ScanIndex scanIndex) {
      ITraqScanSummary summary = scansMap.get(scanIndex);
      // Try a little harder if an exact match cannot be found...
      for(ScanIndexMatcher matcher : MATCHING_ALGORITHMS) {
        if(summary == null) {
          final List<ScanIndex> potentialMatches = getMatches(scanIndex, matcher);
          summary = tryFindSummary(scanIndex, potentialMatches);
        }
      }
      if(summary == null) {
        throw new IllegalStateException("Found no match for scan " + scanIndex + "in scans map " + summarizeScansMap());
      }
      return summary;
    }

    private String summarizeScansMap() {
      final Set<String> names = Sets.newHashSet();
      for(final ScanIndex index : scansMap.keySet()) {
        names.add(index.getName());
      }
      return "ScansMap{entries=" + scansMap.size() + ", uniqueNames=" + Iterables.toString(names) + "}";
    }
  }

  public void setReportParser(final ReportExtractor reportParser) {
    this.reportParser = reportParser;
  }

  public void setXmlPeakListParser(final XmlPeakListParser peakListParser) {
    this.peakListParser = peakListParser;
  }

  public void setItraqMatcher(final ITraqMatcher iTraqMatcher) {
    this.iTraqMatcher = iTraqMatcher;
  }

}
