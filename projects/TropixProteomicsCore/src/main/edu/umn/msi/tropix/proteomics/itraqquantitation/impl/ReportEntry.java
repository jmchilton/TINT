package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

interface ReportEntry {

  public int getScanNumber();

  public short getScanCharge();

  public String getProteinAccession();

  public double getProteinProbability();

  public String getPeptideSequence();

  public double getPeptideProbability();

  public SequenceWithModifications getModifiedPeptideSequence();

  public static class SequenceWithModifications {
    private String modifiedSequence;

    public SequenceWithModifications(final String modifiedSequence) {
      this.modifiedSequence = modifiedSequence;
    }

    public String toString() {
      return modifiedSequence;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((modifiedSequence == null) ? 0 : modifiedSequence.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj)
        return true;
      if(obj == null)
        return false;
      if(getClass() != obj.getClass())
        return false;
      SequenceWithModifications other = (SequenceWithModifications) obj;
      if(modifiedSequence == null) {
        if(other.modifiedSequence != null)
          return false;
      } else if(!modifiedSequence.equals(other.modifiedSequence))
        return false;
      return true;
    }

  }

}