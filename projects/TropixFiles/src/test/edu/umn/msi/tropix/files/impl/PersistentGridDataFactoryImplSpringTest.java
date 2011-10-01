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

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.UserService;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

/**
 * This test ensures that file outlines get created with consistent permissions, etc...
 * when ModelStorageData objects are created and that the code in TropixStorageCore respects
 * these models and updates fileSize appropriately.
 * 
 * @author John Chilton
 * 
 */
@ContextConfiguration
public class PersistentGridDataFactoryImplSpringTest extends FreshConfigTest {

  @Inject
  private PersistentGridDataFactoryImpl factory;

  @Inject
  private UserService userService;

  @Inject
  private TropixObjectService tropixObjectService;

  @Inject
  private FileService fileService;
  
  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {
    final ConfigDirBuilder metadataBuilder = configDirBuilder.createSubConfigDir("metadata");
    metadataBuilder.addDeployProperty("metadata.db.showsql", "true");
  }

  private void assertNullOrFalse(final Boolean value) {
    assert value == null || !value;
  }

  /**
   * See class description.
   */
  @Test(groups = "spring", timeOut = 30000)
  public void testCreate() {
    final String userId = UUID.randomUUID().toString();
    final String dataId1 = UUID.randomUUID().toString(), dataId2 = UUID.randomUUID().toString();
    userService.createOrGetUser(userId);

    final Credential credential = Credentials.getMock(userId);
    final ModelStorageData data = factory.getStorageData((String) null, credential);
    data.getUploadContext().put("Hello World".getBytes());
    assert InputContexts.toString(data.getDownloadContext()).equals("Hello World");
    tropixObjectService.createFile(userId, null, data.getTropixFile(), null);
    assert fileService.loadPhysicalFile(data.getTropixFile().getFileId()).getSize() > 0;
    assertNullOrFalse(tropixObjectService.loadFileWithFileId(userId, data.getTropixFile().getFileId()).getCommitted());

    final TropixFile file1 = new TropixFile();
    file1.setFileId(dataId1);
    final ModelStorageData data1 = factory.getStorageData(file1, credential);
    data1.getUploadContext().put("Hello World".getBytes());
    assert InputContexts.toString(data1.getDownloadContext()).equals("Hello World");
    tropixObjectService.createFile(userId, null, file1, null);
    assert fileService.loadPhysicalFile(file1.getFileId()).getSize() > 0;

    final ModelStorageData data2 = factory.getStorageData(dataId2, null, credential);
    data2.getUploadContext().put("Hello World".getBytes());
    assert InputContexts.toString(data2.getDownloadContext()).equals("Hello World");
    tropixObjectService.createFile(userId, null, data2.getTropixFile(), null);
    assert fileService.loadPhysicalFile(data2.getTropixFile().getFileId()).getSize() > 0;

    final TropixFile file2 = new TropixFile();
    file2.setFileId(UUID.randomUUID().toString());
    file2.setName("Moo");
    final TropixFile loadedFile2 = tropixObjectService.createFile(userId, null, file2, null);

    // Load object by id instead of fileId.
    final ModelStorageData data3 = factory.getPersistedStorageData(loadedFile2.getId(), credential);
    data3.getUploadContext().put("Hello World".getBytes());
    assert InputContexts.toString(data3.getDownloadContext()).equals("Hello World");
    assert data3.getTropixFile().getName().equals("Moo");
    assert fileService.loadPhysicalFile(data3.getTropixFile().getFileId()).getSize() > 0;
  }

}
