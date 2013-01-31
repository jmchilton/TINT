package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

interface ReportEntry {

  public int getScanNumber();

  public short getScanCharge();

  public String getProteinAccession();

  public double getProteinProbability();

  public String getPeptideSequence();

  public double getPeptideProbability();

  public SequenceWithModifications getModifiedPeptideSequence();

  public interface CanSplitModifications {
    // One sequence-and-mod combo for each unique mod.
    List<SequenceWithModifications> splitupModifications();
  }

  public static class SimpleSequenceWithModifications extends SequenceWithModifications implements CanSplitModifications {
    private ImmutableList<String> mods;
    private String sequence;

    public SimpleSequenceWithModifications(String sequence, String mod) {
      this(sequence, Lists.newArrayList(mod));
    }

    public SimpleSequenceWithModifications(String sequence, Iterable<String> mods) {
      super(String.format("%s|%s", sequence, Joiner.on(",").join(mods)));
      this.mods = ImmutableList.copyOf(mods);
      this.sequence = sequence;
    }

    public List<SequenceWithModifications> splitupModifications() {
      final List<SequenceWithModifications> splitup = Lists.newArrayList();
      for(final String mod : Sets.newHashSet(mods)) { // unique
        splitup.add(new SimpleSequenceWithModifications(sequence, mod));
      }
      return splitup;
    }

  }

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