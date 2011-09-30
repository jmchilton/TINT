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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JobDescription implements Serializable {
  @Id
  @Nonnull
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;
  
  @Nonnull
  @Column(length = 4096)
  private String name;
  
  // Needed for hibernate.
  public JobDescription() {
  }
  
  public JobDescription(final String name) {
    this.name = name;
  }
  
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  private boolean complete = false;
  
  public boolean getComplete() {
    return complete;
  }

  public void setComplete(final boolean complete) {
    this.complete = complete;
  }
  
}
