package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class ProteomicsRun extends Run implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.TissueSample object
   **/

  private TissueSample tissueSample;

  /**
   * Retreives the value of tissueSample attribue
   * 
   * @return tissueSample
   **/

  public TissueSample getTissueSample() {
    return tissueSample;
  }

  /**
   * Sets the value of tissueSample attribue
   **/

  public void setTissueSample(final TissueSample tissueSample) {
    this.tissueSample = tissueSample;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixFile object
   **/

  private TropixFile source;

  /**
   * Retreives the value of source attribue
   * 
   * @return source
   **/

  public TropixFile getSource() {
    return source;
  }

  /**
   * Sets the value of source attribue
   **/

  public void setSource(final TropixFile source) {
    this.source = source;
  }

  /**
   * An associated edu.umn.msi.tropix.models.IdentificationAnalysis object's collection
   **/

  private Collection<IdentificationAnalysis> identificationAnalyses;

  /**
   * Retreives the value of identificationAnalyses attribue
   * 
   * @return identificationAnalyses
   **/

  public Collection<IdentificationAnalysis> getIdentificationAnalyses() {
    return identificationAnalyses;
  }

  /**
   * Sets the value of identificationAnalyses attribue
   **/

  public void setIdentificationAnalyses(final Collection<IdentificationAnalysis> identificationAnalyses) {
    this.identificationAnalyses = identificationAnalyses;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixFile object
   **/

  private TropixFile mzxml;

  /**
   * Retreives the value of mzxml attribue
   * 
   * @return mzxml
   **/

  public TropixFile getMzxml() {
    return mzxml;
  }

  /**
   * Sets the value of mzxml attribue
   **/

  public void setMzxml(final TropixFile mzxml) {
    this.mzxml = mzxml;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof ProteomicsRun) {
      final ProteomicsRun c = (ProteomicsRun) obj;
      if(getId() != null && getId().equals(c.getId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns hash code for the primary key of the object
   **/
  @Override
  public int hashCode() {
    if(getId() != null) {
      return getId().hashCode();
    }
    return 0;
  }

}