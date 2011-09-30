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
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.persistence.service.FolderService;

@ManagedBean @ActivityFactoryFor(CreateFolderDescription.class)
class CreateFolderActivityFactoryImpl implements ActivityFactory<CreateFolderDescription> {
  private final FolderService folderService;
  private final FactorySupport factorySupport;

  @Inject
  CreateFolderActivityFactoryImpl(final FolderService folderService, final FactorySupport factorySupport) {
    this.folderService = folderService;
    this.factorySupport = factorySupport;
  }

  class CreateFolderActivityImpl extends BaseTropixObjectJobActivityImpl<CreateFolderDescription, Folder> {

    CreateFolderActivityImpl(final CreateFolderDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext, factorySupport);
    }

    public void run() throws ShutdownException {
      final Folder folder = getModelObject();
      updateId(folderService.createFolder(getUserId(), getDescription().getDestinationId(), folder));      
    }
    
  }

  public Activity getActivity(final CreateFolderDescription activityDescription, final ActivityContext activityContext) {
    return new CreateFolderActivityImpl(activityDescription, activityContext);
  }

}
