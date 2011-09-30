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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.jmchilton.concurrent.CountDownLatch;
import net.jmchilton.concurrent.ThreadUtils;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobPoller;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.status.QueuePosition;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.Cancellable;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.impl.EventBaseImpl;
import edu.umn.msi.tropix.jobs.impl.ResultTransferLauncher;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

public class PollJobActivityFactoryImplTest {
  private static final Ticket TEST_TICKET_1 = new Ticket("test1") /* , TEST_TICKET_2 = new Ticket("test2") */;
  private static final Status TEST_STATUS_1 = new Status();
  private static final String SERVICE_URL_1 = "http://test";

  private MockFactorySupportImpl factorySupport;
  private JobPoller jobPoller;
  private PollJobActivityFactoryImpl factory;
  private JobContextClientFactory jobContextClientFactory;
  private ResultTransferLauncher launcher;

  private PollJobDescription description;
  private ActivityContext context;

  @BeforeMethod(groups = "unit")
  public void init() {
    factorySupport = new MockFactorySupportImpl();
    jobPoller = EasyMock.createMock(JobPoller.class);
    jobContextClientFactory = EasyMock.createMock(JobContextClientFactory.class);
    launcher = EasyMock.createMock(ResultTransferLauncher.class);
    EasyMock.replay(jobPoller);
    factory = new PollJobActivityFactoryImpl(factorySupport, jobContextClientFactory, launcher);
    factory.setJobPoller(jobPoller);
    EasyMock.verify(jobPoller);
    EasyMock.reset(jobPoller);

    description = TestUtils.init(new PollJobDescription());
    description.setServiceUrl(SERVICE_URL_1);
    description.setTicket(TEST_TICKET_1.getValue());

    context = TestUtils.getContext();

  }

  @Test(groups = "unit", timeOut = 1000)
  public void testCancel() {
    final JobQueueContext queueContext = EasyMock.createMock(JobQueueContext.class);
    jobContextClientFactory.getJobClient(context.getCredential(), SERVICE_URL_1, TEST_TICKET_1.getValue(), JobQueueContext.class);
    EasyMock.expectLastCall().andReturn(queueContext);
    queueContext.cancel();
    EasyMock.replay(queueContext, jobContextClientFactory);
    final Activity activity = factory.getActivity(description, context);
    ((Cancellable) activity).cancel();
    EasyMock.verify(queueContext, jobContextClientFactory);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testNoExceptionTicketUnknown() {
    factory.update(TEST_TICKET_1, TEST_STATUS_1);
  }

  @Test(groups = "unit", dataProviderClass = TestNGDataProviders.class, dataProvider = "bool1")
  public void testCompleteBeforeRun(final boolean completeProperly) {
    factory.jobComplete(TEST_TICKET_1, completeProperly, TEST_STATUS_1);
    final Activity activity = factory.getActivity(description, context);
    RuntimeException exception = null;
    try {
      activity.run();
    } catch(final RuntimeException e) {
      exception = e;
    }
    if(completeProperly) {
      assert exception == null;
    } else {
      assert exception != null;
    }
  }

  private static final class TestCompleteAfterRunRunnable implements Runnable {
    private RuntimeException exception = null;
    private final Activity activity;
    private final CountDownLatch latch;

    private TestCompleteAfterRunRunnable(final Activity activity, final CountDownLatch latch) {
      this.activity = activity;
      this.latch = latch;
    }

    public void run() {
      try {
        latch.countDown();
        activity.run();
      } catch(final RuntimeException e) {
        exception = e;
      }
    }
  }

  private static boolean isThreadWaiting(final Thread t) {
    return Sets.newHashSet(Thread.State.BLOCKED, Thread.State.WAITING, Thread.State.TIMED_WAITING).contains(t.getState());
  }

  @Test(groups = "unit", dataProviderClass = TestNGDataProviders.class, dataProvider = "bool1", invocationCount = 1, timeOut = 2000)
  public void testCompleteAfterRun(final boolean completeProperly) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Activity activity = factory.getActivity(description, context);
    final TestCompleteAfterRunRunnable runnable = new TestCompleteAfterRunRunnable(activity, latch);

    final Thread t = new Thread(runnable);
    t.start();
    latch.await();
    Thread.sleep(1);
    // Wait for activity thread to get into some sort of waiting state for completion
    while(!isThreadWaiting(t)) {
      Thread.sleep(1);
    }
    factory.jobComplete(TEST_TICKET_1, completeProperly, TEST_STATUS_1);
    ThreadUtils.join(t);
    final RuntimeException exception = runnable.exception;
    if(completeProperly) {
      assert exception == null;
    } else {
      assert exception != null;
    }

  }

  @Test(groups = "unit", dataProviderClass = TestNGDataProviders.class, dataProvider = "bool1", invocationCount = 1, timeOut = 2000)
  public void testCompleteAfterRunAndInterrupt(final boolean completeProperly) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Activity activity = factory.getActivity(description, context);
    final TestCompleteAfterRunRunnable runnable = new TestCompleteAfterRunRunnable(activity, latch);

