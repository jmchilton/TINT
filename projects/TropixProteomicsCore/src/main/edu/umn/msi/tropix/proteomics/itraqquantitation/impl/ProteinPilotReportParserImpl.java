package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import com.google.common.base.Optional;

public class ProteinPilotReportParserImpl extends PerLineReportParserImpl {

  private static class ProteinPilotReportEntry extends BaseReportEntry implements IndexedReportEntry {
    private int fileIndex;

    ProteinPilotReportEntry(final String line) {
      final String[] values = line.split("\t");
      final String spectrumName = values[22];
      final String theoriticalChargeState = values[20];
      final String peptideSequence = values[12];
      final String modifications = values[13];
      final String proteinNames = values[6];
      final String peptideConfidence = values[5];
      final String[] spectrumNameParts = spectrumName.split("\\.");
      final Integer inputIndex = Integer.parseInt(spectrumNameParts[0]);
      final Integer scanNumber = Integer.parseInt(spectrumNameParts[3]);
      final Short charge = Short.parseShort(theoriticalChargeState); // Might not match

      super.setPeptideProbability(Double.parseDouble(peptideConfidence));
      super.setPeptideSequence(peptideSequence);
      super.setModifiedPeptideSequence(new SequenceWithModifications(peptideSequence + "|" + modifications));
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
