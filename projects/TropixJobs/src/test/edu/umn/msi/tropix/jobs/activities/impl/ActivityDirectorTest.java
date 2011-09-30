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

package edu.umn.msi.tropix.jobs.activities.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.jmchilton.concurrent.CountDownLatch;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.concurrent.Executors;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityStatus;
import edu.umn.msi.tropix.jobs.activities.descriptions.Consumes;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.Produces;

// TODO: Verify checkJobComplete can be called multiple times with same parameters...
public class ActivityDirectorTest {

  class MockActivityService implements ActivityService {
    private Collection<ActivityContext> existingContexts = Lists.newArrayList();
    private List<Map<ActivityDescription, ActivityStatus>> statusSnapshots = Lists.newArrayList();
    private int savedCount = 0;

    public Collection<ActivityContext> loadActivityContexts() {
      return existingContexts;
    }

    public void save(final ActivityContext activityContext) {
      savedCount++;
      if(activityContext.getId() == null) {
        activityContext.setId(UUID.randomUUID().toString());
      }
      final Map<ActivityDescription, ActivityStatus> statusSnapshot = Maps.newHashMap();
      for(final ActivityDescription activityDescription : activityContext.getActivityDescriptions()) {
        statusSnapshot.put(activityDescription, activityDescription.getActivityStatus());
        if(activityDescription.getId() == null) {
          activityDescription.setId(UUID.randomUUID().toString());
        }
        final JobDescription jobDescription = activityDescription.getJobDescription();
        if(jobDescription != null && jobDescription.getId() == null) {
          jobDescription.setId(UUID.randomUUID().toString());
        }
      }
      statusSnapshots.add(statusSnapshot);
    }

  }

  static class WaitingActivity implements Activity, Revertable, Cancellable {
    private final CountDownLatch start = new CountDownLatch(1), finish = new CountDownLatch(1), rolledBackLatch = new CountDownLatch(1),
        cancelLatch = new CountDownLatch(1);
    private boolean wait;
    private boolean ran = false;
    private boolean shutdown = false;
    private boolean throwException = false;
    private boolean rolledBack = false;
    private boolean rollbackException = false;
    private boolean cancelled = false;
    private long rollbackNanotime;

    WaitingActivity() {
      this(true);
    }

    WaitingActivity(final boolean wait) {
      this.wait = wait;
    }

    public void run() {
      start.countDown();
      ran = true;
      if(wait) {
        finish.await();
      }
      if(shutdown) {
        throw new ShutdownException();
      }
      if(throwException) {
        throw new RuntimeException();
      }
    }

    public void rollback() {
      rolledBack = true;
      rollbackNanotime = System.nanoTime();
      rolledBackLatch.countDown();
      if(rollbackException) {
        throw new RuntimeException();
      }
    }

    void assertRolledBackBefore(final WaitingActivity otherActivity) {
      assert rollbackNanotime < otherActivity.rollbackNanotime;
    }

    public void cancel() {
      cancelLatch.countDown();
      cancelled = true;
    }

  }

  public static class TestDescription extends ActivityDescription {
    private String input, output;
    private List<String> listInputs = new LinkedList<String>();
    private List<String> listInputs2 = new ArrayList<String>();

    TestDescription() {
      setId(UUID.randomUUID().toString());
    }

    public String getInput() {
      return input;
    }

    @Consumes
    public void setInput(final String input) {
      this.input = input;
    }

    public String getOutput() {
      return output;
    }

    @Produces
    public void setOutput(final String output) {
      this.output = output;
    }

    @Consumes
    public void setListInputs(final List<String> listInputs) {
      this.listInputs = listInputs;
    }

    public List<String> getListInputs() {
      return listInputs;
    }

    @Consumes
    public void addListInput(final String listInput) {
      listInputs.add(listInput);
    }

    void addDependency(final TestDescription onDescription) {
      getDependencies().add(ActivityDependency.Builder.on(onDescription).consumes("output").produces("input").build());
    }

    @Consumes
    public List<String> getListInputs2() {
      return listInputs2;
    }

    @Consumes
    public void setListInputs2(final List<String> listInputs2) {
      this.listInputs2 = listInputs2;
    }

    public void setListInputs2(final String input, final int index) {
      // Pad list with nulls so we can set the appropriate index
      while(this.listInputs2.size() <= index) {
        this.listInputs2.add(null);
      }
      this.listInputs2.set(index, input);
    }

  }

