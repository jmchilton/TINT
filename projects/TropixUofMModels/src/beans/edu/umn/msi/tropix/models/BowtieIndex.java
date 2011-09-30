package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class BowtieIndex extends TropixObject implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.TropixFile object
   **/

  private TropixFile indexesFile;

  /**
   * Retreives the value of indexesFile attribue
   * 
   * @return indexesFile
   **/

  public TropixFile getIndexesFile() {
    return indexesFile;
  }

  /**
   * Sets the value of indexesFile attribue
   **/

  public void setIndexesFile(final TropixFile indexesFile) {
    this.indexesFile = indexesFile;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof BowtieIndex) {
      final BowtieIndex c = (BowtieIndex) obj;
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