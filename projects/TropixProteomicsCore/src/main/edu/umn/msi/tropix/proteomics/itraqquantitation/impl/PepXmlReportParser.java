package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractorImpl.ReportParser;
import edu.umn.msi.tropix.proteomics.report.PepXmlParserImpl;
import edu.umn.msi.tropix.proteomics.report.SearchReportParser.SpectrumMatch;
import edu.umn.msi.tropix.proteomics.report.SearchReportParser.SpectrumReport;

public class PepXmlReportParser implements ReportParser {

  private static class PepXmlReportEntry extends BaseReportEntry implements NamedReportEntry {

    PepXmlReportEntry(final SpectrumReport spectrumReport) {
      String spectrum = spectrumReport.getSpectrumName();
      if(DtaNameUtils.isDtaName(spectrum)) {
        final DtaNameSummary summary = DtaNameUtils.getDtaNameSummary(spectrum);
        spectrum = summary.getBasename();
      }
      setSpectraId(spectrum);
      setScanNumber(spectrumReport.getScan());
      setScanCharge(spectrumReport.getCharge());
      final SpectrumMatch firstHit = spectrumReport.getMatches().get(0);
      setPeptideSequence(firstHit.getPeptideSequence());
      setProteinAccession(firstHit.getProteinName());
      setPeptideProbability(firstHit.getPeptideProbability());
    }

  }

  public List<ReportEntry> parse(final InputStream inputStream) {
    final PepXmlParserImpl xmlParser = new PepXmlParserImpl();
    final Iterator<SpectrumReport> spectrumReports = xmlParser.parse(inputStream);
    final List<ReportEntry> entries = Lists.newArrayList();
    while(spectrumReports.hasNext()) {
      final SpectrumReport pepXmlEntry = spectrumReports.next();
      final NamedReportEntry entry = new PepXmlReportEntry(pepXmlEntry);
      entries.add(entry);
    }
    return entries;
  }

}