  private final ActivityFactory<ActivityDescription> factory = new ActivityFactory<ActivityDescription>() {
    public Activity getActivity(final ActivityDescription activityDescription, final ActivityContext activityContext) {
      return activityMap.get(activityDescription);
    }
  };
  private Map<ActivityDescription, Activity> activityMap;
  private ActivityDirectorImpl director;
  private MockActivityService activityService;
  private MockWorkflowListener mockWorkflowListener;

  class MockWorkflowListener implements WorkflowListener {
    private CountDownLatch completionLatch = new CountDownLatch(1);
    private Map<String, Boolean> jobCompleteMap = Maps.newHashMap();
    private boolean finishedProperly;

    public void workflowComplete(final ActivityContext activityContext, final boolean finishedProperly) {
      this.finishedProperly = finishedProperly;
      completionLatch.countDown();
    }

    public void assertFinishesProperly() {
      completionLatch.await();
      assert finishedProperly;
    }

    public void assertNotFinishesProperly() {
      completionLatch.await();
      assert !finishedProperly;
    }

    public void jobComplete(final ActivityContext activityContext, final JobDescription job, final boolean finishedProperly) {
      jobCompleteMap.put(job.getId(), finishedProperly);
    }

    public void activityComplete(final ActivityContext activityContext, final ActivityDescription activityDescription, final boolean finishedProperly) {
    }

    public void activityStarted(final ActivityContext activityContext, final ActivityDescription activityDescritpion) {
    }

  }

  @BeforeMethod(groups = "unit")
  public void init() {
    activityMap = Maps.newHashMap();
    director = new ActivityDirectorImpl();
    mockWorkflowListener = new MockWorkflowListener();
    final ActivityExecutorImpl aExecutor = new ActivityExecutorImpl();
    aExecutor.setActivityFactory(factory);
    director.setActivityExecutor(aExecutor);
    director.setExecutor(Executors.getNewThreadExecutor());
    activityService = new MockActivityService();
    director.setActivityService(activityService);
    director.setWorkflowListener(mockWorkflowListener);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void simpleDependentActivities() {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d2.addDependency(d1);
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);
    assert activityService.savedCount > 0;
    a1.start.await();
    a1.finish.countDown();
    a2.start.await();
    assert d2.getOutput().equals("moo");
    a2.finish.countDown();
    mockWorkflowListener.assertFinishesProperly();
    assert activityService.statusSnapshots.size() == 3;
    assert Iterables.all(activityService.statusSnapshots.get(0).values(), Predicates.equalTo(ActivityStatus.WAITING));
    assert Iterables.contains(activityService.statusSnapshots.get(1).values(), ActivityStatus.COMPLETE);
    assert Iterables.contains(activityService.statusSnapshots.get(1).values(), ActivityStatus.WAITING);
    assert Iterables.all(activityService.statusSnapshots.get(2).values(), Predicates.equalTo(ActivityStatus.COMPLETE));
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testJobCompleteListening() {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    final TestDescription d3 = new TestDescription(), d4 = new TestDescription();
    d2.addDependency(d1);
    d4.addDependency(d3);
    final JobDescription job1 = new JobDescription();
    final JobDescription job2 = new JobDescription();

    d1.setJobDescription(job1);
    d2.setJobDescription(job1);
    d3.setJobDescription(job2);
    d4.setJobDescription(job2);

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    final WaitingActivity a3 = new WaitingActivity(), a4 = new WaitingActivity();
    a4.throwException = true;
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    activityMap.put(d4, a4);

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3, d4));
    director.execute(context);
    a1.finish.countDown();
    a2.finish.countDown();
    a3.finish.countDown();
    a4.finish.countDown();

    mockWorkflowListener.assertNotFinishesProperly();
    assert mockWorkflowListener.jobCompleteMap.get(job1.getId());
    assert !mockWorkflowListener.jobCompleteMap.get(job2.getId());
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testStartup() {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d1.setActivityStatus(ActivityStatus.COMPLETE);
    d2.setActivityStatus(ActivityStatus.RUNNING);
    d2.addDependency(d1);
    final WaitingActivity a2 = new WaitingActivity();
    activityMap.put(d2, a2);
    final ActivityContext context = new ActivityContext();
    context.setId(UUID.randomUUID().toString()); // Unlike new contexts, loaded ones should already have an id
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    activityService.existingContexts = Lists.newArrayList(context);
    director.init();
    a2.start.await();
    a2.finish.countDown();
    mockWorkflowListener.assertFinishesProperly();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testActivityShutdown() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d2.addDependency(d1);
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    a1.shutdown = true;
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);

