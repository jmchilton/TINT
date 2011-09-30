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

package edu.umn.msi.tropix.common.jobqueue.service.impl;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.TicketProvider;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceResolver;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.transfer.TransferResourceContextFactory;
import edu.umn.msi.tropix.transfer.types.CaGridTransferResource;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class GridFileJobQueueContextImplTest {

  enum TestType {
    GET_RESULTS, TIMEOUT, DISPOSE_PROBLEM, UPLOAD_PROBLEM;
  }

  class TestImpl extends GridFileJobQueueContextImpl<JobDescription> {
    private List<TransferResource> tscReferences = null;
    private Credential proxy = null;
    private Object filePopulator = null;

    public void init() {
      this.filePopulator = this.getDownloadContext(tscReferences.get(0), proxy);
    }
  }

  private TicketProvider<Ticket> ticketSupplier;
  private FileJobProcessor<JobDescription> fileJobProcessor;
  private JobProcessorQueue<JobDescription> jobProcessorQueue;
  private Timer timer;
  private MockObjectCollection mocks;
  private TransferResourceContextFactory transferContextsFactory;

  private final Executor executor = new Executor() {
    public void execute(final Runnable command) {
      command.run();
    }
  };

  @BeforeMethod(groups = "unit")
  public void initMocks() {
    fileJobProcessor = EasyMock.createMock(FileJobProcessor.class);
    ticketSupplier = EasyMock.createMock(TicketProvider.class);
    timer = EasyMock.createMock(Timer.class);
    transferContextsFactory = EasyMock.createMock(TransferResourceContextFactory.class);
    jobProcessorQueue = EasyMock.createMock(JobProcessorQueue.class);

    mocks = MockObjectCollection.fromObjects(fileJobProcessor, ticketSupplier, timer, transferContextsFactory, jobProcessorQueue);

  }

  private void initContext(final GridFileJobQueueContextImpl<JobDescription> context) {
    context.setTimer(timer);
    context.setTicketSupplier(ticketSupplier);
    context.setJobProcessorQueue(jobProcessorQueue);
    context.setTransferContextsFactory(transferContextsFactory);
    context.setExecutor(executor);
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void incorrectArrayLenths() {
    final TestImpl context = new TestImpl();
    initContext(context);
    final Ticket ticket = new Ticket("1");
    fileJobProcessor.getResults();
    expectLastCall().andReturn(Arrays.asList());
    timer.schedule(EasyMock.isA(Runnable.class), EasyMock.anyLong());
    expect(ticketSupplier.get()).andReturn(ticket).anyTimes();
    jobProcessorQueue.transferring(ticket);
    jobProcessorQueue.fail(ticket);
    mocks.replay();
    context.jobPostProcessed(ticket, "test", fileJobProcessor, true);
    final TransferResource ref = new CaGridTransferResource();
    try {
      assert context.getNumResults() == 0;
      context.getResults(new TransferResource[] {ref}, null);
    } finally {
      mocks.verifyAndReset();
    }
  }

  @Test(groups = "unit")
  public void getFileProcessor() {
    final TestImpl context = new TestImpl();
    initContext(context);
    final Credential proxy = Credentials.getMock();
    final TransferResource ref = new HttpTransferResource("id");
    final InputContext inputContext = mocks.createMock(InputContext.class);
    expect(transferContextsFactory.getDownloadContext(ref, proxy)).andReturn(inputContext);
    context.tscReferences = Arrays.asList(ref);
    context.proxy = proxy;
    mocks.replay();
    context.init();
    mocks.verifyAndReset();

    final File file = new File("moo");
    inputContext.get(file);
    mocks.replay();
    ((InputContext) context.filePopulator).get(file);
    mocks.verifyAndReset();
  }

  @Test(groups = "unit")
  public void getResource() throws Exception {
    runTest(TestType.GET_RESULTS);
  }

  @Test(groups = "unit")
  public void timeout() throws Exception {
    runTest(TestType.TIMEOUT);
  }

  @Test(groups = "unit")
  public void timeoutAndDisposeProblem() throws Exception {
    runTest(TestType.DISPOSE_PROBLEM);
  }

  @Test(groups = "unit")
  public void uploadProblem() throws Exception {
    runTest(TestType.UPLOAD_PROBLEM);
  }

  public void runTest(final TestType testType) throws Exception {
    final GridFileJobQueueContextImpl<JobDescription> context = new GridFileJobQueueContextImpl<JobDescription>();
    initContext(context);

    final CredentialResourceResolver resolver = mocks.createMock(CredentialResourceResolver.class);
    context.setCredentialResourceResolver(resolver);
    context.setTimeout(1000);

    final DisposableResource resource = mocks.createMock(DisposableResource.class);
    expect(fileJobProcessor.getResults()).andReturn(Arrays.asList(resource));
    final Reference<Runnable> timedRunReference = EasyMockUtils.newReference();
    timer.schedule(EasyMockUtils.record(timedRunReference), anyLong());
    mocks.replay();
    context.jobPostProcessed(new Ticket("1"), "test", fileJobProcessor, true);
    mocks.verifyAndReset();

    final File file = new File("moo");
    final OutputContext outputContext = mocks.createMock(OutputContext.class);
    if(testType.equals(TestType.GET_RESULTS)) {
      expect(resource.getFile()).andReturn(file);
      resource.dispose();
      expect(ticketSupplier.get()).andReturn(new Ticket("1")).anyTimes();
      outputContext.put(file);
      // expect(resolver.getDelgatedCredential(null)).andReturn(null);
      final TransferResource ref = new HttpTransferResource("id");
      expect(transferContextsFactory.getUploadContext(ref, null)).andReturn(outputContext);
      jobProcessorQueue.transferring(new Ticket("1"));
      jobProcessorQueue.complete(new Ticket("1"));
      mocks.replay();

      assert context.getNumResults() == 1;
      context.getResults(ref, null);
    } else if(testType.equals(TestType.UPLOAD_PROBLEM)) {
      expect(ticketSupplier.get()).andReturn(new Ticket("1")).anyTimes();
      final TransferResource ref = new HttpTransferResource("id");
      // expect(resolver.getDelgatedCredential(null)).andReturn(null);
      transferContextsFactory.getUploadContext(ref, null);
      expectLastCall().andThrow(new IllegalStateException());
      jobProcessorQueue.transferring(new Ticket("1"));
      jobProcessorQueue.fail(new Ticket("1"));
      mocks.replay();
      RuntimeException re = null;
      try {
        context.getResults(ref, null);
      } catch(final RuntimeException e) {
        re = e;
      }
      assert re != null;
    } else if(testType.equals(TestType.TIMEOUT)) {
      resource.dispose();
      mocks.replay();

      timedRunReference.get().run();
    } else if(testType.equals(TestType.DISPOSE_PROBLEM)) {
      resource.dispose();
      expectLastCall().andThrow(new IllegalStateException());
      mocks.replay();
      timedRunReference.get().run();
    }
    mocks.verifyAndReset();
  }
}
