package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.LineNumberReader;
import java.util.Map;

import org.testng.collections.Maps;

import com.google.common.base.Optional;
import com.google.inject.internal.Preconditions;

public class MaxQuantReportParserImpl extends PerLineReportParserImpl {
  private static final String[] USED_COLUMNS = new String[] {"Raw file", "Scan number", "Sequence", "Proteins", "Charge", "Score"};
  private Map<String, Integer> columnIndices = Maps.newHashMap();

  private class MaxQuantReportEntry extends BaseReportEntry implements NamedReportEntry {

    MaxQuantReportEntry(final String line) {
      final String[] lineParts = line.split("\t");
      final String spectrum = getPart(lineParts, "Raw file");
      final int scanNumber = Integer.parseInt(getPart(lineParts, "Scan number"));
      final String peptideSequence = getPart(lineParts, "Sequence");
      final String proteins = getPart(lineParts, "Proteins");
      final double peptideScore = Double.parseDouble(getPart(lineParts, "Score"));
      final short charge = Short.parseShort(getPart(lineParts, "Charge"));
      super.setSpectraId(spectrum);
      super.setPeptideProbability(peptideScore);
      super.setScanNumber(scanNumber);
      super.setProteinAccession(proteins);
      super.setScanCharge(charge);
      super.setPeptideSequence(peptideSequence);
    }

    private String getPart(final String[] lineParts, final String columnName) {
      Integer index = columnIndices.get(columnName);
      Preconditions.checkNotNull(index);
      return lineParts[index];
    }

  }

  protected Optional<ReportEntry> parseLine(String line) {
    return Optional.<ReportEntry>of(new MaxQuantReportEntry(line));
  }

  protected void readHeader(final LineNumberReader reader) {
    final String headerLine = nextLine(reader);
    final String[] headerParts = headerLine.split("\t");
    int index = 0;
    for(final String rawColumnName : headerParts) {
      final String columnName = rawColumnName.trim();
      for(final String usedColumn : USED_COLUMNS) {
        if(usedColumn.equals(columnName)) {
          columnIndices.put(usedColumn, index);
        }
      }
      index++;
    }
    for(final String columnName : USED_COLUMNS) {
      if(!columnIndices.containsKey(columnName)) {
        throw new RuntimeException("Failed to parse MaxQaunt msms.txt file, could not find column with name " + columnName);
      }
    }
  }

}
