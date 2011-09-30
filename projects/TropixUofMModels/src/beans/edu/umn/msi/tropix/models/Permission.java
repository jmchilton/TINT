package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Permission implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String role;

  /**
   * Retreives the value of role attribute
   * 
   * @return role
   **/

  public String getRole() {
    return role;
  }

  /**
   * Sets the value of role attribue
   **/

  public void setRole(final String role) {
    this.role = role;
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
   * An associated edu.umn.msi.tropix.models.Group object's collection
   **/

  private Collection<Group> groups;

  /**
   * Retreives the value of groups attribue
   * 
   * @return groups
   **/

  public Collection<Group> getGroups() {
    return groups;
  }

  /**
   * Sets the value of groups attribue
   **/

  public void setGroups(final Collection<Group> groups) {
    this.groups = groups;
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
   * An associated edu.umn.msi.tropix.models.TropixObject object's collection
   **/

  private Collection<TropixObject> objects;

  /**
   * Retreives the value of objects attribue
   * 
   * @return objects
   **/

  public Collection<TropixObject> getObjects() {
    return objects;
  }

  /**
   * Sets the value of objects attribue
   **/

  public void setObjects(final Collection<TropixObject> objects) {
    this.objects = objects;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Permission) {
      final Permission c = (Permission) obj;
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