package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class IdentificationParameters extends Parameters implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String type;

  /**
   * Retreives the value of type attribute
   * 
   * @return type
   **/

  public String getType() {
    return type;
  }

  /**
   * Sets the value of type attribue
   **/

  public void setType(final String type) {
    this.type = type;
  }

  /**
	* 	**/
  public String parametersId;

  /**
   * Retreives the value of parametersId attribute
   * 
   * @return parametersId
   **/

  public String getParametersId() {
    return parametersId;
  }

  /**
   * Sets the value of parametersId attribue
   **/

  public void setParametersId(final String parametersId) {
    this.parametersId = parametersId;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof IdentificationParameters) {
      final IdentificationParameters c = (IdentificationParameters) obj;
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