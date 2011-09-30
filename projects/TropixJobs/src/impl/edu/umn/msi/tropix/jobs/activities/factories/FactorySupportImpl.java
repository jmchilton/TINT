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

package edu.umn.msi.tropix.jobs.activities.factories;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.jobs.events.impl.EventSupport;
import edu.umn.msi.tropix.persistence.service.TropixObjectDeleter;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

@ManagedBean
class FactorySupportImpl implements FactorySupport {
  private final PersistentModelStorageDataFactory storageDataFactory;
  private final EventSupport eventSupport;
  private final TropixObjectService tropixObjectService;
  
  @Inject
  FactorySupportImpl(final PersistentModelStorageDataFactory storageDataFactory,
                     final EventSupport eventSupport,
                     final TropixObjectService tropixObjectService) {
    this.storageDataFactory = storageDataFactory;
    this.eventSupport = eventSupport;
    this.tropixObjectService = tropixObjectService;
  }
  
  public EventSupport getEventSupport() {
    return eventSupport;
  }

  public PersistentModelStorageDataFactory getStorageDataFactory() {
    return storageDataFactory;
  }

  public TropixObjectLoaderService getTropixObjectService() {
    return tropixObjectService;
  }

  public TropixObjectDeleter getTropixObjectDeleter() {
    return tropixObjectService;
  }
}
