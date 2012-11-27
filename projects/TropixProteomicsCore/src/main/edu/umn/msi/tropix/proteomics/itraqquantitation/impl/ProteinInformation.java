package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

public class ProteinInformation {
  private final String proteinAccession;
  private final Double proteinScore;

  public ProteinInformation(final String proteinAccession, final Double proteinScore) {
    this.proteinAccession = proteinAccession;
    this.proteinScore = proteinScore;
  }

  public String getProteinAccession() {
    return proteinAccession;
  }

  public Double getProteinScore() {
    return proteinScore;
  }

}
