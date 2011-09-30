package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class Request extends TropixObject implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String externalId;

  /**
   * Retreives the value of externalId attribute
   * 
   * @return externalId
   **/

  public String getExternalId() {
    return externalId;
  }

  /**
   * Sets the value of externalId attribue
   **/

  public void setExternalId(final String externalId) {
    this.externalId = externalId;
  }

  /**
	* 	**/
  public String requestorId;

  /**
   * Retreives the value of requestorId attribute
   * 
   * @return requestorId
   **/

  public String getRequestorId() {
    return requestorId;
  }

  /**
   * Sets the value of requestorId attribue
   **/

  public void setRequestorId(final String requestorId) {
    this.requestorId = requestorId;
  }

  /**
	* 	**/
  public String serviceId;

  /**
   * Retreives the value of serviceId attribute
   * 
   * @return serviceId
   **/

  public String getServiceId() {
    return serviceId;
  }

  /**
   * Sets the value of serviceId attribue
   **/

  public void setServiceId(final String serviceId) {
    this.serviceId = serviceId;
  }

  /**
	* 	**/
  public String contact;

  /**
   * Retreives the value of contact attribute
   * 
   * @return contact
   **/

  public String getContact() {
    return contact;
  }

  /**
   * Sets the value of contact attribue
   **/

  public void setContact(final String contact) {
    this.contact = contact;
  }

  /**
	* 	**/
  public String destination;

  /**
   * Retreives the value of destination attribute
   * 
   * @return destination
   **/

  public String getDestination() {
    return destination;
  }

  /**
   * Sets the value of destination attribue
   **/

  public void setDestination(final String destination) {
    this.destination = destination;
  }

  /**
	* 	**/
  public String serviceInfo;

  /**
   * Retreives the value of serviceInfo attribute
   * 
   * @return serviceInfo
   **/

  public String getServiceInfo() {
    return serviceInfo;
  }

  /**
   * Sets the value of serviceInfo attribue
   **/

  public void setServiceInfo(final String serviceInfo) {
    this.serviceInfo = serviceInfo;
  }

  /**
	* 	**/
  public String state;

  /**
   * Retreives the value of state attribute
   * 
   * @return state
   **/

  public String getState() {
    return state;
  }

  /**
   * Sets the value of state attribue
   **/

  public void setState(final String state) {
    this.state = state;
  }

  /**
	* 	**/
  public String report;

  /**
   * Retreives the value of report attribute
   * 
   * @return report
   **/

  public String getReport() {
    return report;
  }

  /**
   * Sets the value of report attribue
   **/

  public void setReport(final String report) {
    this.report = report;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixObject object's collection
   **/

  private Collection<TropixObject> contents;

  /**
   * Retreives the value of contents attribue
   * 
   * @return contents
   **/

  public Collection<TropixObject> getContents() {
    return contents;
  }

  /**
   * Sets the value of contents attribue
   **/

  public void setContents(final Collection<TropixObject> contents) {
    this.contents = contents;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Provider object
   **/

  private Provider provider;

  /**
   * Retreives the value of provider attribue
   * 
   * @return provider
   **/

  public Provider getProvider() {
    return provider;
  }

  /**
   * Sets the value of provider attribue
   **/

  public void setProvider(final Provider provider) {
    this.provider = provider;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Request) {
      final Request c = (Request) obj;
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