    final Thread t = new Thread(runnable);
    t.start();
    latch.await();
    Thread.sleep(1);
    // Wait for activity thread to get into some sort of waiting state for completion
    while(!isThreadWaiting(t)) {
      Thread.sleep(1);
    }
    t.interrupt();
    Thread.sleep(1);
    while(!isThreadWaiting(t)) {
      Thread.sleep(1);
    }
    factory.jobComplete(TEST_TICKET_1, completeProperly, TEST_STATUS_1);
    ThreadUtils.join(t);
    final RuntimeException exception = runnable.exception;
    if(completeProperly) {
      assert exception == null;
    } else {
      assert exception != null;
    }

  }

  @Test(groups = "unit", timeOut = 2000)
  public void testShutdownAfterRun() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Activity activity = factory.getActivity(description, context);

    final TestCompleteAfterRunRunnable runnable = new TestCompleteAfterRunRunnable(activity, latch);

    final Thread t = new Thread(runnable);
    t.start();
    latch.await();
    Thread.sleep(1);
    // Wait for activity thread to get into some sort of waiting state for completion
    while(!isThreadWaiting(t)) {
      Thread.sleep(1);
    }
    factory.shutdown();
    ThreadUtils.join(t);
    assert runnable.exception != null && runnable.exception instanceof ShutdownException;
  }

  @Test(groups = "unit", timeOut = 2000, expectedExceptions = ShutdownException.class)
  public void testShutdownBeforeRun() throws InterruptedException {
    factory.shutdown();
    final Activity activity = factory.getActivity(description, context);
    activity.run();
  }

  private static final class TestActivityRunnable implements Runnable {
    private final Activity activity;

    private TestActivityRunnable(final Activity activity) {
      this.activity = activity;
    }

    public void run() {
      activity.run();
    }
  }

  @Test(groups = "unit", timeOut = 5000)
  public void routing() throws InterruptedException {
    final int count = 10;
    final Map<String, PollJobDescription> descriptions = Maps.newHashMapWithExpectedSize(count);
    final Map<String, ActivityContext> contexts = Maps.newHashMapWithExpectedSize(count);
    final Map<Integer, String> posToTicket = Maps.newHashMap();

    for(int i = 0; i < count; i++) {
      PollJobDescription description;
      ActivityContext context;

      description = TestUtils.init(new PollJobDescription());
      description.setServiceUrl(SERVICE_URL_1 + i);
      final String ticket = UUID.randomUUID().toString();
      description.setTicket(ticket);

      context = TestUtils.getContext();

      descriptions.put(ticket, description);
      contexts.put(ticket, context);
    }

    final List<EventBaseImpl> events = Lists.newArrayList();
    final List<Status> statuses = Lists.newArrayList();
    final List<Job> jobs = Lists.newArrayList();
    final List<Thread> threads = Lists.newArrayListWithExpectedSize(count);
    final CountDownLatch latch = new CountDownLatch(count); // Will fire after polling has started on all jobs

    jobPoller.pollJob(EasyMockUtils.record(new EasyMockUtils.Recorder<Job>() {
      public void record(final Job job) {
        jobs.add(job);
        latch.countDown();
      }
    }));
    EasyMock.expectLastCall().anyTimes();
    factorySupport.getEventSupport().gridStatus(EasyMockUtils.addToCollection(events), EasyMockUtils.addToCollection(statuses));
    EasyMock.expectLastCall().anyTimes();
    EasyMockUtils.replayAll(jobPoller, factorySupport.getEventSupport());

    for(final String ticket : descriptions.keySet()) {
      final PollJobDescription description = descriptions.get(ticket);
      final Thread t = new Thread(new TestActivityRunnable(factory.getActivity(description, contexts.get(ticket))));
      threads.add(t);
      t.start();
    }

    latch.await();

    assert jobs.size() == count : jobs.size();
    for(final Job job : jobs) {
      final String ticket = job.getTicket().getValue();
      final PollJobDescription description = descriptions.get(ticket);
      final ActivityContext context = contexts.get(ticket);
      assert job.getProxy().equals(context.getCredential());
      assert job.getServiceAddress().equals(description.getServiceUrl());
    }

    int pos = 0;
    for(final PollJobDescription description : descriptions.values()) {
      final Status status = new Status();
      status.setQueuePosition(new QueuePosition(count, pos));
      posToTicket.put(pos, description.getTicket());
      pos++;
      factory.update(new Ticket(description.getTicket()), status);
    }

    for(final PollJobDescription description : descriptions.values()) {
      final Status status = new Status();
      status.setQueuePosition(new QueuePosition(count, pos));
      posToTicket.put(pos, description.getTicket());
      pos++;
      factory.jobComplete(new Ticket(description.getTicket()), true, status);
    }

    for(final Thread t : threads) {
      t.join();
    }

    EasyMockUtils.verifyAndResetAll(jobPoller, factorySupport.getEventSupport());

    assert statuses.size() == 2 * count : statuses.size();
    assert events.size() == 2 * count : events.size();

    for(int i = 0; i < 2 * count; i++) {
      final EventBase eventBase = events.get(i);
      final Status status = statuses.get(i);
      final String userId = eventBase.getUserId();
      String ticket = null;
      for(final Map.Entry<String, ActivityContext> contextEntry : contexts.entrySet()) {
        if(contextEntry.getValue().getCredential().getIdentity().equals(userId)) {
          ticket = contextEntry.getKey();
        }
      }
      assert ticket != null;
      final PollJobDescription description = descriptions.get(ticket);
      final ActivityContext context = contexts.get(ticket);
      assert eventBase.getJobId().equals(description.getJobDescription().getId());
      assert eventBase.getJobName().equals(description.getJobDescription().getName());
      assert eventBase.getUserId().equals(context.getCredential().getIdentity());
      assert posToTicket.get(status.getQueuePosition().getValue()).equals(ticket);
    }

  }

}
