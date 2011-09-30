package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class TropixObject implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

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
  public Boolean committed;

  /**
   * Retreives the value of committed attribute
   * 
   * @return committed
   **/

  public Boolean getCommitted() {
    return committed;
  }

  /**
   * Sets the value of committed attribue
   **/

  public void setCommitted(final Boolean committed) {
    this.committed = committed;
  }

  /**
	* 	**/
  public String deletedTime;

  /**
   * Retreives the value of deletedTime attribute
   * 
   * @return deletedTime
   **/

  public String getDeletedTime() {
    return deletedTime;
  }

  /**
   * Sets the value of deletedTime attribue
   **/

  public void setDeletedTime(final String deletedTime) {
    this.deletedTime = deletedTime;
  }

  /**
   * An associated edu.umn.msi.tropix.models.VirtualFolder object's collection
   **/

  private Collection<VirtualFolder> parentVirtualFolders;

  /**
   * Retreives the value of parentVirtualFolders attribue
   * 
   * @return parentVirtualFolders
   **/

  public Collection<VirtualFolder> getParentVirtualFolders() {
    return parentVirtualFolders;
  }

  /**
   * Sets the value of parentVirtualFolders attribue
   **/

  public void setParentVirtualFolders(final Collection<VirtualFolder> parentVirtualFolders) {
    this.parentVirtualFolders = parentVirtualFolders;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixObject object's collection
   **/

  private Collection<TropixObject> permissionParents;

  public Collection<TropixObject> getPermissionParents() {
    return permissionParents;
  }

  public void setPermissionParents(final Collection<TropixObject> permissionParents) {
    this.permissionParents = permissionParents;
  }

  private Collection<TropixObject> permissionChildren;

  /**
   * Retreives the value of permissionChildren attribue
   * 
   * @return permissionChildren
   **/

  public Collection<TropixObject> getPermissionChildren() {
    return permissionChildren;
  }

  /**
   * Sets the value of permissionChildren attribue
   **/

  public void setPermissionChildren(final Collection<TropixObject> permissionChildren) {
    this.permissionChildren = permissionChildren;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Folder object
   **/

  private Folder parentFolder;

  /**
   * Retreives the value of parentFolder attribue
   * 
   * @return parentFolder
   **/

  public Folder getParentFolder() {
    return parentFolder;
  }

  /**
   * Sets the value of parentFolder attribue
   **/

  public void setParentFolder(final Folder parentFolder) {
    this.parentFolder = parentFolder;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Permission object's collection
   **/

  private Collection<Permission> permissions;

  /**
   * Retreives the value of permissions attribue
   * 
   * @return permissions
   **/

  public Collection<Permission> getPermissions() {
    return permissions;
  }

  /**
   * Sets the value of permissions attribue
   **/

  public void setPermissions(final Collection<Permission> permissions) {
    this.permissions = permissions;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Study object's collection
   **/

  private Collection<Study> studies;

  /**
   * Retreives the value of studies attribue
   * 
   * @return studies
   **/

  public Collection<Study> getStudies() {
    return studies;
  }

  /**
   * Sets the value of studies attribue
   **/

  public void setStudies(final Collection<Study> studies) {
    this.studies = studies;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof TropixObject) {
      final TropixObject c = (TropixObject) obj;
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