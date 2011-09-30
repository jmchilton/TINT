package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class PhysicalFile implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public Long size;

  /**
   * Retreives the value of size attribute
   * 
   * @return size
   **/

  public Long getSize() {
    return size;
  }

  /**
   * Sets the value of size attribue
   **/

  public void setSize(final Long size) {
    this.size = size;
  }

  /**
	* 	**/
  public String hash;

  /**
   * Retreives the value of hash attribute
   * 
   * @return hash
   **/

  public String getHash() {
    return hash;
  }

  /**
   * Sets the value of hash attribue
   **/

  public void setHash(final String hash) {
    this.hash = hash;
  }

  /**
	* 	**/
  public String id;

  /**
   * Retreives the value of id attribute
   * 
   * @return id
   **/

  public String getId() {
    return id;
  }

  /**
   * Sets the value of id attribue
   **/

  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof PhysicalFile) {
      final PhysicalFile c = (PhysicalFile) obj;
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