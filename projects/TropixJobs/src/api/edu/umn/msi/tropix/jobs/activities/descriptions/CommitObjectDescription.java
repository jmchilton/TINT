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

import javax.annotation.Nullable;
import javax.persistence.Entity;

@Entity @DescriptionMessageCode(MessageCodeConstants.COMMITTING)
public class CommitObjectDescription extends ActivityDescription {
  private String objectId;
  private String destinationId;
  
  public String getObjectId() {
    return objectId;
  }
  
  @Consumes
  public void setObjectId(final String objectId) {
    this.objectId = objectId;
  }

  @Nullable
  public String getDestinationId() {
    return destinationId;
  }

  @Consumes
  public void setDestinationId(@Nullable final String destinationId) {
    this.destinationId = destinationId;
  }
  
  

}
