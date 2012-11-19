package edu.umn.msi.tropix.proteomics.report;

import java.io.InputStream;
import java.util.List;

import com.google.common.collect.UnmodifiableIterator;

public interface SearchReportParser {

  public UnmodifiableIterator<SpectrumReport> parse(final InputStream inputStream);

  public class SpectrumReport {
    private final String spectrumName;
    private final int scan;
    private final int altScan;
    private final short charge;
    private final List<SpectrumMatch> matches;

    public String getSpectrumName() {
      return spectrumName;
    }

    public int getScan() {
      return scan;
    }

    public int getAltScan() {
      return altScan;
    }

    public short getCharge() {
      return charge;
    }

    public List<SpectrumMatch> getMatches() {
      return matches;
    }

    public SpectrumReport(final String spectrumName,
        final int scan,
        final int altScan,
        final short charge,
        final List<SpectrumMatch> matches) {
      this.spectrumName = spectrumName;
      this.scan = scan;
      this.altScan = altScan;
      this.charge = charge;
      this.matches = matches;
    }

  }

  public class SpectrumMatch {
    private final String peptideSequence;
    private final String proteinName;
    private final double peptideProbability;

    public SpectrumMatch(String peptideSequence, String proteinName, double peptideProbability) {
      super();
      this.peptideSequence = peptideSequence;
      this.proteinName = proteinName;
      this.peptideProbability = peptideProbability;
    }

    public String getPeptideSequence() {
      return peptideSequence;
    }

    public String getProteinName() {
      return proteinName;
    }

    public double getPeptideProbability() {
      return peptideProbability;
    }

  }

}
