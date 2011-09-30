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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.IndexColumn;

@Entity
public class MergeScaffoldSamplesDescription extends ActivityDescription {
  private boolean mudpit;

  @OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private IdList names = new IdList(); /* Thats unfortunate, maybe IdList should be renamed to StringList */
  @OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private IdList identificationIds = new IdList();
  private boolean produceMultipleSamples;

  @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  @IndexColumn(name="id")
  private List<ScaffoldSample> samples;

  public boolean getMudpit() {
    return mudpit;
  }
  
  @Consumes
  public void setMudpit(final boolean mudpit) {
    this.mudpit = mudpit;
  }

  public IdList getNames() {
    return names;
  }
  
  @Consumes
  public void addName(final String name) {
    names.add(name);
  }
  
  public IdList getIdentificationIds() {
    return identificationIds;
  }

  public void setNames(final IdList names) {
    this.names = names;
  }

  public void setIdentificationIds(final IdList identificationIds) {
    this.identificationIds = identificationIds;
  }

  @Consumes
  public void addIdentificationId(final String identifciationId) {
    identificationIds.add(identifciationId);
  }
  
  public boolean getProduceMultipleSamples() {
    return produceMultipleSamples;
  }

  @Consumes
  public void setProduceMultipleSamples(final boolean produceMultipleSamples) {
    this.produceMultipleSamples = produceMultipleSamples;
  }

  @Produces
  public List<ScaffoldSample> getScaffoldSamples() {
    return samples;
  }

  public void setScaffoldSamples(final List<ScaffoldSample> samples) {
    this.samples = samples;
  }

}
