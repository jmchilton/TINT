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

package edu.umn.msi.tropix.files.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;

@ManagedBean
@Named("persistentStorageDataFactory")
class PersistentGridDataFactoryImpl implements PersistentModelStorageDataFactory {
  private static final Log LOG = LogFactory.getLog(PersistentGridDataFactoryImpl.class);
  private final ModelStorageDataFactory gridDataFactory;
  private final FileService fileService;
  private final TropixObjectLoaderService tropixObjectService;

  @Inject
  PersistentGridDataFactoryImpl(final ModelStorageDataFactory gridDataFactory, final TropixObjectLoaderService tropixObjectService,
      final FileService fileService) {
    LOG.debug("Instantiating persistentStorageDataFactory");
    this.gridDataFactory = gridDataFactory;
    this.tropixObjectService = tropixObjectService;
    this.fileService = fileService;
  }

  public ModelStorageData getPersistedStorageData(final String tropixFileId, final Credential proxy) {
    LOG.debug("Attempting to load TropixFile with id " + tropixFileId);
    final TropixFile tropixFile = (TropixFile) this.tropixObjectService.load(proxy.getIdentity(), tropixFileId, TropixObjectTypeEnum.FILE);
    if(tropixFile == null) {
      throw new IllegalArgumentException("Unknown TropixFileId - " + tropixFileId);
    }
    LOG.debug(String.format("TropixFile obtained with fileId [%s]", tropixFile.getFileId()));
    return this.gridDataFactory.getStorageData(tropixFile, proxy);
  }

  private void initialize(final ModelStorageData modelStorageData, final Credential proxy) {
  }

  public ModelStorageData getStorageData(final String serviceUrl, final Credential proxy) {
    final ModelStorageData modelStorageData = this.gridDataFactory.getStorageData(serviceUrl, proxy);
    initialize(modelStorageData, proxy);
    return modelStorageData;
  }

  public ModelStorageData getStorageData(final String dataIdentifier, final String serviceUrl, final Credential proxy) {
    final ModelStorageData modelStorageData = this.gridDataFactory.getStorageData(dataIdentifier, serviceUrl, proxy);
    initialize(modelStorageData, proxy);
    return modelStorageData;
  }

  public ModelStorageData getStorageData(final TropixFile file, final Credential proxy) {
    final ModelStorageData modelStorageData = this.gridDataFactory.getStorageData(file, proxy);
    initialize(modelStorageData, proxy);
    return modelStorageData;
  }

}
