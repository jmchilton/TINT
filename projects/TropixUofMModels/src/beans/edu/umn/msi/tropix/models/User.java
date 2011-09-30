package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class User implements Serializable {
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
  public String firstName;

  /**
   * Retreives the value of firstName attribute
   * 
   * @return firstName
   **/

  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the value of firstName attribue
   **/

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  /**
	* 	**/
  public String lastName;

  /**
   * Retreives the value of lastName attribute
   * 
   * @return lastName
   **/

  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the value of lastName attribue
   **/

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  /**
	* 	**/
  public String cagridId;

  /**
   * Retreives the value of cagridId attribute
   * 
   * @return cagridId
   **/

  public String getCagridId() {
    return cagridId;
  }

  /**
   * Sets the value of cagridId attribue
   **/

  public void setCagridId(final String cagridId) {
    this.cagridId = cagridId;
  }

  /**
	* 	**/
  public String email;

  /**
   * Retreives the value of email attribute
   * 
   * @return email
   **/

  public String getEmail() {
    return email;
  }

  /**
   * Sets the value of email attribue
   **/

  public void setEmail(final String email) {
    this.email = email;
  }

  /**
	* 	**/
  public String phone;

  /**
   * Retreives the value of phone attribute
   * 
   * @return phone
   **/

  public String getPhone() {
    return phone;
  }

  /**
   * Sets the value of phone attribue
   **/

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Folder object
   **/

  private Folder homeFolder;

  /**
   * Retreives the value of homeFolder attribue
   * 
   * @return homeFolder
   **/

  public Folder getHomeFolder() {
    return homeFolder;
  }

  /**
   * Sets the value of homeFolder attribue
   **/

  public void setHomeFolder(final Folder homeFolder) {
    this.homeFolder = homeFolder;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Parameters object's collection
   **/

  private Collection<Parameters> parameters;

  /**
   * Retreives the value of parameters attribue
   * 
   * @return parameters
   **/

  public Collection<Parameters> getParameters() {
    return parameters;
  }

  /**
   * Sets the value of parameters attribue
   **/

  public void setParameters(final Collection<Parameters> parameters) {
    this.parameters = parameters;
  }

  /**
   * An associated edu.umn.msi.tropix.models.LogMessage object's collection
   **/

  private Collection<LogMessage> messages;

  /**
   * Retreives the value of messages attribue
   * 
   * @return messages
   **/

  public Collection<LogMessage> getMessages() {
    return messages;
  }

  /**
   * Sets the value of messages attribue
   **/

  public void setMessages(final Collection<LogMessage> messages) {
    this.messages = messages;
  }

  /**
   * An associated edu.umn.msi.tropix.models.VirtualFolder object's collection
   **/

  private Collection<VirtualFolder> sharedFolders;

  /**
   * Retreives the value of sharedFolders attribue
   * 
   * @return sharedFolders
   **/

  public Collection<VirtualFolder> getSharedFolders() {
    return sharedFolders;
  }

  /**
   * Sets the value of sharedFolders attribue
   **/

  public void setSharedFolders(final Collection<VirtualFolder> sharedFolders) {
    this.sharedFolders = sharedFolders;
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
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof User) {
      final User c = (User) obj;
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