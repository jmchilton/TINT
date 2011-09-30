package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class TissueSample extends Sample implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String species;

  /**
   * Retreives the value of species attribute
   * 
   * @return species
   **/

  public String getSpecies() {
    return species;
  }

  /**
   * Sets the value of species attribue
   **/

  public void setSpecies(final String species) {
    this.species = species;
  }

  /**
	* 	**/
  public String tissueType;

  /**
   * Retreives the value of tissueType attribute
   * 
   * @return tissueType
   **/

  public String getTissueType() {
    return tissueType;
  }

  /**
   * Sets the value of tissueType attribue
   **/

  public void setTissueType(final String tissueType) {
    this.tissueType = tissueType;
  }

  /**
	* 	**/
  public String collectionTime;

  /**
   * Retreives the value of collectionTime attribute
   * 
   * @return collectionTime
   **/

  public String getCollectionTime() {
    return collectionTime;
  }

  /**
   * Sets the value of collectionTime attribue
   **/

  public void setCollectionTime(final String collectionTime) {
    this.collectionTime = collectionTime;
  }

  /**
	* 	**/
  public String condition;

  /**
   * Retreives the value of condition attribute
   * 
   * @return condition
   **/

  public String getCondition() {
    return condition;
  }

  /**
   * Sets the value of condition attribue
   **/

  public void setCondition(final String condition) {
    this.condition = condition;
  }

  /**
   * An associated edu.umn.msi.tropix.models.ProteomicsRun object's collection
   **/

  private Collection<ProteomicsRun> proteomicsRuns;

  /**
   * Retreives the value of proteomicsRuns attribue
   * 
   * @return proteomicsRuns
   **/

  public Collection<ProteomicsRun> getProteomicsRuns() {
    return proteomicsRuns;
  }

  /**
   * Sets the value of proteomicsRuns attribue
   **/

  public void setProteomicsRuns(final Collection<ProteomicsRun> proteomicsRuns) {
    this.proteomicsRuns = proteomicsRuns;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof TissueSample) {
      final TissueSample c = (TissueSample) obj;
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