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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.StatusServiceFactory;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobPollingHalter;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobPollingRunnable;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobTracker;
import edu.umn.msi.tropix.common.jobqueue.client.impl.PollingJobHandlerImpl;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class PollingJobHandlerImplTest {

  @Test(groups = "unit")
  public void startingJobs() {
    final PollingJobHandlerImpl handler = new PollingJobHandlerImpl();
    final Credential proxy = createMock(Credential.class);
    handler.setHostProxy(proxy);
    final StatusServiceFactory statusServiceFactory = createMock(StatusServiceFactory.class);
    handler.setStatusServiceFactory(statusServiceFactory);
    final Job job1 = new Job(), job2 = new Job(), job3 = new Job(), job4 = new Job();
    job1.setTicket(new Ticket("1"));
    job1.setJobType("MOCK");
    job1.setServiceAddress("http://1");
    job2.setTicket(new Ticket("2"));
    job2.setJobType("MOCK");
    job2.setServiceAddress("http://1");
    job3.setTicket(new Ticket("3"));
    job3.setServiceAddress("http://2");
    job3.setJobType("MOCK");
    job4.setTicket(new Ticket("4"));
    job4.setServiceAddress("http://1");
    job4.setJobType("MOCK");

    final Executor mockExecutor = EasyMock.createMock(Executor.class);

    @SuppressWarnings("unchecked")
    final Supplier<LoopingRunnable> mockLoopingRunnableSupplier = EasyMock.createMock(Supplier.class);
    @SuppressWarnings("unchecked")
    final Supplier<JobTracker> mockTrackerSupplier = EasyMock.createMock(Supplier.class);

    @SuppressWarnings("unchecked")
    final Supplier<JobPollingRunnable> mockPollingRunnableSupplier = EasyMock.createMock(Supplier.class);

    final LoopingRunnable[] loopingRunnables = new LoopingRunnable[] {createMock(LoopingRunnable.class), createMock(LoopingRunnable.class)};
    final JobTracker[] jobTrackers = new JobTracker[] {createMock(JobTracker.class), createMock(JobTracker.class)};
    final JobPollingRunnable[] pollingRunnables = new JobPollingRunnable[] {createMock(JobPollingRunnable.class), createMock(JobPollingRunnable.class)};
    final List<Reference<JobPollingHalter>> halterReferences = Lists.newArrayList();
    final List<Reference<Supplier<StatusService>>> supplierReferences = Lists.newArrayList();
    for(int i = 0; i < 2; i++) {
      final LoopingRunnable runnable = loopingRunnables[i];
      final JobTracker jobTracker = jobTrackers[i];
      final JobPollingRunnable jobPollingRunnable = pollingRunnables[i];

      expect(mockLoopingRunnableSupplier.get()).andReturn(runnable);
      mockExecutor.execute(EasyMock.same(runnable));
      expect(mockTrackerSupplier.get()).andReturn(jobTracker);
      expect(mockPollingRunnableSupplier.get()).andReturn(jobPollingRunnable);
      runnable.setBaseRunnable(EasyMock.same(jobPollingRunnable));
      halterReferences.add(EasyMockUtils.<JobPollingHalter>newReference());
      jobPollingRunnable.setJobPollingHalter(EasyMockUtils.record(halterReferences.get(i)));
      supplierReferences.add(EasyMockUtils.<Supplier<StatusService>>newReference());
      jobPollingRunnable.setStatusServiceSupplier(EasyMockUtils.record(supplierReferences.get(i)));
      jobPollingRunnable.setServiceUrl("http://" + (i + 1));
      jobPollingRunnable.setJobTracker(jobTracker);
    }

    jobTrackers[0].addJob(EasyMock.eq(new Ticket("1")), EasyMock.same(job1));
    jobTrackers[0].addJob(EasyMock.eq(new Ticket("2")), EasyMock.same(job2));
    jobTrackers[1].addJob(EasyMock.eq(new Ticket("3")), EasyMock.same(job3));
    jobTrackers[0].addJob(EasyMock.eq(new Ticket("4")), EasyMock.same(job4));

    final Collection<Object> mockObjects = new LinkedList<Object>();
    mockObjects.addAll(Arrays.asList(loopingRunnables));
    mockObjects.addAll(Arrays.asList(jobTrackers));
    mockObjects.addAll(Arrays.asList(pollingRunnables));
    mockObjects.addAll(Arrays.asList(mockExecutor, mockLoopingRunnableSupplier, mockTrackerSupplier, mockPollingRunnableSupplier));

    for(final Object mockObject : mockObjects) {
      EasyMock.replay(mockObject);
    }

    handler.setJobPollingExecutor(mockExecutor);
    handler.setJobPollingRunnableSupplier(mockPollingRunnableSupplier);
    handler.setLoopingRunnableSupplier(mockLoopingRunnableSupplier);
    handler.setJobTrackerSupplier(mockTrackerSupplier);

    handler.pollJob(job1);
    handler.pollJob(job2);
    handler.pollJob(job3);
    handler.pollJob(job4);

    for(final Object mockObject : mockObjects) {
      EasyMockUtils.verifyAndReset(mockObject);
    }

    statusServiceFactory.getStatusService("http://1", proxy);
    EasyMock.expectLastCall().andReturn(null);
    statusServiceFactory.getStatusService("http://2", proxy);
    EasyMock.expectLastCall().andReturn(null);

    EasyMock.replay(statusServiceFactory);
    supplierReferences.get(0).get().get(); // Get supplier from reference, and object from supplier
    supplierReferences.get(1).get().get();
    EasyMockUtils.verifyAndReset(statusServiceFactory);

    expect(jobTrackers[0].hasJobs()).andReturn(true);
    expect(jobTrackers[0].hasJobs()).andReturn(false);
    jobTrackers[0].completeAllJobs(false);
    EasyMock.replay(jobTrackers[0]);

    halterReferences.get(0).get().haltIfComplete();
    halterReferences.get(0).get().haltIfComplete();

    EasyMockUtils.verifyAndReset(jobTrackers[0]);

  }
}
