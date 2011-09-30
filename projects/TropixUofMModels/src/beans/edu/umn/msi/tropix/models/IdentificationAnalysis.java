package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

/**
	* 	**/
public class IdentificationAnalysis extends Analysis implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String identificationProgram;

  /**
   * Retreives the value of identificationProgram attribute
   * 
   * @return identificationProgram
   **/

  public String getIdentificationProgram() {
    return identificationProgram;
  }

  /**
   * Sets the value of identificationProgram attribue
   **/

  public void setIdentificationProgram(final String identificationProgram) {
    this.identificationProgram = identificationProgram;
  }

  /**
   * An associated edu.umn.msi.tropix.models.Database object
   **/

  private Database database;

  /**
   * Retreives the value of database attribue
   * 
   * @return database
   **/

  public Database getDatabase() {
    return database;
  }

  /**
   * Sets the value of database attribue
   **/

  public void setDatabase(final Database database) {
    this.database = database;
  }

  /**
   * An associated edu.umn.msi.tropix.models.ProteomicsRun object
   **/

  private ProteomicsRun run;

  /**
   * Retreives the value of run attribue
   * 
   * @return run
   **/

  public ProteomicsRun getRun() {
    return run;
  }

  /**
   * Sets the value of run attribue
   **/

  public void setRun(final ProteomicsRun run) {
    this.run = run;
  }

  /**
   * An associated edu.umn.msi.tropix.models.ScaffoldAnalysis object's collection
   **/

  private Collection<ScaffoldAnalysis> scaffoldAnalyses;

  /**
   * Retreives the value of scaffoldAnalyses attribue
   * 
   * @return scaffoldAnalyses
   **/

  public Collection<ScaffoldAnalysis> getScaffoldAnalyses() {
    return scaffoldAnalyses;
  }

  /**
   * Sets the value of scaffoldAnalyses attribue
   **/

  public void setScaffoldAnalyses(final Collection<ScaffoldAnalysis> scaffoldAnalyses) {
    this.scaffoldAnalyses = scaffoldAnalyses;
  }

  /**
   * An associated edu.umn.msi.tropix.models.TropixFile object
   **/

  private TropixFile output;

  /**
   * Retreives the value of output attribue
   * 
   * @return output
   **/

  public TropixFile getOutput() {
    return output;
  }

  /**
   * Sets the value of output attribue
   **/

  public void setOutput(final TropixFile output) {
    this.output = output;
  }

  /**
   * An associated edu.umn.msi.tropix.models.IdentificationParameters object
   **/

  private IdentificationParameters parameters;

  /**
   * Retreives the value of parameters attribue
   * 
   * @return parameters
   **/

  public IdentificationParameters getParameters() {
    return parameters;
  }

  /**
   * Sets the value of parameters attribue
   **/

  public void setParameters(final IdentificationParameters parameters) {
    this.parameters = parameters;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof IdentificationAnalysis) {
      final IdentificationAnalysis c = (IdentificationAnalysis) obj;
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