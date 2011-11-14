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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.jobs.events.impl.EventSupport;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class FactorySupportImplTest {

  @Test(groups = "unit")
  public void testGetters() {
    final PersistentModelStorageDataFactory storageDataFactory = EasyMock.createMock(PersistentModelStorageDataFactory.class);
    final EventSupport eventSupport = EasyMock.createMock(EventSupport.class);
    final TropixObjectService tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    final FactorySupportImpl support = new FactorySupportImpl(storageDataFactory, eventSupport, tropixObjectService);

    assert support.getStorageDataFactory() == storageDataFactory;
    assert support.getEventSupport() == eventSupport;
    assert support.getTropixObjectDeleter() == tropixObjectService;
    assert support.getTropixObjectService() == tropixObjectService;  
  }
  
}
