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

package edu.umn.msi.tropix.common.jobqueue.test;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.TicketProvider;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.credential.types.SimpleCredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceResolver;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.transfer.TransferResourceContextFactory;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class BaseFileJobQueueContextImplTest {
  private TransferResourceContextFactory tcFactory;
  private CredentialResourceResolver credentialResourceResolver;
  private boolean ticketSet;
  private final CredentialResource credentialResource = new SimpleCredentialResource("test");
  private final Credential credential = Credentials.getMock();
  private JobProcessorQueue<JobDescription> jobProcessorQueue;
  private FileJobProcessor<JobDescription> fileJobProcessor;
  private MockObjectCollection mockObjects;

  protected void addMock(final Object mock) {
    mockObjects.add(mock);
  }

  protected <T> T createMock(final Class<T> clazz) {
    return mockObjects.createMock(clazz);
  }

  protected void expectLastCallAndReturnFileJobProcessor() {
    EasyMock.expectLastCall().andReturn(fileJobProcessor);
  }

  protected void expectSubmitJob(final String type) {
    expectSubmitJob(type, false);
  }

  protected void expectSubmitJob(final String type, final boolean throwException) {
    jobProcessorQueue.submitJob(EasyMock.same(fileJobProcessor), EasyMock.eq(type));
    if(throwException) {
      EasyMock.expectLastCall().andThrow(new RuntimeException());
    } else {
      EasyMock.expectLastCall().andReturn(new Ticket("123"));
    }
  }

  protected JobProcessorConfiguration expectConfiguration() {
    return EasyMock.eq(JobProcessorConfigurationFactories.getInstance().get(credential));
  }

  private void assertTicketSet() {
    assert ticketSet;
  }

  public BaseFileJobQueueContextImplTest() {
    super();
  }

  protected TransferResource getReference() {
    return new HttpTransferResource(UUID.randomUUID().toString());
  }

  protected InputContext getDownloadContext(final TransferResource ref) {
    final InputContext context = EasyMock.createMock(InputContext.class);
    EasyMock.expect(tcFactory.getDownloadContext(ref, credential)).andReturn(context);
    return context;
  }

  protected void init(final GridFileJobQueueContextImpl<? extends JobDescription> context) {
    context.setTransferContextsFactory(tcFactory);
    context.setJobProcessorQueue(jobProcessorQueue);
    context.setCredentialResourceResolver(credentialResourceResolver);
    EasyMock.expect(credentialResourceResolver.getCredential(getCredentialReference())).andReturn(credential);
    ticketSet = false;
    context.setTicketSupplier(new TicketProvider<Ticket>() {
      public void set(final Ticket ticket) {
        ticketSet = true;
      }

      public Ticket get() {
        return null;
      }
    });
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    tcFactory = EasyMock.createMock(TransferResourceContextFactory.class);
    jobProcessorQueue = EasyMock.createMock(JobProcessorQueue.class);
    fileJobProcessor = EasyMock.createMock(FileJobProcessor.class);
    credentialResourceResolver = EasyMock.createMock(CredentialResourceResolver.class);
    mockObjects = MockObjectCollection.fromObjects(credentialResourceResolver, tcFactory, jobProcessorQueue, fileJobProcessor);
  }

  protected void doReplay() {
    mockObjects.replay();
  }

  protected void doVerify() {
    mockObjects.verifyAndReset();
    assertTicketSet();
  }

  protected CredentialResource getCredentialReference() {
    return credentialResource;
  }

}