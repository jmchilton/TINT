package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class FileType implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

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
	* 	**/
  public String shortName;

  /**
   * Retreives the value of shortName attribute
   * 
   * @return shortName
   **/

  public String getShortName() {
    return shortName;
  }

  /**
   * Sets the value of shortName attribue
   **/

  public void setShortName(final String shortName) {
    this.shortName = shortName;
  }

  /**
	* 	**/
  public String extension;

  /**
   * Retreives the value of extension attribute
   * 
   * @return extension
   **/

  public String getExtension() {
    return extension;
  }

  /**
   * Sets the value of extension attribue
   **/

  public void setExtension(final String extension) {
    this.extension = extension;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof FileType) {
      final FileType c = (FileType) obj;
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