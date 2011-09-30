package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Group implements Serializable {
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
   * An associated edu.umn.msi.tropix.models.User object's collection
   **/

  private Collection<User> users;

  /**
   * Retreives the value of users attribue
   * 
   * @return users
   **/

  public Collection<User> getUsers() {
    return users;
  }

  /**
   * Sets the value of users attribue
   **/

  public void setUsers(final Collection<User> users) {
    this.users = users;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Group) {
      final Group c = (Group) obj;
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