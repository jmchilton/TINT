package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.File;

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportParser.ReportType;

public class InputReport {
  private final File report;
  private final ReportType reportType;

  public InputReport(File report, ReportType reportType) {
    super();
    this.report = report;
    this.reportType = reportType;
  }

  public File getReport() {
    return report;
  }

  public ReportType getReportType() {
    return reportType;
  }

}
