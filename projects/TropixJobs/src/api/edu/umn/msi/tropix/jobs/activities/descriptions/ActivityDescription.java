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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class ActivityDescription  implements Serializable {
  @Id
  @Nonnull
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;

  @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private Set<ActivityDependency> dependencies = new HashSet<ActivityDependency>();

  @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private JobDescription jobDescription;

  private ActivityStatus activityStatus = ActivityStatus.WAITING;

  protected String fieldsToString() {
    return "id=" + id;
  }
  
  public String toString() {
    return getClass().getName() +"[" + fieldsToString() + "]";
  }
  
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Set<ActivityDependency> getDependencies() {
    return dependencies;
  }

  public void setDependencies(final Set<ActivityDependency> dependencies) {
    this.dependencies = dependencies;
  }
  
  public void addDependency(final ActivityDependency dependency) {
    dependencies.add(dependency);
  }

  public JobDescription getJobDescription() {
    return jobDescription;
  }

  public void setJobDescription(final JobDescription jobDescription) {
    this.jobDescription = jobDescription;
  }
  
  public ActivityStatus getActivityStatus() {
    return activityStatus;
  }

  public void setActivityStatus(final ActivityStatus activityStatus) {
    this.activityStatus = activityStatus;
  }
  
}
