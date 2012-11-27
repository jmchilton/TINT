package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportEntry.SequenceWithModifications;

public class BaseReportEntry {

  private String spectraId;
  private int scanNumber;
  private short scanCharge;
  private String proteinAccession;
  private double peptideProbability;
  private String peptideSequence;
  private SequenceWithModifications modifiedPeptideSequence;

  public BaseReportEntry() {
    super();
  }

  protected void setSpectraId(String spectraId) {
    this.spectraId = spectraId;
  }

  protected void setScanNumber(int scanNumber) {
    this.scanNumber = scanNumber;
  }

  protected void setScanCharge(short scanCharge) {
    this.scanCharge = scanCharge;
  }

  protected void setProteinAccession(String proteinAccession) {
    this.proteinAccession = proteinAccession;
  }

  protected void setPeptideProbability(double peptideProbability) {
    this.peptideProbability = peptideProbability;
  }

  protected void setPeptideSequence(String peptideSequence) {
    this.peptideSequence = peptideSequence;
  }

  protected void setModifiedPeptideSequence(final SequenceWithModifications modifiedPeptideSequence) {
    this.modifiedPeptideSequence = modifiedPeptideSequence;
  }

  public String getSpectraId() {
    return spectraId;
  }

  public int getScanNumber() {
    return scanNumber;
  }

  public short getScanCharge() {
    return scanCharge;
  }

  public String getProteinAccession() {
    return proteinAccession;
  }

  public double getPeptideProbability() {
    return peptideProbability;
  }

  public String getPeptideSequence() {
    return peptideSequence;
  }

  public double getProteinProbability() {
    return -2.0;
  }

  public SequenceWithModifications getModifiedPeptideSequence() {
    return modifiedPeptideSequence;
  }

}