package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class GalaxyTool implements Serializable {
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
  public String name;

  /**
   * Retreives the value of name attribute
   * 
   * @return name
   **/

  public String getName() {
    return name;
  }

  /**
   * Sets the value of name attribue
   **/

  public void setName(final String name) {
    this.name = name;
  }

  /**
	* 	**/
  public String description;

  /**
   * Retreives the value of description attribute
   * 
   * @return description
   **/

  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description attribue
   **/

  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * An associated edu.umn.msi.tropix.models.GalaxyToolRevision object's collection
   **/

  private Collection<GalaxyToolRevision> revisions;

  /**
   * Retreives the value of revisions attribue
   * 
   * @return revisions
   **/

  public Collection<GalaxyToolRevision> getRevisions() {
    return revisions;
  }

  /**
   * Sets the value of revisions attribue
   **/

  public void setRevisions(final Collection<GalaxyToolRevision> revisions) {
    this.revisions = revisions;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof GalaxyTool) {
      final GalaxyTool c = (GalaxyTool) obj;
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