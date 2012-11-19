package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStream;
import java.util.List;

public interface ReportParser {
  public static enum ReportType {
    SCAFFOLD, PEPXML;
  }

  List<ReportEntry> parse(InputStream inputStream, ReportType reportType);

}
