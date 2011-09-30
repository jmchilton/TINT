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

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.jobqueue.client.impl.JobPollingHalter;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobPollingRunnable;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobPollingRunnableSupplierImpl;
import edu.umn.msi.tropix.common.jobqueue.client.impl.JobTracker;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class JobPollingRunnableSupplierTest {

  @Test(groups = "unit")
  public void runs() {
    final JobPollingRunnableSupplierImpl supplier = new JobPollingRunnableSupplierImpl();
    final JobPollingRunnable runnable = supplier.get();

    @SuppressWarnings("unchecked")
    final Supplier<StatusService> mockStatusServiceSupplier = createMock(Supplier.class);
    final StatusService mockStatusService = createMock(StatusService.class);
    expect(mockStatusServiceSupplier.get()).andReturn(mockStatusService);

    final JobTracker mockJobTracker = createStrictMock(JobTracker.class);
    final List<Ticket> tickets = Arrays.asList(new Ticket("123"), new Ticket("234"));
    expect(mockJobTracker.getTickets()).andReturn(tickets).times(2);

    final Status[] statuses = new Status[2];
    for(int i = 0; i < 2; i++) {
      statuses[i] = new Status();
      statuses[i].setStage(new Stage());
      statuses[i].getStage().setValue(StageEnumeration.Pending);
    }

    expect(mockStatusService.getStatuses(aryEq(tickets.toArray(new Ticket[] {})))).andReturn(statuses).times(2);

    final JobPollingHalter mockPollingHalter = createStrictMock(JobPollingHalter.class);
    mockPollingHalter.haltIfComplete();
    expectLastCall().times(2);

    replay(mockStatusServiceSupplier, mockStatusService, mockJobTracker, mockPollingHalter);

    runnable.setStatusServiceSupplier(mockStatusServiceSupplier);
    runnable.setJobTracker(mockJobTracker);
    runnable.setServiceUrl("http://url/");
    runnable.setJobPollingHalter(mockPollingHalter);

    runnable.run();
    runnable.run();

    verify(mockStatusServiceSupplier, mockStatusService, mockJobTracker, mockPollingHalter);

  }

  // Make sure mockPollingHalter.halt() isn't called on shutdown
  @Test(groups = "unit")
  public void shutdown() {
    final JobPollingRunnableSupplierImpl supplier = new JobPollingRunnableSupplierImpl();
    final JobPollingRunnable runnable = supplier.get();

    final Supplier<StatusService> mockStatusServiceSupplier = EasyMockUtils.createMockSupplier();
    expect(mockStatusServiceSupplier.get()).andThrow(new ShutdownException());
    final JobPollingHalter mockPollingHalter = createStrictMock(JobPollingHalter.class);

    final JobTracker mockJobTracker = createStrictMock(JobTracker.class);
    final List<Ticket> tickets = Arrays.asList(new Ticket("123"));
    expect(mockJobTracker.getTickets()).andReturn(tickets);

    replay(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter);

    runnable.setStatusServiceSupplier(mockStatusServiceSupplier);
    runnable.setJobTracker(mockJobTracker);
    runnable.setServiceUrl("http://url/");
    runnable.setJobPollingHalter(mockPollingHalter);

    runnable.run();
    verify(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter);
  }

  @Test(groups = "unit")
  public void clientError() {
    final JobPollingRunnableSupplierImpl supplier = new JobPollingRunnableSupplierImpl();
    final JobPollingRunnable runnable = supplier.get();

    final Supplier<StatusService> mockStatusServiceSupplier = EasyMockUtils.createMockSupplier();
    expect(mockStatusServiceSupplier.get()).andThrow(new IllegalStateException());
    final JobPollingHalter mockPollingHalter = createStrictMock(JobPollingHalter.class);
    mockPollingHalter.halt();

    final JobTracker mockJobTracker = createStrictMock(JobTracker.class);
    List<Ticket> tickets = Arrays.asList(new Ticket("123"));
    expect(mockJobTracker.getTickets()).andReturn(tickets);

    replay(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter);

    runnable.setStatusServiceSupplier(mockStatusServiceSupplier);
    runnable.setJobTracker(mockJobTracker);
    runnable.setServiceUrl("http://url/");
    runnable.setJobPollingHalter(mockPollingHalter);

    runnable.run();
    EasyMockUtils.verifyAndReset(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter);

    StatusService statusService = createMock(StatusService.class);
    expect(mockStatusServiceSupplier.get()).andReturn(statusService);
    tickets = Arrays.asList(new Ticket("123"));
    expect(mockJobTracker.getTickets()).andReturn(tickets);
    statusService.getStatuses(aryEq(new Ticket[] {new Ticket("123")}));
    expectLastCall().andThrow(new IllegalStateException());
    replay(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter, statusService);
    runnable.run();
    EasyMockUtils.verifyAndReset(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter, statusService);

    statusService = createMock(StatusService.class);
    expect(mockStatusServiceSupplier.get()).andReturn(statusService);
    tickets = Arrays.asList(new Ticket("123"));
    expect(mockJobTracker.getTickets()).andReturn(tickets);
    statusService.getStatuses(aryEq(new Ticket[] {new Ticket("123")}));
    expectLastCall().andThrow(new IllegalStateException());
    replay(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter, statusService);
    runnable.run();
    EasyMockUtils.verifyAndReset(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter, statusService);

  }

  @Test(groups = "unit")
  public void noTickets() {
    final JobPollingRunnableSupplierImpl supplier = new JobPollingRunnableSupplierImpl();
    final JobPollingRunnable runnable = supplier.get();
    final JobTracker mockJobTracker = createStrictMock(JobTracker.class);
    expect(mockJobTracker.getTickets()).andReturn(Collections.<Ticket>emptyList()).times(4);

    final Supplier<StatusService> mockStatusServiceSupplier = EasyMockUtils.createMockSupplier();
    // Status service should be initialized if there are no tickets

    final JobPollingHalter mockPollingHalter = createStrictMock(JobPollingHalter.class);
    mockPollingHalter.haltIfComplete();
    expectLastCall().times(4);

    replay(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter);

    runnable.setStatusServiceSupplier(mockStatusServiceSupplier);
    runnable.setJobTracker(mockJobTracker);
    runnable.setServiceUrl("http://url/");
    runnable.setJobPollingHalter(mockPollingHalter);

    runnable.run();
    runnable.run();
    runnable.run();
    runnable.run();

    verify(mockStatusServiceSupplier, mockJobTracker, mockPollingHalter);
  }

  @Test(groups = "unit")
  public void completes() {
    final StageEnumeration[] stage = new StageEnumeration[] {StageEnumeration.Complete, StageEnumeration.Absent, StageEnumeration.Failed, StageEnumeration.Timedout};
    // TODO: I set completedNormally up for reason, do something with it
    // boolean[] completedNormally = new boolean[]{true,false,false,false};
    for(final StageEnumeration stageEnumeration : stage) {
      // boolean expectCompletedNormally = completedNormally[i];
      final JobPollingRunnableSupplierImpl supplier = new JobPollingRunnableSupplierImpl();
      final JobPollingRunnable runnable = supplier.get();

      final Supplier<StatusService> mockStatusServiceSupplier = EasyMockUtils.createMockSupplier();
      final StatusService mockStatusService = createMock(StatusService.class);
      expect(mockStatusServiceSupplier.get()).andReturn(mockStatusService);

      final JobTracker mockJobTracker = createStrictMock(JobTracker.class);
      final List<Ticket> tickets = Arrays.asList(new Ticket("123"));
      expect(mockJobTracker.getTickets()).andReturn(tickets);
      final Status[] statuses = new Status[1];
      statuses[0] = new Status();
      statuses[0].setStage(new Stage());
      statuses[0].getStage().setValue(stageEnumeration);
      mockJobTracker.updateJob(tickets.get(0), statuses[0]);

      expect(mockStatusService.getStatuses(aryEq(tickets.toArray(new Ticket[] {})))).andReturn(statuses);

      final JobPollingHalter mockPollingHalter = createStrictMock(JobPollingHalter.class);
      mockPollingHalter.haltIfComplete();

      replay(mockStatusServiceSupplier, mockStatusService, mockJobTracker, mockPollingHalter);

      runnable.setStatusServiceSupplier(mockStatusServiceSupplier);
      runnable.setJobTracker(mockJobTracker);
      runnable.setServiceUrl("http://url/");
      runnable.setJobPollingHalter(mockPollingHalter);

      runnable.run();

      verify(mockStatusServiceSupplier, mockStatusService, mockJobTracker, mockPollingHalter);
    }
  }

}
