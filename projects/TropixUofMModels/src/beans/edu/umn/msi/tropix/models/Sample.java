package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Sample extends TropixObject implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.Sample object's collection
   **/

  private Collection<Sample> childSamples;

  /**
   * Retreives the value of childSamples attribue
   * 
   * @return childSamples
   **/

  public Collection<Sample> getChildSamples() {
    return childSamples;
  }

  /**
   * Sets the value of childSamples attribue
   **/

  public void setChildSamples(final Collection<Sample> childSamples) {
    this.childSamples = childSamples;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Sample) {
      final Sample c = (Sample) obj;
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