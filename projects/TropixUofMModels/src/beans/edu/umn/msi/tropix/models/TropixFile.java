package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class TropixFile extends TropixObject implements Serializable {
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
  public String fileId;

  /**
   * Retreives the value of fileId attribute
   * 
   * @return fileId
   **/

  public String getFileId() {
    return fileId;
  }

  /**
   * Sets the value of fileId attribue
   **/

  public void setFileId(final String fileId) {
    this.fileId = fileId;
  }

  /**
   * An associated edu.umn.msi.tropix.models.FileType object
   **/

  private FileType fileType;

  /**
   * Retreives the value of fileType attribue
   * 
   * @return fileType
   **/

  public FileType getFileType() {
    return fileType;
  }

  /**
   * Sets the value of fileType attribue
   **/

  public void setFileType(final FileType fileType) {
    this.fileType = fileType;
  }

  public String originalName;
  
  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof TropixFile) {
      final TropixFile c = (TropixFile) obj;
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