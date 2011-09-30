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

package edu.umn.msi.tropix.jobs.impl;

import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceFactory;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class TransferClientImpl implements TransferClient {
  private CredentialResourceFactory delegatedCredentialFactory;
  private StorageDataFactory storageDataFactory;

  public String[] transferResults(final Integer inputNumResults, final FileJobQueueContext context, final String storageServiceUrl, final Credential proxy) {
    final int numResults = inputNumResults == null ? context.getNumResults() : inputNumResults;
    CredentialResource delegatedCredentialReference = null;
    if(proxy != null && delegatedCredentialFactory != null) {
      delegatedCredentialReference = delegatedCredentialFactory.createDelegatedCredential(proxy);
    }
    final TransferResource[] resources = new TransferResource[numResults];
    final String[] ids = new String[numResults];
    for(int i = 0; i < numResults; i++) {
      final StorageData storageData = storageDataFactory.getStorageData(storageServiceUrl, proxy);
      resources[i] = storageData.prepareUploadResource();
      ids[i] = storageData.getDataIdentifier();
    }
    context.getResults(resources, delegatedCredentialReference);

    return ids;
  }

  public void setCredentialResourceFactory(final CredentialResourceFactory delegatedCredentialFactory) {
    this.delegatedCredentialFactory = delegatedCredentialFactory;
  }

  public void setStorageDataFactory(final StorageDataFactory storageDataFactory) {
    this.storageDataFactory = storageDataFactory;
  }
}
