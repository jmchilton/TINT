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

import java.io.File;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import net.jmchilton.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.MessageCodes;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;

@ManagedBean @ActivityFactoryFor(UploadFileDescription.class)
class UploadFileActivityFactoryImpl implements ActivityFactory<UploadFileDescription> {
  private static final Log LOG = LogFactory.getLog(UploadFileActivityFactoryImpl.class);
  private final FactorySupport factorySupport;
  private final Semaphore semaphore;

  UploadFileActivityFactoryImpl(final FactorySupport factorySupport) {  
    this(factorySupport, 0);
  }
  
  @Inject 
  UploadFileActivityFactoryImpl(final FactorySupport factorySupport,
                                @Value("${max.simultaneous.storage.uploads?:0}") final int maxSimultaneousUploads) {
    this.factorySupport = factorySupport;
    final int actualMaxUploads = maxSimultaneousUploads > 0 ? maxSimultaneousUploads : Integer.MAX_VALUE;
    LOG.debug("Setting max simulataneous uplods to " + actualMaxUploads);
    this.semaphore = new Semaphore(actualMaxUploads, true);
  }
  
  class UploadActivityImpl extends BaseActivityImpl<UploadFileDescription> {

    UploadActivityImpl(final UploadFileDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext);
    }
 
    public void run() throws ShutdownException {
      factorySupport.getEventSupport().workflowUpdate(newWorkflowEvent(), MessageCodes.TRANSFERRING_FILES, null);
      final StorageDataFactory storageDataFactory = factorySupport.getStorageDataFactory();
      final StorageData storageData = storageDataFactory.getStorageData(getDescription().getStorageServiceUrl(), getCredential());
      try {
        semaphore.acquire();
        storageData.getUploadContext().put(new File(getDescription().getInputFilePath()));
      } finally {
        semaphore.release();
      }
      final String dataIdentifier = storageData.getDataIdentifier();
      getDescription().setFileId(dataIdentifier);
    }

  }
  
  public Activity getActivity(final UploadFileDescription activityDescription, final ActivityContext activityContext) {
    return new UploadActivityImpl(activityDescription, activityContext);
  }
    
}
