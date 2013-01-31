package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;
import org.testng.collections.Lists;

import com.google.common.base.Optional;

public class ProteinPilotReportParserImpl extends PerLineReportParserImpl {

  private static class ProteinPilotReportEntry extends BaseReportEntry implements IndexedReportEntry {
    private int fileIndex;

    ProteinPilotReportEntry(final String line) {
      final String[] values = line.split("\t");
      final String spectrumName = values[22];
      final String theoriticalChargeState = values[20];
      final String peptideSequence = values[12];
      final String rawModifications = values[13];
      final String proteinNames = values[6];
      final String peptideConfidence = values[5];
      final String[] spectrumNameParts = spectrumName.split("\\.");
      final Integer inputIndex = Integer.parseInt(spectrumNameParts[0]);
      final Integer scanNumber = Integer.parseInt(spectrumNameParts[3]);
      final Short charge = Short.parseShort(theoriticalChargeState); // Might not match

      super.setPeptideProbability(Double.parseDouble(peptideConfidence));
      super.setPeptideSequence(peptideSequence);
      final List<String> modifications = Lists.newArrayList();
      if(StringUtils.hasText(rawModifications.trim())) {
        final String[] splitModifications = rawModifications.split("\\s*;\\s*");
        for(int i = 0; i < splitModifications.length; i++) {
          splitModifications[i] = splitModifications[i].trim();
        }
        modifications.addAll(Arrays.asList(splitModifications));
      } else {
        modifications.add("*UNMODIFIED*");
      }
      super.setModifiedPeptideSequence(new SimpleSequenceWithModifications(peptideSequence, modifications));
      super.setProteinAccession(proteinNames);
      super.setScanCharge(charge);
      super.setScanNumber(scanNumber);
      fileIndex = inputIndex - 1;
    }

    public int getInputFileIndex() {
      return fileIndex;
    }

  }

  protected Optional<ReportEntry> parseLine(String line) {
    return Optional.<ReportEntry>of(new ProteinPilotReportEntry(line));
  }
}
