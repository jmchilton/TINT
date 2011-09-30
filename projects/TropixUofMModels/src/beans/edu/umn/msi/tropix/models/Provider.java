package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Provider extends Permission implements Serializable {
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

  @Override
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id attribue
   **/

  @Override
  public void setId(final String id) {
    this.id = id;
  }

  /**
	* 	**/
  public String catalogId;

  /**
   * Retreives the value of catalogId attribute
   * 
   * @return catalogId
   **/

  public String getCatalogId() {
    return catalogId;
  }

  /**
   * Sets the value of catalogId attribue
   **/

  public void setCatalogId(final String catalogId) {
    this.catalogId = catalogId;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Request object's collection
   **/

  private Collection<Request> requests;

  /**
   * Retreives the value of requests attribue
   * 
   * @return requests
   **/

  public Collection<Request> getRequests() {
    return requests;
  }

  /**
   * Sets the value of requests attribue
   **/

  public void setRequests(final Collection<Request> requests) {
    this.requests = requests;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Provider) {
      final Provider c = (Provider) obj;
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