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

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ActivityDependency implements Serializable {
  public static boolean specifiesProducesIndex(final ActivityDependency activityDependency) {
    return activityDependency.getIndex() != -1;
  }

  public static boolean specifiesProducesKey(final ActivityDependency activityDependency) {
    return activityDependency.getKey() != null;
  }
  
  public static boolean specifiesConsumesIndex(final ActivityDependency activityDependency) {
    return activityDependency.getConsumesIndex() != -1;
  }

  public static class Builder {
    private final ActivityDependency activityDependency = new ActivityDependency();

    public Builder(final ActivityDescription dependentActivity) {
      activityDependency.setActivityDescription(dependentActivity);
    }

    public Builder consumes(final String consumes) {
      activityDependency.setConsumerProperty(consumes);
      return this;
    }
    
    public Builder consumesWithIndex(final String consumes, final int consumesIndex) {
      activityDependency.setConsumesIndex(consumesIndex);
      return consumes(consumes); 
    }

    public Builder produces(final String produces) {
      activityDependency.setProducerProperty(produces);
      return this;
    }
    
    public Builder producesWithIndex(final String produces, final int index) {
      return produces(produces).withIndex(index);
    }
    
    public Builder withIndex(final int index) {
      activityDependency.setIndex(index);
      return this;
    }
    
    public Builder withKey(final String key) {
      activityDependency.setKey(key);
      return this;
    }

    public ActivityDependency build() {
      return activityDependency;
    }

    public static Builder on(final ActivityDescription dependentActivity) {
      return new Builder(dependentActivity);
    }

  }

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;

  @ManyToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  @JoinColumn(name = "ACTIVITY_ID", nullable = false)
  private ActivityDescription activityDescription;

  private String consumerProperty;

  private String producerProperty;

  private int index = -1;
  
  private int consumesIndex = -1;
  
  private String key = null;
  
  public int getIndex() {
    return index;
  }

  public void setIndex(final int index) {
    this.index = index;
  }

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public ActivityDescription getActivityDescription() {
    return activityDescription;
  }

  public void setActivityDescription(final ActivityDescription activityDescription) {
    this.activityDescription = activityDescription;
  }

  @Nullable
  public String getConsumerProperty() {
    return consumerProperty;
  }

  public void setConsumerProperty(final String consumerProperty) {
    this.consumerProperty = consumerProperty;
  }

  @Nullable
  public String getProducerProperty() {
    return producerProperty;
  }

  public void setProducerProperty(final String producerProperty) {
    this.producerProperty = producerProperty;
  }
  
  public int getConsumesIndex() {
    return consumesIndex;
  }

  public void setConsumesIndex(final int consumesIndex) {
    this.consumesIndex = consumesIndex;
  }

}
