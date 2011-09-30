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

import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.persistence.service.FolderService;

public class CreateFolderActivityFactoryImplTest extends BaseCreateActivityFactoryImplTest<CreateFolderDescription, Folder> {

  @Test(groups = "unit")
  public void testRun() {
    final FolderService folderService = EasyMock.createMock(FolderService.class);
    folderService.createFolder(matchId(), matchDestinationId(), captureObject());
    returnInitializedObject();
    EasyMock.replay(folderService);
    runAndVerify(new CreateFolderActivityFactoryImpl(folderService, getFactorySupport()));
    EasyMock.verify(folderService);
  }
}
