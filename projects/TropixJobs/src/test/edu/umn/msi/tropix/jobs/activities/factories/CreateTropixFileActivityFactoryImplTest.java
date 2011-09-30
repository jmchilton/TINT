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

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class CreateTropixFileActivityFactoryImplTest  extends BaseCreateActivityFactoryImplTest<CreateTropixFileDescription, TropixFile> {

  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void create(final boolean useExtension) {
    final TropixObjectService tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    final FileTypeService fileTypeService = EasyMock.createMock(FileTypeService.class);
    getDescription().setExtension(useExtension ? "ext" : null);
    final String fileTypeId = UUID.randomUUID().toString();
    if(useExtension) {
      final FileType fileType = new FileType();
      fileType.setId(fileTypeId);
      EasyMock.expect(fileTypeService.loadPrimaryFileTypeWithExtension(matchId(), EasyMock.eq("ext"))).andReturn(fileType);
    }
    tropixObjectService.createFile(matchId(), matchDestinationId(), captureObject(), useExtension ? EasyMock.eq(fileTypeId) : EasyMock.<String>isNull());
    returnInitializedObject();
    EasyMock.replay(tropixObjectService, fileTypeService);
    runAndVerify(new CreateTropixFileActivityFactoryImpl(tropixObjectService, getFactorySupport(), fileTypeService));
    EasyMock.verify(tropixObjectService, fileTypeService);    
  }
  
}
