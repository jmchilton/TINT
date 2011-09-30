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

package edu.umn.msi.tropix.common.jobqueue.description;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.globus.exec.generated.JobDescriptionType;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class JobDescriptionTransformerImpl implements JobDescriptionTransformer {
  private boolean overrideExisting;
  private Integer count;
  private Long maxCpuTime;
  private Long maxWallTime;
  private Long maxTime;
  private Integer maxMemory;

  public void transform(final JobDescriptionType jobDescription) {
    if(count != null) {
      if(jobDescription.getCount() == null || overrideExisting) {
        jobDescription.setCount(new PositiveInteger("" + count));
      }
    }

    if(maxMemory != null) {
      if(jobDescription.getMaxMemory() == null || overrideExisting) {
        jobDescription.setMaxMemory(new NonNegativeInteger("" + maxMemory));
      }
    }

    if(maxCpuTime != null) {
      if(jobDescription.getMaxCpuTime() == null || overrideExisting) {
        jobDescription.setMaxCpuTime(maxCpuTime);
      }
    }

    if(maxWallTime != null) {
      if(jobDescription.getMaxWallTime() == null || overrideExisting) {
        jobDescription.setMaxWallTime(maxWallTime);
      }
    }
    if(maxTime != null) {
      if(jobDescription.getMaxTime() == null || overrideExisting) {
        jobDescription.setMaxTime(maxTime);
      }
    }
  }

  @ManagedAttribute
  public boolean isOverrideExisting() {
    return overrideExisting;
  }

  @ManagedAttribute
  public void setOverrideExisting(final boolean overrideExisting) {
    this.overrideExisting = overrideExisting;
  }

  @ManagedAttribute
  public Integer getCount() {
    return count;
  }

  @ManagedAttribute
  public void setCount(final Integer count) {
    this.count = count;
  }

  @ManagedAttribute
  public Long getMaxCpuTime() {
    return maxCpuTime;
  }

  @ManagedAttribute
  public void setMaxCpuTime(final Long maxCpuTime) {
    this.maxCpuTime = maxCpuTime;
  }

  @ManagedAttribute
  public Long getMaxWallTime() {
    return maxWallTime;
  }

  @ManagedAttribute
  public void setMaxWallTime(final Long maxWallTime) {
    this.maxWallTime = maxWallTime;
  }

  @ManagedAttribute
  public Long getMaxTime() {
    return maxTime;
  }

  @ManagedAttribute
  public void setMaxTime(final Long maxTime) {
    this.maxTime = maxTime;
  }

  @ManagedAttribute
  public Integer getMaxMemory() {
    return maxMemory;
  }

  @ManagedAttribute
  public void setMaxMemory(final Integer maxMemory) {
    this.maxMemory = maxMemory;
  }

}
