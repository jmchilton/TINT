package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class InternalRequest extends Request implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String storageServiceUrl;

  /**
   * Retreives the value of storageServiceUrl attribute
   * 
   * @return storageServiceUrl
   **/

  public String getStorageServiceUrl() {
    return storageServiceUrl;
  }

  /**
   * Sets the value of storageServiceUrl attribue
   **/

  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

  /**
	* 	**/
  public String requestServiceUrl;

  /**
   * Retreives the value of requestServiceUrl attribute
   * 
   * @return requestServiceUrl
   **/

  public String getRequestServiceUrl() {
    return requestServiceUrl;
  }

  /**
   * Sets the value of requestServiceUrl attribue
   **/

  public void setRequestServiceUrl(final String requestServiceUrl) {
    this.requestServiceUrl = requestServiceUrl;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Folder object
   **/

  private Folder destinationFolder;

  /**
   * Retreives the value of destinationFolder attribue
   * 
   * @return destinationFolder
   **/

  public Folder getDestinationFolder() {
    return destinationFolder;
  }

  /**
   * Sets the value of destinationFolder attribue
   **/

  public void setDestinationFolder(final Folder destinationFolder) {
    this.destinationFolder = destinationFolder;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof InternalRequest) {
      final InternalRequest c = (InternalRequest) obj;
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