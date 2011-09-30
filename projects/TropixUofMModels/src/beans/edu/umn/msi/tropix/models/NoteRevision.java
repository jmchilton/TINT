package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class NoteRevision implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String creationTime;

  /**
   * Retreives the value of creationTime attribute
   * 
   * @return creationTime
   **/

  public String getCreationTime() {
    return creationTime;
  }

  /**
   * Sets the value of creationTime attribue
   **/

  public void setCreationTime(final String creationTime) {
    this.creationTime = creationTime;
  }

  /**
	* 	**/
  public String contents;

  /**
   * Retreives the value of contents attribute
   * 
   * @return contents
   **/

  public String getContents() {
    return contents;
  }

  /**
   * Sets the value of contents attribue
   **/

  public void setContents(final String contents) {
    this.contents = contents;
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
	* 	**/
  public Integer revisionNum;

  /**
   * Retreives the value of revisionNum attribute
   * 
   * @return revisionNum
   **/

  public Integer getRevisionNum() {
    return revisionNum;
  }

  /**
   * Sets the value of revisionNum attribue
   **/

  public void setRevisionNum(final Integer revisionNum) {
    this.revisionNum = revisionNum;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Note object
   **/

  private Note note;

  /**
   * Retreives the value of note attribue
   * 
   * @return note
   **/

  public Note getNote() {
    return note;
  }

  /**
   * Sets the value of note attribue
   **/

  public void setNote(final Note note) {
    this.note = note;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof NoteRevision) {
      final NoteRevision c = (NoteRevision) obj;
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