/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.IndexColumn;

@Entity
public class ScaffoldSample implements Serializable {
  @Id
  @Nonnull
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private IdList identificationAnalysisIds;

  private String sampleName;

  private String category;

  private boolean analyzeAsMudpit;

  @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  @IndexColumn(name = "id")
  private List<ScaffoldQuantativeSample> quantitativeSamples;

  private String quantitativeModelType;

  private String quantitativeModelPurityCorrection;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public IdList getIdentificationAnalysisIds() {
    return identificationAnalysisIds;
  }

  public void setIdentificationAnalysisIds(final IdList identificationAnalysisIds) {
    this.identificationAnalysisIds = identificationAnalysisIds;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(final String sampleName) {
    this.sampleName = sampleName;
  }

  public boolean getAnalyzeAsMudpit() {
    return analyzeAsMudpit;
  }

  public void setAnalyzeAsMudpit(final boolean analyzeAsMudpit) {
    this.analyzeAsMudpit = analyzeAsMudpit;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(final String category) {
    this.category = category;
  }

  public List<ScaffoldQuantativeSample> getQuantitativeSamples() {
    return quantitativeSamples;
  }

  public void setQuantitativeSamples(final List<ScaffoldQuantativeSample> quantitativeSamples) {
    this.quantitativeSamples = quantitativeSamples;
  }

  public String getQuantitativeModelType() {
    return quantitativeModelType;
  }

  public void setQuantitativeModelType(final String quantitativeModelType) {
    this.quantitativeModelType = quantitativeModelType;
  }

  public String getQuantitativeModelPurityCorrection() {
    return quantitativeModelPurityCorrection;
  }

  public void setQuantitativeModelPurityCorrection(final String quantitativeModelPurityCorrection) {
    this.quantitativeModelPurityCorrection = quantitativeModelPurityCorrection;
  }

}
