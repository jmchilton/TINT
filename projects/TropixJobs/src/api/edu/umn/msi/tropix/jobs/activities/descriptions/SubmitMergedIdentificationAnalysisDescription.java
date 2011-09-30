package edu.umn.msi.tropix.jobs.activities.descriptions;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class SubmitMergedIdentificationAnalysisDescription extends SubmitJobDescription {

  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private IdList identificationIds;
  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private IdList databaseIds;
  private String driverFileId;

  public SubmitMergedIdentificationAnalysisDescription() {
    super();
  }

  public IdList getIdentificationIds() {
    return identificationIds;
  }

  @Consumes
  public void setIdentificationIds(final IdList identificationIds) {
    this.identificationIds = identificationIds;
  }

  public IdList getDatabaseIds() {
    return databaseIds;
  }

  @Consumes
  public void setDatabaseIds(final IdList databaseIds) {
    this.databaseIds = databaseIds;
  }

  public String getDriverFileId() {
    return driverFileId;
  }

  @Consumes
  public void setDriverFileId(final String driverFileId) {
    this.driverFileId = driverFileId;
  }

}