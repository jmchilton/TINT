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

import java.util.Collection;

import org.easymock.EasyMock;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.files.MockPersistentModelStorageDataFactoryImpl;
import edu.umn.msi.tropix.jobs.events.impl.EventSupport;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.TropixObjectDeleter;
import edu.umn.msi.tropix.persistence.service.test.MockTropixObjectLoaderServiceImpl;

class MockFactorySupportImpl implements FactorySupport {
  private final EventSupport eventSupport = EasyMock.createMock(EventSupport.class);
  private final MockTropixObjectLoaderServiceImpl tropixObjectService = new MockTropixObjectLoaderServiceImpl();
  private final MockPersistentModelStorageDataFactoryImpl storageDataFactory = new MockPersistentModelStorageDataFactoryImpl(tropixObjectService);
  private final Collection<String> deletedIds = Lists.newLinkedList();

  private final MockObjectCollection mocks = MockObjectCollection.fromObjects(eventSupport);

  MockFactorySupportImpl() {
  }

  public EventSupport getEventSupport() {
    return eventSupport;
  }

  public MockPersistentModelStorageDataFactoryImpl getStorageDataFactory() {
    return storageDataFactory;
  }

  public MockTropixObjectLoaderServiceImpl getTropixObjectService() {
    return tropixObjectService;
  }

  public Collection<Object> getMockObjects() {
    return mocks;
  }

  void replay() {
    mocks.replay();
  }

  void verifyAndReset() {
    mocks.verifyAndReset();
  }

  void saveObject(final TropixObject object) {
    tropixObjectService.saveObject(object);
  }

  void saveObjects(final Iterable<? extends TropixObject> objects) {
    tropixObjectService.saveObjects(objects);
  }

  void saveObjects(final TropixObject... objects) {
    tropixObjectService.saveObjects(objects);
  }

  public TropixObjectDeleter getTropixObjectDeleter() {
    return new TropixObjectDeleter() {
      public void delete(final String cagridId, final String id) {
        Preconditions.checkNotNull(cagridId);
        deletedIds.add(id);
      }
    };
  }

  void assertDeleted(final String id) {
    assert deletedIds.contains(id);
  }

}
