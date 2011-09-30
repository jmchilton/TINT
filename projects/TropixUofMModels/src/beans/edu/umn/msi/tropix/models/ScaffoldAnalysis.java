package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 
	*/
public class ScaffoldAnalysis extends Analysis implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.TropixFile object
   **/

  private TropixFile outputs;

  /**
   * Retreives the value of outputs attribue
   * 
   * @return outputs
   **/

  public TropixFile getOutputs() {
    return outputs;
  }

  /**
   * Sets the value of outputs attribue
   **/

  public void setOutputs(final TropixFile outputs) {
    this.outputs = outputs;
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

  private TropixFile input;

  /**
   * Retreives the value of input attribue
   * 
   * @return input
   **/

  public TropixFile getInput() {
    return input;
  }

  /**
   * Sets the value of input attribue
   **/

  public void setInput(final TropixFile input) {
    this.input = input;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof ScaffoldAnalysis) {
      final ScaffoldAnalysis c = (ScaffoldAnalysis) obj;
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