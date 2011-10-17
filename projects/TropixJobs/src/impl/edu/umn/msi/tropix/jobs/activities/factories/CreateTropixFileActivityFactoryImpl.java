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

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.files.creator.TropixFileCreator;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.jobs.activities.impl.Revertable;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileTypeService;

@ManagedBean @ActivityFactoryFor(CreateTropixFileDescription.class)
class CreateTropixFileActivityFactoryImpl implements ActivityFactory<CreateTropixFileDescription> {
  private final TropixFileCreator fileCreator;
  private final FileTypeService fileTypeService;
  private final FactorySupport factorySupport;
  
  @Inject
  CreateTropixFileActivityFactoryImpl(final TropixFileCreator fileCreator, final FactorySupport factorySupport, final FileTypeService fileTypeService) {
    this.fileCreator = fileCreator;
    this.factorySupport = factorySupport;
    this.fileTypeService = fileTypeService;
  }

  private class CreateActivity extends BaseTropixObjectJobActivityImpl<CreateTropixFileDescription, TropixFile> implements Activity, Revertable {    
    CreateActivity(final CreateTropixFileDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    } 
    
    public void run() throws ShutdownException {
      final TropixFile tropixFile = getModelObject();
      tropixFile.setStorageServiceUrl(getDescription().getStorageServiceUrl());
      tropixFile.setFileId(getDescription().getFileId());
      final String extension = getDescription().getExtension();
      final String fileTypeId = extension == null ? null : fileTypeService.loadPrimaryFileTypeWithExtension(getUserId(), extension).getId();
      updateId(fileCreator.createFile(getCredential(), getDescription().getDestinationId(), tropixFile, fileTypeId));
    }

  }

  public Activity getActivity(final CreateTropixFileDescription activityDescription, final ActivityContext activityContext) {
    return new CreateActivity(activityDescription, activityContext);
  }

}
