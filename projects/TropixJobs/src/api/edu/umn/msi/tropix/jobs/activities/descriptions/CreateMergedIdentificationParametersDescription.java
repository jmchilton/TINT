package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.IndexColumn;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class CreateMergedIdentificationParametersDescription extends ActivityDescription implements ConsumesStorageServiceUrl {
  private String driverFileId;
  private String storageServiceUrl;
  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private IdList identificationAnalysisIds;
  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private IdList databaseIds;
  @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  @IndexColumn(name = "id")
  private List<ScaffoldSample> scaffoldSamples = new LinkedList<ScaffoldSample>();
  @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private StringParameterSet parameterSet;

  public String getStorageServiceUrl() {
    return storageServiceUrl;
  }

  @Consumes
  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

  @Produces
  public String getDriverFileId() {
    return driverFileId;
  }

  public void setDriverFileId(final String driverFileId) {
    this.driverFileId = driverFileId;
  }

  @Produces
  public IdList getIdentificationAnalysisIds() {
    return identificationAnalysisIds;
  }

  public void setIdentificationAnalysisIds(final IdList identificationAnalysisIds) {
    this.identificationAnalysisIds = identificationAnalysisIds;
  }

  @Produces
  public IdList getDatabaseIds() {
    return databaseIds;
  }

  public void setDatabaseIds(final IdList databasesIds) {
    this.databaseIds = databasesIds;
  }

  public List<ScaffoldSample> getScaffoldSamples() {
    return scaffoldSamples;
  }

  @Consumes
  public void setScaffoldSamples(final List<ScaffoldSample> scaffoldSamples) {
    this.scaffoldSamples = scaffoldSamples;
  }

  public void addScaffoldSample(final ScaffoldSample scaffoldSample) {
    scaffoldSamples.add(scaffoldSample);
  }

  public StringParameterSet getParameterSet() {
    return parameterSet;
  }

  @Consumes
  public void setParameterSet(final StringParameterSet parameterSet) {
    this.parameterSet = parameterSet;
  }

}