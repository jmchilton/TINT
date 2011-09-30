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

package edu.umn.msi.tropix.files;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.test.MockTropixObjectLoaderServiceImpl;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.test.MockModelStorageDataFactoryImpl;

public class MockPersistentModelStorageDataFactoryImpl extends MockModelStorageDataFactoryImpl implements PersistentModelStorageDataFactory {

  public MockPersistentModelStorageDataFactoryImpl() {    
  }
  
  public MockPersistentModelStorageDataFactoryImpl(final MockTropixObjectLoaderServiceImpl objectLoader) {
    objectLoader.registerSaveListener(new Closure<TropixObject>() {
      public void apply(final TropixObject object) {
        if(object instanceof TropixFile) {
          register((TropixFile) object);
        }
      }      
    });
  }

  public void register(final TropixFile tropixFile) {
    Preconditions.checkNotNull(tropixFile.getId());
    Preconditions.checkNotNull(tropixFile.getFileId());
    Preconditions.checkNotNull(tropixFile.getStorageServiceUrl());
    idMap.put(tropixFile.getId(), tropixFile.getFileId());
    serviceMap.put(tropixFile.getId(), tropixFile.getStorageServiceUrl());
  }
  
  private Map<String, String> idMap = Maps.newHashMap();
  private Map<String, String> serviceMap = Maps.newHashMap();
  
  public ModelStorageData getPersistedStorageData(final String storedDataId, final Credential credential) {
    final String id = idMap.get(storedDataId);
    final String service = serviceMap.get(storedDataId);
    return getStorageData(id, service, credential);
  }
     
}
