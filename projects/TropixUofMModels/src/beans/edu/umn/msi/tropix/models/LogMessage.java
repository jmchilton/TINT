package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class LogMessage implements Serializable {
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
   * An associated edu.umn.msi.tropix.models.User object
   **/

  private User user;

  /**
   * Retreives the value of user attribue
   * 
   * @return user
   **/

  public User getUser() {
    return user;
  }

  /**
   * Sets the value of user attribue
   **/

  public void setUser(final User user) {
    this.user = user;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof LogMessage) {
      final LogMessage c = (LogMessage) obj;
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