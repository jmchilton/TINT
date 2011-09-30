package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Note extends TropixObject implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.NoteRevision object's collection
   **/

  private Collection<NoteRevision> revisions;

  /**
   * Retreives the value of revisions attribue
   * 
   * @return revisions
   **/

  public Collection<NoteRevision> getRevisions() {
    return revisions;
  }

  /**
   * Sets the value of revisions attribue
   **/

  public void setRevisions(final Collection<NoteRevision> revisions) {
    this.revisions = revisions;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Note) {
      final Note c = (Note) obj;
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