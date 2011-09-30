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

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;

/*
 * This is an interface in search of package that makes sense. It used to be in
 * with the Web Gui, but during the refactoring for workflow it made sense to move it
 * into the Jobs code. It might also make sense in StorageClient but I would prefer 
 * that module not have explicit dependencies on TropixPersistence.
 */
/**
 * Adds a method to {@link ModelStorageDataFactory} that obtains a {@link ModelStorageData}
 * object from an object id.
 * 
 * @author John Chilton 
 *
 */
public interface PersistentModelStorageDataFactory extends ModelStorageDataFactory {
  /**
   * 
   * @param storedDataId Id of {@link edu.umn.msi.tropix.models.TropixFile} object in metadata store.
   * @param credential {@link Credential} corresponding to the user who this action is being request on 
   * behalf of.
   * @return A {@link ModelStorageData} backed by the metadata store.
   */
  ModelStorageData getPersistedStorageData(String storedDataId, Credential credential);
}
