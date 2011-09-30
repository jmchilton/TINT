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

package edu.umn.msi.tropix.jobs.activities;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;

@Entity
public class ActivityContext {
  @Id
  @Nonnull
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;
  
  @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  @JoinTable(name = "CONTEXT_ACTIVITY", joinColumns = @JoinColumn(name = "CONTEXT_ID"), inverseJoinColumns = @JoinColumn(name = "ACTIVITY_ID"))
  private Set<ActivityDescription> activityDescriptions = Sets.newHashSet();
  
  @Column(columnDefinition = "clob")
  @Basic(fetch=FetchType.EAGER)
  private String credentialStr;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }
  
  public Set<ActivityDescription> getActivityDescriptions() {
    return activityDescriptions;
  }

  public void setActivityDescription(final Set<ActivityDescription> activityDescriptions) {
    this.activityDescriptions = activityDescriptions;
  }

  public String getCredentialStr() {
    return credentialStr;
  }

  public void setCredentialStr(final String credentialStr) {
    this.credentialStr = credentialStr;
  }
  
  public Credential getCredential() {
    return Credentials.fromString(credentialStr);
  }
  
  public void addDescription(final ActivityDescription activityDescription) {
    this.activityDescriptions.add(activityDescription);
  }
  
  public String toString() {
    return "ActivityContext[credential=" + credentialStr+",descriptions=[" + Iterables.toString(activityDescriptions) +"]]";
  }
  
}
