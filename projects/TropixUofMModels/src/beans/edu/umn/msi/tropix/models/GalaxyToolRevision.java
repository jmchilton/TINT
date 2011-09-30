package edu.umn.msi.tropix.models;

import java.io.Serializable;

/**
	* 	**/
public class GalaxyToolRevision implements Serializable {
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
  public Long revisionNum;

  /**
   * Retreives the value of revisionNum attribute
   * 
   * @return revisionNum
   **/

  public Long getRevisionNum() {
    return revisionNum;
  }

  /**
   * Sets the value of revisionNum attribue
   **/

  public void setRevisionNum(final Long revisionNum) {
    this.revisionNum = revisionNum;
  }

  /**
	* 	**/
  public String xml;

  /**
   * Retreives the value of xml attribute
   * 
   * @return xml
   **/

  public String getXml() {
    return xml;
  }

  /**
   * Sets the value of xml attribue
   **/

  public void setXml(final String xml) {
    this.xml = xml;
  }

  /**
   * An associated edu.umn.msi.tropix.models.GalaxyTool object
   **/

  private GalaxyTool tool;

  /**
   * Retreives the value of tool attribue
   * 
   * @return tool
   **/

  public GalaxyTool getTool() {
    return tool;
  }

  /**
   * Sets the value of tool attribue
   **/

  public void setTool(final GalaxyTool tool) {
    this.tool = tool;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof GalaxyToolRevision) {
      final GalaxyToolRevision c = (GalaxyToolRevision) obj;
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