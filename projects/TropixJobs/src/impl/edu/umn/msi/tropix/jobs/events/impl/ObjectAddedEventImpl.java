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

package edu.umn.msi.tropix.jobs.events.impl;

import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.ObjectAddedEvent;

class ObjectAddedEventImpl extends EventBaseImpl implements ObjectAddedEvent {
  private final String objectId;
  private final String destinationId;
  
  ObjectAddedEventImpl(final EventBase eventBase, final String objectId, final String destinationId) {
    super(eventBase);
    this.objectId = objectId;
    this.destinationId = destinationId;
  }
    
  public String getObjectId() {
    return objectId;
  }

  public String getDestinationId() {
    return destinationId;
  }

  @Override
  protected String fieldsToString() {
    return super.fieldsToString() + ",objectId=" + objectId + ",destinationId=" + destinationId;
  }

}
