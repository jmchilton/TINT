package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class BowtieAnalysis extends Analysis implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.Database object's collection
   **/

  private Collection<Database> databases;

  /**
   * Retreives the value of databases attribue
   * 
   * @return databases
   **/

  public Collection<Database> getDatabases() {
    return databases;
  }

  /**
   * Sets the value of databases attribue
   **/

  public void setDatabases(final Collection<Database> databases) {
    this.databases = databases;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixFile object
   **/

  private TropixFile output;

  /**
   * Retreives the value of output attribue
   * 
   * @return output
   **/

  public TropixFile getOutput() {
    return output;
  }

  /**
   * Sets the value of output attribue
   **/

  public void setOutput(final TropixFile output) {
    this.output = output;
  }

  /**
   * An associated edu.umn.msi.tropix.models.BowtieIndex object
   **/

  private BowtieIndex index;

  /**
   * Retreives the value of index attribue
   * 
   * @return index
   **/

  public BowtieIndex getIndex() {
    return index;
  }

  /**
   * Sets the value of index attribue
   **/

  public void setIndex(final BowtieIndex index) {
    this.index = index;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof BowtieAnalysis) {
      final BowtieAnalysis c = (BowtieAnalysis) obj;
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