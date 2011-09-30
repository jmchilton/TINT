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

import javax.persistence.Entity;

@Entity
public class CreateDatabaseDescription extends TropixObjectDescription {
  private String databaseFileId;
  private String databaseType;
  
  public String getDatabaseFileId() {
    return databaseFileId;
  }
  
  @Consumes
  public void setDatabaseFileId(final String databaseFileId) {
    this.databaseFileId = databaseFileId;
  }
  
  public String getDatabaseType() {
    return databaseType;
  }
  
  @Consumes
  public void setDatabaseType(final String databaseType) {
    this.databaseType = databaseType;
  }
    
}
