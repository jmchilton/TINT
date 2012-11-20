package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

abstract class PerLineReportParserImpl implements ReportExtractorImpl.ReportParser {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public List<ReportEntry> parse(InputStream inputStream) {
    List<ReportEntry> reportEntries = Lists.newArrayList();
    LineNumberReader reader = new LineNumberReader(new InputStreamReader(inputStream));
    readHeader(reader);
    try {
      while(true) {
        final String line = nextLine(reader);
        if(line == null) {
          break;
        }
        if(line.trim().equals("")) {
          continue;
        }
        Optional<ReportEntry> entryOption = parseLine(line);
        if(entryOption.isPresent()) {
          reportEntries.add(entryOption.get());
        }
      }
    } finally {
      IO_UTILS.closeQuietly(reader);
      IO_UTILS.closeQuietly(inputStream);
    }
    return reportEntries;
  }

  protected void readHeader(final LineNumberReader reader) {
    // Read-one line by default, the table header.
    nextLine(reader);
  }

  protected String nextLine(final LineNumberReader reader) {
    final String line = IO_UTILS.readLine(reader);
    return line;
  }

  protected abstract Optional<ReportEntry> parseLine(final String line);

}
