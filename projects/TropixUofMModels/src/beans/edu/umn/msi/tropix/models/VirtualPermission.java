package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class VirtualPermission extends Permission implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
   * An associated edu.umn.msi.tropix.models.VirtualFolder object
   **/

  private VirtualFolder rootVirtualFolder;

  /**
   * Retreives the value of rootVirtualFolder attribue
   * 
   * @return rootVirtualFolder
   **/

  public VirtualFolder getRootVirtualFolder() {
    return rootVirtualFolder;
  }

  /**
   * Sets the value of rootVirtualFolder attribue
   **/

  public void setRootVirtualFolder(final VirtualFolder rootVirtualFolder) {
    this.rootVirtualFolder = rootVirtualFolder;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof VirtualPermission) {
      final VirtualPermission c = (VirtualPermission) obj;
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