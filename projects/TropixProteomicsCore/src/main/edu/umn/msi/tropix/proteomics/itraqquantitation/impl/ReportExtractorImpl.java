package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;

public class ReportExtractorImpl implements ReportExtractor {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private Map<ReportType, Class<? extends ReportParser>> reportParsers = ImmutableMap.<ReportType, Class<? extends ReportParser>>builder()
      .put(ReportType.SCAFFOLD, ScaffoldReportParserImpl.class)
      .put(ReportType.PEPXML, PepXmlReportParser.class)
      .put(ReportType.PROTEIN_PILOT, ProteinPilotReportParserImpl.class)
      .put(ReportType.MAX_QUANT, MaxQuantReportParserImpl.class)
      .build();

  public List<ReportEntry> parse(final InputStream inputStream, final ReportType reportType) {
    final Class<? extends ReportParser> parserClass = reportParsers.get(reportType);
    final ReportParser parser = REFLECTION_HELPER.newInstance(parserClass);
    if(parser != null) {
      return parser.parse(inputStream);
    } else {
      throw new IllegalArgumentException("Unknown report type " + reportType);
    }
  }

  static interface ReportParser {
    List<ReportEntry> parse(InputStream inputStream);
  }

}
