package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;
import edu.umn.msi.tropix.proteomics.report.PepXmlParserImpl;
import edu.umn.msi.tropix.proteomics.report.SearchReportParser.SpectrumMatch;
import edu.umn.msi.tropix.proteomics.report.SearchReportParser.SpectrumReport;

public class ReportParserImpl implements ReportParser {
  private final ScaffoldReportParser scaffoldReportParser;
  private final PepXmlReportParser pepXmlParser;

  public ReportParserImpl() {
    this(new ScaffoldReportParserImpl(), new PepXmlReportParser());
  }

  public ReportParserImpl(final ScaffoldReportParser scaffoldReportParser,
      final PepXmlReportParser pepXmlReportParser) {
    this.scaffoldReportParser = scaffoldReportParser;
    this.pepXmlParser = pepXmlReportParser;
  }

  public List<ReportEntry> parse(final InputStream inputStream,
      final ReportType reportType) {
    if(reportType == ReportType.SCAFFOLD) {
      return scaffoldReportParser.parse(new InputStreamReader(inputStream));
    } else if(reportType == ReportType.PEPXML) {
      return pepXmlParser.parse(inputStream);
    } else {
      throw new IllegalArgumentException("Unknown report type " + reportType);
    }
  }

  private static class PepXmlReportParser {

    private static class PepXmlReportEntry implements ReportEntry {
      private final String spectraId;
      private final int scanNumber;
      private final short scanCharge;

      private final String proteinAccession;
      private final double peptideProbability;
      private final String peptideSequence;

      PepXmlReportEntry(final SpectrumReport spectrumReport) {
        String spectrum = spectrumReport.getSpectrumName();
        if(DtaNameUtils.isDtaName(spectrum)) {
          final DtaNameSummary summary = DtaNameUtils.getDtaNameSummary(spectrum);
          spectrum = summary.getBasename();
        }
        this.spectraId = spectrum;
        this.scanNumber = spectrumReport.getScan();
        this.scanCharge = spectrumReport.getCharge();
        final SpectrumMatch firstHit = spectrumReport.getMatches().get(0);
        this.peptideSequence = firstHit.getPeptideSequence();
        this.proteinAccession = firstHit.getProteinName();
        this.peptideProbability = firstHit.getPeptideProbability();
      }

      public String getSpectraId() {
        return spectraId;
      }

      public int getScanNumber() {
        return scanNumber;
      }

      public short getScanCharge() {
        return scanCharge;
      }

      public String getProteinAccession() {
        return proteinAccession;
      }

      public double getPeptideProbability() {
        return peptideProbability;
      }

      public String getPeptideSequence() {
        return peptideSequence;
      }

      public double getProteinProbability() {
        return -2.0;
      }

    }

    public List<ReportEntry> parse(final InputStream inputStream) {
      final PepXmlParserImpl xmlParser = new PepXmlParserImpl();
      final Iterator<SpectrumReport> spectrumReports = xmlParser.parse(inputStream);
      final List<ReportEntry> entries = Lists.newArrayList();
      while(spectrumReports.hasNext()) {
        final SpectrumReport pepXmlEntry = spectrumReports.next();
        final ReportEntry entry = new PepXmlReportEntry(pepXmlEntry);
        entries.add(entry);
      }
      return entries;
    }

  }

}
