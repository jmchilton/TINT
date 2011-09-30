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

import java.util.Arrays;
import java.util.UUID;

import org.easymock.EasyMock;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.credential.types.SimpleCredentialResource;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitJobDescription;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.ModelFunctions;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class BaseSubmitActivityFactoryImplTest {
  private SubmitJobFactorySupport submitSupport;
  private MockFactorySupportImpl factorySupport;
  private MockObjectCollection mockObjects;
  private Ticket ticket;
  private ActivityContext context;
  private CredentialResource dcRef;

  protected <T> T createMock(final Class<T> clazz) {
    return mockObjects.createMock(clazz);
  }

  protected void init() {
    factorySupport = new MockFactorySupportImpl();
    submitSupport = EasyMock.createMock(SubmitJobFactorySupport.class);

    mockObjects = MockObjectCollection.fromObjects(submitSupport);
    mockObjects.addAll(factorySupport.getMockObjects());
    ticket = new Ticket(UUID.randomUUID().toString());
    context = TestUtils.getContext();

    dcRef = new SimpleCredentialResource("credentialid");
    EasyMock.expect(submitSupport.createCredentialResource(context.getCredential())).andStubReturn(dcRef);
  }

  protected ModelStorageData getPersistedData(final String fileId) {
    return factorySupport.getStorageDataFactory().getPersistedStorageData(fileId, context.getCredential());
  }

  protected TransferResource getDownload(final String id) {
    return getFactorySupport().getStorageDataFactory().getPersistedStorageData(id, context.getCredential()).prepareDownloadResource();
  }

  protected SubmitJobFactorySupport getSubmitSupport() {
    return submitSupport;
  }

  protected MockFactorySupportImpl getFactorySupport() {
    return factorySupport;
  }

  protected MockObjectCollection getMockObjects() {
    return mockObjects;
  }

  protected Ticket getTicket() {
    return ticket;
  }

  protected ActivityContext getContext() {
    return context;
  }

  protected CredentialResource expectCredentialResource() {
    return EasyMock.eq(dcRef);
  }

  protected void preRun(final FileJobQueueContext queueContext) {
    EasyMock.expect(queueContext.getTicket()).andReturn(getTicket());
    getMockObjects().replay();
  }

  protected void postRun(final SubmitJobDescription description) {
    assert description.getTicket().equals(getTicket().getValue());
    getMockObjects().verifyAndReset();
  }

  protected <T> T expectCreateJob(final String serviceUrl, final Class<T> clazz) {
    final T mock = createMock(clazz);
    EasyMock.expect(submitSupport.createJobContext(context.getCredential(), serviceUrl, clazz)).andReturn(mock);
    return mock;
  }

  protected TransferResource expectDownload(final String fileId) {
    return EasyMock.eq(factorySupport.getStorageDataFactory().getPersistedStorageData(fileId, context.getCredential()).prepareDownloadResource());
  }

  protected TransferResource expectDownload(final TropixFile file) {
    return expectDownload(file.getId());
  }

  protected TransferResource[] expectDownloads(final TropixFile... files) {
    return expectDownloads(Iterables.toArray(Collections.transform(Arrays.asList(files), ModelFunctions.getIdFunction()), String.class));
  }

  private TransferResource[] expectDownloads(final String... ids) {
    final TransferResource[] resources = new TransferResource[ids.length];
    int i = 0;
    for(final String id : ids) {
      resources[i++] = factorySupport.getStorageDataFactory().getPersistedStorageData(id, context.getCredential()).prepareDownloadResource();
    }
    return EasyMock.aryEq(resources);
  }

  protected TropixFile getFileWithRandomId() {
    final TropixFile file = new TropixFile();
    file.setId(UUID.randomUUID().toString());
    return file;
  }

}
