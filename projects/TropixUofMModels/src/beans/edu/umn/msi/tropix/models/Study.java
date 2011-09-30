package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Study extends TropixObject implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String publishedCitation;

  /**
   * Retreives the value of publishedCitation attribute
   * 
   * @return publishedCitation
   **/

  public String getPublishedCitation() {
    return publishedCitation;
  }

  /**
   * Sets the value of publishedCitation attribue
   **/

  public void setPublishedCitation(final String publishedCitation) {
    this.publishedCitation = publishedCitation;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixObject object's collection
   **/

  private Collection<TropixObject> contents;

  /**
   * Retreives the value of contents attribue
   * 
   * @return contents
   **/

  public Collection<TropixObject> getContents() {
    return contents;
  }

  /**
   * Sets the value of contents attribue
   **/

  public void setContents(final Collection<TropixObject> contents) {
    this.contents = contents;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Study) {
      final Study c = (Study) obj;
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