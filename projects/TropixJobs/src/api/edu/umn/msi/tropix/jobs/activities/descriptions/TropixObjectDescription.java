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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import edu.umn.msi.tropix.models.TropixObject;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class TropixObjectDescription extends ActivityDescription  implements Serializable {
  private String objectId;
  private String name;
  
  @Column(length = 4096)
  private String description;
  private String destinationId;
  private boolean committed = false;

  @Override
  protected String fieldsToString() {
    return super.fieldsToString() + ",objectId=" + objectId +",name=" + name +",description=" + description +",destinationId=" + destinationId +",committed=" + committed;
  }
  
  public String getName() {
    return name;
  }
  
  @Consumes
  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }
    
  @Consumes
  public void setDescription(final String description) {
    this.description = description;
  }
  
  public String getDestinationId() {
    return destinationId;
  }
  
  @Consumes
  public void setDestinationId(final String destinationId) {
    this.destinationId = destinationId;
  }

  @Produces
  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(final String objectId) {
    this.objectId = objectId;
  }
  
  public boolean getCommitted() {
    return committed;
  }

  @Consumes
  public void setCommitted(final boolean committed) {
    this.committed = committed;
  }

  public void init(final TropixObject tropixObject) {
    tropixObject.setName(name);
    tropixObject.setDescription(description);
    tropixObject.setCommitted(committed);
  }
  
}
