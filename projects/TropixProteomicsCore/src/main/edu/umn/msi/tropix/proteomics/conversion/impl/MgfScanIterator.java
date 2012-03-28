package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

class MgfScanIterator extends ScanIterator {
  private static final Pattern START_IONS_PATTERN = Pattern.compile("[bB][eE][gG][iI][nN] [iI][oO][nN][sS]");
  private static final Pattern END_IONS_PATTERN = Pattern.compile("[eE][nN][dD] [iI][oO][nN][sS]");
  private final LineIterator lineIterator;
  private List<Short> defaultCharges;
  private String lastLine = "";
  private Optional<String> defaultParentName = Optional.absent();

  MgfScanIterator(final InputStream inputStream) {
    lineIterator = IOUtils.lineIterator(new InputStreamReader(inputStream));
    while(isHeaderLine()) {
      readNextLine();
      if(defaultCharges == null) {
        // See if this was default charges line
        defaultCharges = MgfParseUtils.parseCharges(lastLine);
      } else if(!defaultParentName.isPresent()) {
        defaultParentName = MgfParseUtils.parseDefaultParentName(lastLine);
      }
    }
  }

  private boolean isHeaderLine() {
    return !(isStartOfScan() || outOfLines());
  }

  @Override
  protected Iterable<Scan> getNextScans() {
    while(!isStartOfScan()) {
      if(outOfLines()) {
        return NO_MORE_SCANS;
      }
      readNextLine();
    }
    return parseScanSection();
  }

  private boolean outOfLines() {
    return !lineIterator.hasNext();
  }

  private void readNextLine() {
    lastLine = lineIterator.nextLine().trim();
  }

  private List<Scan> parseScanSection() {
    final List<String> scanSectionLines = readScanSectionLines();
    final List<Scan> scansToCache = extractScans(scanSectionLines);
    return scansToCache;
  }

  private List<Scan> extractScans(final List<String> scanSectionLines) {
    return new MgfScanExtracter(scanSectionLines, defaultCharges).extractScans();
  }

  private List<String> readScanSectionLines() {
    final List<String> scanSectionLines = Lists.newLinkedList();
    while(true) {
      readNextLine();
      scanSectionLines.add(lastLine);
      if(isEndOfScanSection()) {
        break;
      }
    }
    return scanSectionLines;
  }

  private boolean isEndOfScanSection() {
    return END_IONS_PATTERN.matcher(lastLine).matches();
  }

  private boolean isStartOfScan() {
    return START_IONS_PATTERN.matcher(lastLine).matches();
  }

}