    a1.start.await();
    a1.finish.countDown();
    Thread.sleep(10);
    assert !a2.ran;
    assert d1.getActivityStatus().equals(ActivityStatus.RUNNING);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testDirectorShutdownOnComplete() throws Exception {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d2.addDependency(d1);
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);
    a1.start.await();
    director.destroy();
    a1.finish.countDown();
    Thread.sleep(10);
    assert !a2.ran;
    assert activityService.statusSnapshots.get(1).get(d1) == ActivityStatus.COMPLETE;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testDirectorShutdownOnFailure() throws Exception {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    final TestDescription d3 = new TestDescription(), d4 = new TestDescription();
    final TestDescription d5 = new TestDescription();
    d2.addDependency(d1);
    d4.addDependency(d3);
    d5.addDependency(d2);
    d5.addDependency(d4);
    final JobDescription job1 = new JobDescription();
    final JobDescription job2 = new JobDescription();
    final JobDescription job3 = new JobDescription();

    d1.setJobDescription(job1);
    d1.setJobDescription(job1);
    d2.setJobDescription(job2);
    d2.setJobDescription(job2);
    d5.setJobDescription(job3);

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    a1.throwException = true;
    final WaitingActivity a3 = new WaitingActivity(), a4 = new WaitingActivity();
    final WaitingActivity a5 = new WaitingActivity();

    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    activityMap.put(d4, a4);
    activityMap.put(d5, a5);
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3, d4, d5));
    director.execute(context);
    a1.start.await();
    a3.start.await();
    director.destroy();
    a1.finish.countDown();
    a3.finish.countDown();
    while(activityService.statusSnapshots.size() < 3) {
      Thread.sleep(5);
    }

    assert !a2.ran;
    assert !a4.ran;
    assert activityService.statusSnapshots.get(2).get(d1) == ActivityStatus.FAILED;
    assert activityService.statusSnapshots.get(2).get(d2) == ActivityStatus.CANCELLED;
    assert activityService.statusSnapshots.get(2).get(d5) == ActivityStatus.CANCELLED;
    assert activityService.statusSnapshots.get(2).get(d3) == ActivityStatus.COMPLETE;
    assert activityService.statusSnapshots.get(2).get(d4) == ActivityStatus.WAITING;
  }

  @Test(groups = "unit", timeOut = 10000)
  public void multipleDependentActivities() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription(), d4 = new TestDescription();
    d2.addDependency(d1);
    d3.addDependency(d1);
    d4.addDependency(ActivityDependency.Builder.on(d2).consumes("output").produces("output").build());
    d4.addDependency(ActivityDependency.Builder.on(d3).consumes("output").produces("output").build());

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity(), a3 = new WaitingActivity(), a4 = new WaitingActivity();
    d1.setInput("moo");
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    activityMap.put(d4, a4);
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3, d4));
    director.execute(context);
    a1.start.await();
    a1.finish.countDown();
    a2.start.await();
    assert d2.getOutput().equals("moo");
    a2.finish.countDown();
    a3.start.await();
    assert !a4.ran;
    a3.finish.countDown();
    a4.start.await();
    a4.finish.countDown();
    mockWorkflowListener.assertFinishesProperly();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void addListProducerDependency() {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d1.setListInputs(Lists.newArrayList("moo", "cow"));
    d2.getDependencies().add(ActivityDependency.Builder.on(d1).produces("listInputs").withIndex(0).consumes("input").build());
    d3.getDependencies().add(ActivityDependency.Builder.on(d1).produces("listInputs").withIndex(1).consumes("input").build());

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity(), a3 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a1.start.await();
    a1.finish.countDown();
    a2.start.await();
    a2.finish.countDown();
    a3.start.await();
    a3.finish.countDown();
    mockWorkflowListener.assertFinishesProperly();

    assert d2.getInput().equals("moo");
    assert d3.getInput().equals("cow");
  }

  @Test(groups = "unit", timeOut = 10000)
  public void addDependency() {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d3.getDependencies().add(ActivityDependency.Builder.on(d1).consumes("listInput").produces("output").build());
    d3.getDependencies().add(ActivityDependency.Builder.on(d2).consumes("listInput").produces("output").build());
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity(), a3 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);

    d1.setOutput("o1");
    d2.setOutput("o2");

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a1.start.await();
    a2.start.await();
    a1.finish.countDown();
    a2.finish.countDown();

    a3.start.await();
    a3.finish.countDown();
    assert d3.getListInputs().contains("o1");
    assert d3.getListInputs().contains("o2");
    mockWorkflowListener.assertFinishesProperly();
  }

  @Test(groups = "unit", timeOut = 5000) 
  public void testConsumesIndex() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d1.setOutput("d1Output");
    d2.setOutput("d2Output");
    
    
    d3.getDependencies().add(ActivityDependency.Builder.on(d1).consumesWithIndex("listInputs2", 0).produces("output").build());
    d3.getDependencies().add(ActivityDependency.Builder.on(d2).consumesWithIndex("listInputs2", 1).produces("output").build());
    
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity(), a3 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a1.start.await();
    a2.start.await();
    a2.finish.countDown();
    while(true) {
      if(!d3.getListInputs2().isEmpty()) {
        break;
      }
      Thread.sleep(1);
    }
    a1.finish.countDown();

    a3.start.await();
    a3.finish.countDown();
    assert d3.getListInputs2().contains("d1Output");
    assert d3.getListInputs2().contains("d2Output");
    int d1OutputIndex =  d3.getListInputs2().indexOf("d1Output");
    int d2OutputIndex =  d3.getListInputs2().indexOf("d2Output");
    assert d1OutputIndex < d2OutputIndex : String.format("Expected d1 index before d2, %d < %d", d1OutputIndex, d2OutputIndex);
    mockWorkflowListener.assertFinishesProperly();    
  }
  
  @Test(groups = "unit", timeOut = 10000)
  public void verifyConsumesIndexNeed() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d1.setOutput("d1Output");
    d2.setOutput("d2Output");
    
    d3.getDependencies().add(ActivityDependency.Builder.on(d1).consumes("listInput").produces("output").build());
    d3.getDependencies().add(ActivityDependency.Builder.on(d2).consumes("listInput").produces("output").build());
    
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity(), a3 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a1.start.await();
    a2.start.await();
    a2.finish.countDown();
    while(true) {
      if(!d3.getListInputs().isEmpty()) {
        break;
      }
      Thread.sleep(1);
    }
    a1.finish.countDown();

    a3.start.await();
    a3.finish.countDown();
    assert d3.getListInputs().contains("d1Output");
    assert d3.getListInputs().contains("d2Output");
    int d1OutputIndex =  d3.getListInputs().indexOf("d1Output");
    int d2OutputIndex =  d3.getListInputs().indexOf("d2Output");
    assert d1OutputIndex > d2OutputIndex : String.format("Expected d1 index after d2, %d < %d", d1OutputIndex, d2OutputIndex);
    mockWorkflowListener.assertFinishesProperly();
    

    
  }

  /**
   * This test verifies that activities corresponding to job descriptions that have been completed
   * are not rolled back.
   */
  @Test(groups = "unit", timeOut = 10000)
  public void testPreserveJobs() {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d2.addDependency(d1);
    d3.addDependency(d2);

    final JobDescription j1 = new JobDescription();
    d1.setJobDescription(j1);
    d2.setJobDescription(j1);

    final JobDescription j2 = new JobDescription();
    d3.setJobDescription(j2);

    final WaitingActivity a1 = new WaitingActivity(false), a2 = new WaitingActivity(false), a3 = new WaitingActivity(false);
    a3.throwException = true;
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    d1.setInput("moo");

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a3.rolledBackLatch.await();
    mockWorkflowListener.assertNotFinishesProperly();
    assert a3.rolledBack;
    assert !a1.rolledBack;
    assert !a2.rolledBack;
  }

  /**
   * This test verifies that activities corresponding to job descriptions that have not been complete
   * are rolled back upon failure.
   */
  @Test(groups = "unit", timeOut = 10000, dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testPartialJobCompleteRollback(final boolean rollbackException) {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d2.addDependency(d1);
    d3.addDependency(d2);

    final JobDescription j1 = new JobDescription();
    d1.setJobDescription(j1);
    d2.setJobDescription(j1);
    d3.setJobDescription(j1);

    final WaitingActivity a1 = new WaitingActivity(false), a2 = new WaitingActivity(false), a3 = new WaitingActivity(false);
    a3.throwException = true;
    a3.rollbackException = rollbackException;
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    d1.setInput("moo");

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a3.rolledBackLatch.await();
    mockWorkflowListener.assertNotFinishesProperly();
    assert a3.rolledBack;
    assert a1.rolledBack;
    assert a2.rolledBack;
  }

  /**
   * This test verifies that activities corresponding to job descriptions that have not been complete
   * are rolled back upon failure.
   */
  @Test(groups = "unit", timeOut = 10000)
  public void testRollbackOrder() {
    final JobDescription j1 = new JobDescription();
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    final TestDescription d4 = new TestDescription(), d5 = new TestDescription(), d6 = new TestDescription();
    final TestDescription d7 = new TestDescription();

    /*
     * Dependency graph
     * 3-------\
     * / 6
     * 2 /
     * \ 4 - 5
     * \
     * 7
     * 1 //d1 is an island
     */
    d3.addDependency(d2);
    d4.addDependency(d2);
    d5.addDependency(d4);
    d6.addDependency(d3);
    d6.addDependency(d5);
    d7.addDependency(d4);

    final WaitingActivity a1 = new WaitingActivity(false), a2 = new WaitingActivity(false), a3 = new WaitingActivity(false);
    final WaitingActivity a4 = new WaitingActivity(false), a5 = new WaitingActivity(false), a6 = new WaitingActivity(false);
    final WaitingActivity a7 = new WaitingActivity(true);
    a7.throwException = true;

    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    activityMap.put(d4, a4);
    activityMap.put(d5, a5);
    activityMap.put(d6, a6);
    activityMap.put(d7, a7);
    for(ActivityDescription description : activityMap.keySet()) {
      description.setJobDescription(j1);
    }
    d1.setInput("moo");

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3, d4, d5, d6, d7));
    director.execute(context);

    // a7 waits for its finish countdown, make sure everything else has finished first (that way everything gets rollbacked).
    a1.start.await();
    a6.start.await();
    a7.finish.countDown();

    for(final Activity activity : activityMap.values()) {
      WaitingActivity waitingActivity = (WaitingActivity) activity;
      waitingActivity.rolledBackLatch.await();
    }

    mockWorkflowListener.assertNotFinishesProperly();

    a2.assertRolledBackBefore(a3);
    a2.assertRolledBackBefore(a4);
    a3.assertRolledBackBefore(a6);
    a4.assertRolledBackBefore(a5);
    a4.assertRolledBackBefore(a7);
    a5.assertRolledBackBefore(a6);
  }

  @Test(groups = "unit", timeOut = 10000)
  public void testActivitiesWithNoJobDescriptionNotRolledBack() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d2.addDependency(d1);

    final WaitingActivity a1 = new WaitingActivity(false), a2 = new WaitingActivity(false);
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    a2.throwException = true;
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);
    mockWorkflowListener.assertNotFinishesProperly();
    assert !a1.rolledBack;
    assert !a2.rolledBack;
  }

  @Test(groups = "unit", timeOut = 10000)
  public void dependentFailure() throws InterruptedException {
    final JobDescription j1 = new JobDescription();
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d1.setJobDescription(j1);
    d2.setJobDescription(j1);
    d2.addDependency(d1);

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    a1.throwException = true;
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);
    a1.start.await();
    a1.finish.countDown();
    mockWorkflowListener.assertNotFinishesProperly();
    a1.rolledBackLatch.await();
    assert a1.rolledBack;
    assert !a2.rolledBack;

    // Verify on first save they were all waiting, on second save one had failed and the other was cancelled
    assert activityService.statusSnapshots.size() == 2;
    assert Iterables.all(activityService.statusSnapshots.get(0).values(), Predicates.equalTo(ActivityStatus.WAITING));
    assert Iterables.contains(activityService.statusSnapshots.get(1).values(), ActivityStatus.FAILED);
    assert Iterables.contains(activityService.statusSnapshots.get(1).values(), ActivityStatus.CANCELLED);
  }

  @Test(groups = "unit", timeOut = 10000)
  public void testSaveOnCancel() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription(), d3 = new TestDescription();
    d2.addDependency(d1);

    final JobDescription j1 = new JobDescription();
    d1.setJobDescription(j1);
    d2.setJobDescription(j1);

    final JobDescription j2 = new JobDescription();
    d3.setJobDescription(j2);

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity(), a3 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3));
    director.execute(context);
    a1.start.await();
    a3.start.await();
    director.cancelJob(context.getId(), j1.getId());

    assert activityService.statusSnapshots.size() == 2;
    final Map<ActivityDescription, ActivityStatus> statusesAfterSave = activityService.statusSnapshots.get(1);
    assert statusesAfterSave.get(d1).equals(ActivityStatus.RUNNING) : statusesAfterSave.get(d1);
    assert statusesAfterSave.get(d2).equals(ActivityStatus.CANCELLED);
    assert statusesAfterSave.get(d3).equals(ActivityStatus.RUNNING);

    a1.finish.countDown();
    a3.finish.countDown();

    mockWorkflowListener.assertNotFinishesProperly();
    a1.rolledBackLatch.await();
    assert a1.rolledBack;
    assert !a2.rolledBack;

    assert activityService.statusSnapshots.size() == 4;
    final Map<ActivityDescription, ActivityStatus> statusesAtEnd = activityService.statusSnapshots.get(3);
    assert statusesAtEnd.get(d1).equals(ActivityStatus.COMPLETE) : statusesAtEnd.get(d1);
    assert statusesAtEnd.get(d2).equals(ActivityStatus.CANCELLED);
    assert statusesAtEnd.get(d3).equals(ActivityStatus.COMPLETE);

  }

  @Test(groups = "unit", timeOut = 10000)
  public void testCancelWorkflowFinishProperly() throws InterruptedException {
    final JobDescription j1 = new JobDescription();
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d1.setJobDescription(j1);
    d2.setJobDescription(j1);

    d2.addDependency(d1);
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);
    a1.start.await();
    director.cancelWorkflow(context.getId());
    a1.cancelLatch.await();
    a1.finish.countDown();
    a1.rolledBackLatch.await();
    assert !mockWorkflowListener.jobCompleteMap.get(j1.getId());
    assert a1.rolledBack;
    mockWorkflowListener.assertNotFinishesProperly();
    assert !a2.ran;
  }

  @Test(groups = "unit", timeOut = 10000)
  public void testCancelJobFinishProperly() throws InterruptedException {
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    final TestDescription d3 = new TestDescription(), d4 = new TestDescription();
    final TestDescription d5 = new TestDescription(), d6 = new TestDescription();
    d2.addDependency(d1);
    d4.addDependency(d3);
    d5.addDependency(d2);
    d5.addDependency(d4);
    d6.addDependency(d3);
    final JobDescription job1 = new JobDescription();
    final JobDescription job2 = new JobDescription();
    final JobDescription job3 = new JobDescription();

    d1.setJobDescription(job1);
    d1.setJobDescription(job1);
    d2.setJobDescription(job2);
    d2.setJobDescription(job2);
    d5.setJobDescription(job3);
    d6.setJobDescription(job3);

    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    final WaitingActivity a3 = new WaitingActivity(), a4 = new WaitingActivity();
    final WaitingActivity a5 = new WaitingActivity(), a6 = new WaitingActivity();

    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    activityMap.put(d3, a3);
    activityMap.put(d4, a4);
    activityMap.put(d5, a5);
    activityMap.put(d6, a6);

    d1.setInput("moo");
    d3.setInput("moo2");

    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2, d3, d4, d5));
    director.execute(context);
    a1.start.await();
    a3.start.await();
    director.cancelJob(context.getId(), job1.getId());
    // Cancelling job1 should cause d2 not to run, but d4 should still run
    a1.cancelLatch.await();
    a1.finish.countDown();
    a3.finish.countDown();
    a4.start.await();
    a4.finish.countDown();

    mockWorkflowListener.assertNotFinishesProperly();

    assert a1.cancelled;
    assert !a3.cancelled;
    assert !a2.ran;
    assert a4.ran;
    assert !a5.ran;
    assert !a6.ran;
  }

  @Test(groups = "unit", timeOut = 10000)
  public void testCancelWorkflowAndFailed() throws InterruptedException {
    final JobDescription j1 = new JobDescription();
    final TestDescription d1 = new TestDescription(), d2 = new TestDescription();
    d1.setJobDescription(j1);
    d2.setJobDescription(j1);
    d2.addDependency(d1);
    final WaitingActivity a1 = new WaitingActivity(), a2 = new WaitingActivity();
    a1.throwException = true;
    activityMap.put(d1, a1);
    activityMap.put(d2, a2);
    d1.setInput("moo");
    final ActivityContext context = new ActivityContext();
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1, d2));
    director.execute(context);
    a1.start.await();
    director.cancelWorkflow(context.getId());
    a1.cancelLatch.await();
    a1.finish.countDown();
    a1.rolledBackLatch.await();
    assert a1.rolledBack;
    mockWorkflowListener.assertNotFinishesProperly();
    assert !a2.ran;
  }

}
