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

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityStatus;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.Id;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.MergeScaffoldSamplesDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldQuantativeSample;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameter;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitGalaxyDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/jobs/test/testDatabaseContext.xml"})
@TransactionConfiguration(transactionManager = "jobsTransactionManager", defaultRollback = true)
public class DescriptionPersistenceTest extends AbstractTransactionalTestNGSpringContextTests {
  @PersistenceContext(unitName = "jobs")
  private EntityManager entityManager;

  @Inject
  private ActivityService activityService;

  @Test(groups = "unit")
  public void activityStatus() {
    final ActivityContext context = new ActivityContext();
    final ActivityDescription d1 = new UploadFileDescription();
    d1.setActivityStatus(ActivityStatus.CANCELLED);
    Set<ActivityDescription> activityDescriptions = Sets.newHashSet(d1);
    context.setActivityDescription(activityDescriptions);
    activityService.save(context);

    assert entityManager.find(ActivityDescription.class, d1.getId()).getActivityStatus() == ActivityStatus.CANCELLED;
  }

  @Test(groups = "unit")
  public void testSubmitGalaxyDescription() {
    final ActivityContext context = new ActivityContext();
    final SubmitGalaxyDescription description = new SubmitGalaxyDescription();

    final RootInput rootInput = new RootInput();
    final Input i1 = new Input();
    i1.setName("i1");
    i1.setValue("vi1");

    final Input i2 = new Input();
    i2.setName("i2");
    i2.setValue("vi2");

    final Input i11 = new Input();
    i11.setName("i11");
    i11.setValue("vi11");
    i1.getInput().add(i11);

    rootInput.getInput().add(i1);
    rootInput.getInput().add(i2);
    description.setInput(rootInput);

    Set<ActivityDescription> activityDescriptions = Sets.<ActivityDescription>newHashSet(description);
    context.setActivityDescription(activityDescriptions);
    activityService.save(context);

    final SubmitGalaxyDescription d = entityManager.find(SubmitGalaxyDescription.class, description.getId());
    final RootInput loadedInput = d.getInput();
    assert loadedInput.getInput().size() == 2 : loadedInput.getInput().size();
    int i1Idx = 0, i2Idx = 1;
    if(!loadedInput.getInput().get(0).getName().equals("i1")) {
      i1Idx = 1;
      i2Idx = 0;
    }
    assert loadedInput.getInput().get(i1Idx).getInput().get(0).getName().equals("i11");
    assert loadedInput.getInput().get(i1Idx).getInput().get(0).getValue().equals("vi11");
    assert loadedInput.getInput().get(i2Idx).getName().equals("i2");
    assert loadedInput.getInput().get(i2Idx).getValue().equals("vi2");
  }

  @Test(groups = "unit")
  public void idListOrderingTest() {
    // final List<String> ids = Lists.newArrayList("3", "2", "1");

    final IdList idList = new IdList();
    final Id id1 = new Id();
    id1.setValue("3");
    id1.setIndex(0);

    final Id id2 = new Id();
    id2.setValue("1");
    id2.setIndex(1);

    final Id id3 = new Id();
    id3.setValue("2");
    id3.setIndex(2);

    // Don't put them in the list in the correct index order
    idList.setIds(Lists.newArrayList(id3, id1, id2));

    entityManager.persist(idList);
    entityManager.flush();
    entityManager.clear();

    final IdList loadedList = entityManager.find(IdList.class, idList.getId());
    final List<Id> loadedIds = loadedList.getIds();
    // Verify they still came out in the right order
    assert loadedIds.get(0).getValue().equals("3");
    assert loadedIds.get(1).getValue().equals("1");
    assert loadedIds.get(2).getValue().equals("2");
    entityManager.clear();

    final Id id4 = new Id();
    id4.setValue("3");

    final Id id5 = new Id();
    id5.setValue("1");

    final Id id6 = new Id();
    id6.setValue("2");

    final IdList fromCollectionIdList = IdList.forIterable(Lists.newArrayList("3", "2", "1"));
    assert fromCollectionIdList.getIds().get(0).getIndex() < fromCollectionIdList.getIds().get(1).getIndex();
    assert fromCollectionIdList.getIds().get(1).getIndex() < fromCollectionIdList.getIds().get(2).getIndex();
  }

  @Test(groups = "unit")
  public void testSaveScaffoldWithQuant() {
    final ActivityContext context = new ActivityContext();
    final ScaffoldSample sample = new ScaffoldSample();
    final ScaffoldQuantativeSample quantSample = new ScaffoldQuantativeSample();
    sample.setQuantitativeSamples(Lists.newArrayList(quantSample));
    final CreateScaffoldDriverDescription description = new CreateScaffoldDriverDescription();
    description.getScaffoldSamples().add(sample);
    Set<ActivityDescription> activityDescriptions = Sets.<ActivityDescription>newHashSet();
    context.setActivityDescription(activityDescriptions);
    activityService.save(context);

  }

  @Test(groups = "unit")
  public void saveIdentificationParameters() {
    final ActivityContext context = new ActivityContext();
    final CreateIdentificationParametersDescription d1 = new CreateIdentificationParametersDescription();

    StringParameterSet parameters = new StringParameterSet();
    StringParameter parameter1 = new StringParameter();
    parameter1.setKey("key1");
    parameter1.setValue("value1");
    StringParameter parameter2 = new StringParameter();
    parameter2.setKey("key2");
    parameter2.setValue("value2");
    parameters.addParameter(parameter1);
    parameters.addParameter(parameter2);
    d1.setParameters(parameters);
    Set<ActivityDescription> activityDescriptions = Sets.<ActivityDescription>newHashSet(d1);
    context.setActivityDescription(activityDescriptions);
    activityService.save(context);

    assert entityManager.createQuery("select p from StringParameter p").getResultList().size() == 2;
  }

