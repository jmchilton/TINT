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

import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.credential.types.SimpleCredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceFactory;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class TransferClientImplTest {
  @Test(groups = "unit", timeOut = 1000)
  public void transferAnonymous() {
    transferResults(2, true, true, false);
    transferResults(2, false, true, false);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void transfer() {
    transferResults(5, true, false, true);
  }

  public void transferResults(final int num, final boolean setNum, final boolean nullProxy, final boolean setDCFactory) {
    final TransferClientImpl client = new TransferClientImpl();
    Credential proxy = null;
    if(!nullProxy) {
      proxy = EasyMock.createMock(Credential.class);
    }
    final StorageDataFactory storageDataFactory = EasyMock.createMock(StorageDataFactory.class);
    client.setStorageDataFactory(storageDataFactory);
    final CredentialResourceFactory dcFactory = EasyMock.createMock(CredentialResourceFactory.class);
    CredentialResource dcReference = null;
    if(setDCFactory) {
      dcReference = new SimpleCredentialResource("testid");
      dcFactory.createDelegatedCredential(EasyMock.same(proxy));
      EasyMock.expectLastCall().andReturn(dcReference);
      client.setCredentialResourceFactory(dcFactory);
    }
    final String storageServiceUrl = "http://storage";
    client.setStorageDataFactory(storageDataFactory);
    final FileJobQueueContext context = EasyMock.createMock(FileJobQueueContext.class);
    if(!setNum) {
      EasyMock.expect(context.getNumResults()).andReturn(num);
    }
    final TransferResource[] references = new TransferResource[num];
    final StorageData[] storageDataArray = new StorageData[num];
    for(int i = 0; i < num; i++) {
      final StorageData storageData = EasyMock.createMock(StorageData.class);
      storageDataFactory.getStorageData(storageServiceUrl, proxy);
      EasyMock.expectLastCall().andReturn(storageData);
      storageDataArray[i] = storageData;
      final TransferResource reference = new HttpTransferResource("id" + i);
      references[i] = reference;
      storageData.prepareUploadResource();
      EasyMock.expectLastCall().andReturn(reference);
      EasyMock.expect(storageData.getDataIdentifier()).andReturn("" + i);
    }
    context.getResults(EasyMockUtils.arySame(references), dcReference == null ? (CredentialResource) EasyMock.isNull() : EasyMock.same(dcReference));
    EasyMockUtils.replayAll(storageDataArray, context, storageDataFactory, dcFactory);
    final String[] ids = client.transferResults(setNum ? num : null, context, storageServiceUrl, proxy);
    assert ids.length == num;
    for(int i = 0; i < ids.length; i++) {
      assert ids[i].equals("" + i);
    }
    EasyMockUtils.verifyAll(storageDataArray, context, storageDataFactory, dcFactory);
  }
}