  @Test(groups = "unit")
  public void saveCascadesToDescription() {
    final ActivityContext context = new ActivityContext();
    final ActivityDescription d1 = new UploadFileDescription();
    Set<ActivityDescription> activityDescriptions = Sets.newHashSet(d1);
    context.setActivityDescription(activityDescriptions);
    activityService.save(context);

    final UploadFileDescription desc = entityManager.find(UploadFileDescription.class, d1.getId());
    assert desc != d1; // Verify d1 is detached
    assert desc != null; // Verify a description object was found.

    final ActivityContext loadedContext = entityManager.find(ActivityContext.class, context.getId());
    assert loadedContext.getActivityDescriptions().iterator().next() instanceof UploadFileDescription;
  }

  @Test(groups = "unit")
  public void inheritenceTwoLevelsDeep() {
    final ActivityContext context = new ActivityContext();
    final ActivityDescription d1 = new CreateProteomicsRunDescription();
    final ActivityDescription d2 = new CreateTropixFileDescription();
    context.addDescription(d1);
    context.addDescription(d2);

    activityService.save(context);
    assert entityManager.find(CreateProteomicsRunDescription.class, d1.getId()) != null;

  }

  @Test(groups = "unit")
  public void saveCascadesToJobs() {
    final ActivityContext context = new ActivityContext();
    final JobDescription job1 = new JobDescription();
    job1.setName("New Job1");
    final JobDescription job2 = new JobDescription();
    job2.setName("New Job2");

    final ActivityDescription d1 = new CreateProteomicsRunDescription();
    d1.setJobDescription(job1);
    final ActivityDescription d2 = new CreateTropixFileDescription();
    d2.setJobDescription(job1);
    final ActivityDescription d3 = new CreateTropixFileDescription();
    d3.setJobDescription(job2);
    context.addDescription(d1);
    context.addDescription(d2);
    context.addDescription(d3);

    activityService.save(context);
    assert entityManager.createQuery("select j from JobDescription j").getResultList().size() == 2;
  }

  @Test(groups = "unit")
  public void saveAndUpdateScaffoldSamples() {
    final ActivityContext context = new ActivityContext();
    final JobDescription job1 = new JobDescription();
    job1.setName("New Job1");
    MergeScaffoldSamplesDescription d1 = new MergeScaffoldSamplesDescription();
    d1.setJobDescription(job1);
    context.setActivityDescription(Sets.<ActivityDescription>newHashSet(d1));

    activityService.save(context);

    assert entityManager.createQuery("select s from ScaffoldSample s where s.sampleName='test sample'").getResultList().size() == 0;
    assert entityManager.createQuery("select id from Id id where id.value='the name'").getResultList().size() == 0;

    d1.addIdentificationId("abc");
    d1.addName("the name");
    activityService.save(context);
    assert entityManager.createQuery("select id from Id id where id.value='the name'").getResultList().size() == 1;

    final ScaffoldSample s1 = new ScaffoldSample();
    s1.setAnalyzeAsMudpit(true);
    s1.setIdentificationAnalysisIds(IdList.forIterable(Lists.newArrayList("abc", "def")));
    s1.setSampleName("test sample");

    d1.setScaffoldSamples(Lists.newArrayList(s1));

    activityService.save(context);

    assert entityManager.createQuery("select s from ScaffoldSample s where s.sampleName='test sample'").getResultList().size() == 1;

    MergeScaffoldSamplesDescription loadedD1 = (MergeScaffoldSamplesDescription) entityManager.createQuery(
        "select d from MergeScaffoldSamplesDescription d where d.id='" + d1.getId() + "'").getSingleResult();
    assert loadedD1 != null;

    assert loadedD1.getScaffoldSamples().size() == 1;

  }

  @Test(groups = "unit")
  public void saveCascadesToDependencies() {
    final ActivityContext context = new ActivityContext();
    final ActivityDescription d1 = new UploadFileDescription();
    final ActivityDescription d2 = new UploadFileDescription();
    final ActivityDescription d3 = new UploadFileDescription();
    final ActivityDependency dep1 = ActivityDependency.Builder.on(d1).build();
    d2.setDependencies(Sets.newHashSet(dep1));
    final ActivityDependency dep2 = ActivityDependency.Builder.on(d2).build();
    d3.setDependencies(Sets.newHashSet(dep2));
    Set<ActivityDescription> activityDescriptions = Sets.newHashSet(d1, d2, d3);
    context.setActivityDescription(activityDescriptions);
    activityService.save(context);

    final UploadFileDescription desc = entityManager.find(UploadFileDescription.class, d1.getId());
    assert desc != d1; // Verify d1 is detached
    assert desc != null; // Verify a description object was found.

    assert entityManager.createQuery("select d from ActivityDescription d").getResultList().size() == 3;

    final ActivityContext loadedContext = entityManager.find(ActivityContext.class, context.getId());
    assert loadedContext.getActivityDescriptions().iterator().next() instanceof UploadFileDescription;
  }

}
