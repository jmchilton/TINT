package edu.umn.msi.tropix.webgui.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.test.ScaffoldParameterTestData;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.persistence.service.DatabaseService;
import edu.umn.msi.tropix.persistence.service.ProteomicsRunService;
import edu.umn.msi.tropix.proteomics.sequest.SequestParameterTestData;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.webgui.client.components.newwizards.IdentificationWorkflowBuilder;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ScaffoldSampleTypeWizardPageImpl.ScaffoldSampleType;
import edu.umn.msi.tropix.webgui.client.components.newwizards.TestCommonMetadataProvider;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantProxies;

public class IdentificationWorkflowIntegerationTest extends WebIntegrationTest {
  private IdentificationWorkflowBuilder workflowBuilder;

  @Override
  protected void initializeConfigDir(final ConfigDirBuilder builder) {
    // builder.createSubConfigDir("client").addDeployProperty("queue.staging.clean", "false");
  }

  public void setupWorkflowBuilder() throws Exception {
    logon();
    launchMessageProcessingThread();

    workflowBuilder = new IdentificationWorkflowBuilder(ConstantProxies.getProxy(ComponentConstants.class));
    workflowBuilder.setScaffoldType(ScaffoldSampleType.MANY_SAMPLE);
    workflowBuilder.setScaffoldParameterMap(ScaffoldParameterTestData.getTestParameters());

    setupDatabase();
    setupMetadataProvider();
    setupIdParameters();
    setupIdService();
  }

  @Test(groups = "integration")
  public void testIdentificationOnly() throws Exception {
    setupWorkflowBuilder();
    final ProteomicsRun run1 = createProteomicsRun("run1", "parentPerScan.mzxml");

    workflowBuilder.setUseScaffold(false);
    workflowBuilder.setUseExistingRuns(true);
    workflowBuilder.setSelectedRuns(Lists.newArrayList(run1));
    final Set<ActivityDescription> descriptions = workflowBuilder.build();
    super.submit(descriptions);

    final CreateIdentificationAnalysisDescription description = super.<CreateIdentificationAnalysisDescription>getActivityDescriptionOfType(
        CreateIdentificationAnalysisDescription.class, descriptions);
    assertActivityCompleteNormally(description);
    finishMessageProcessing();
  }

  @Test(groups = "integration")
  public void testSingleScaffold() throws Exception {
    setupWorkflowBuilder();
    final ProteomicsRun run1 = createProteomicsRun("run1", "parentPerScan.mzxml");

    workflowBuilder.setUseScaffold(true);
    workflowBuilder.setUseExistingRuns(true);
    workflowBuilder.setSelectedRuns(Lists.newArrayList(run1));

    setupScaffoldService();

    final Set<ActivityDescription> descriptions = workflowBuilder.build();
    super.submit(descriptions);

    final CreateScaffoldAnalysisDescription description = super.<CreateScaffoldAnalysisDescription>getActivityDescriptionOfType(
        CreateScaffoldAnalysisDescription.class, descriptions);
    assertActivityCompleteNormally(description);
    finishMessageProcessing();
  }

  @Test(groups = "integration", invocationCount = 10, skipFailedInvocations = true)
  public void testMultipleScaffoldSamples() throws Exception {
    setupWorkflowBuilder();
    final ProteomicsRun run1 = createProteomicsRun("run1", "readw.mzXML");
    final ProteomicsRun run2 = createProteomicsRun("run2", "parentPerScan.mzxml");

    workflowBuilder.setUseScaffold(true);
    workflowBuilder.setUseExistingRuns(true);
    workflowBuilder.setSelectedRuns(Lists.newArrayList(run1, run2));

    setupScaffoldService();

    final Set<ActivityDescription> descriptions = workflowBuilder.build();
    super.submit(descriptions);

    final CreateScaffoldAnalysisDescription description = super.<CreateScaffoldAnalysisDescription>getActivityDescriptionOfType(
        CreateScaffoldAnalysisDescription.class, descriptions);
    assertActivityCompleteNormally(description);

    final CreateIdentificationAnalysisDescription idDesc1 = getActivityDescriptionOfTypeWithNamePrefix(CreateIdentificationAnalysisDescription.class,
        descriptions, "run1");
    final CreateIdentificationAnalysisDescription idDesc2 = getActivityDescriptionOfTypeWithNamePrefix(CreateIdentificationAnalysisDescription.class,
        descriptions, "run2");
    final String id1Output = idDesc1.getAnalysisFileId();
    Preconditions.checkNotNull(id1Output);
    final String id2Output = idDesc2.getAnalysisFileId();
    Preconditions.checkNotNull(id2Output);
    final long id1Size = getSize(getDownloadContextForObjectId(id1Output));
    final long id2Size = getSize(getDownloadContextForObjectId(id2Output));

    assert id1Size != id2Size;

    final String outputFileId = description.getOutputFileId();

    final Properties properties = new Properties();
    properties.load(InputContexts.asInputStream(getDownloadContextForObjectId(outputFileId)));
    assert getActualResourceSize("run1", properties) == id1Size;
    assert getActualResourceSize("run2", properties) == id2Size;
    finishMessageProcessing();
  }

  private long getActualResourceSize(final String name, final Properties propertiesFromFakeScaffold) {
    final Iterable<String> keys = Maps.fromProperties(propertiesFromFakeScaffold).keySet();
    for(final String recordedFileName : keys) {
      if(recordedFileName.contains(name)) {
        return Long.parseLong(propertiesFromFakeScaffold.getProperty(recordedFileName));
      }
    }
    throw new IllegalStateException(String.format("Could not find name containing [%s] in keys [%s]", name, Iterables.toString(keys)));
  }

  private long getSize(final InputContext inputContext) throws IOException {
    final InputStream inputStream = InputContexts.asInputStream(inputContext);
    long size = 0;
    while(inputStream.read() != -1) {
      size++;
    }
    return size;
  }

  private long getExpectedResourceSize(final String resourceId) throws IOException {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream(resourceId);
    long size = 0;
    while(inputStream.read() != -1) {
      size++;
    }
    return size;
  }

  @Inject
  private ProteomicsRunService proteomicsRunService;

  protected ProteomicsRun createProteomicsRun(final String name, final String resourceId) {
    final ModelStorageData storageData = newPersistedStorageData(name + ".RAW");
    storageData.getUploadContext().put(ProteomicsTests.getResourceAsStream(resourceId));
    final String mzxmlId = storageData.getTropixFile().getId();
    final ProteomicsRun run = new ProteomicsRun();
    run.setName(name);
    run.setSource(storageData.getTropixFile());
    run.setMzxml(storageData.getTropixFile());
    return proteomicsRunService.createProteomicsRun(getUserGridId(), getUserHomeFolderId(), run, mzxmlId, null, mzxmlId);
  }

  @Inject
  private DatabaseService databaseService;

  protected Database createDatabase() {
    final ModelStorageData storageData = newPersistedStorageData("test.fasta");
    storageData.getUploadContext().put("> a\nABCD".getBytes());
    final String mzxmlId = storageData.getTropixFile().getId();
    final Database database = new Database();
    database.setName("test");
    database.setType("FASTA");
    database.setCommitted(true);
    return databaseService.createDatabase(getUserGridId(), getUserHomeFolderId(), database, mzxmlId);
  }

  private void setupIdService() {
    final IdentificationGridService idService = new IdentificationGridService();
    idService.setParameterType(IdentificationType.SEQUEST.getParameterType());
    idService.setServiceAddress("local://Sequest");
    workflowBuilder.setIdService(idService);
  }

  private void setupScaffoldService() {
    final ScaffoldGridService scaffoldService = new ScaffoldGridService();
    scaffoldService.setScaffoldVersion("3");
    scaffoldService.setServiceAddress("local://Scaffold");
    workflowBuilder.setScaffoldGridService(scaffoldService);
  }

  private void setupIdParameters() throws Exception {
    final Map<String, String> idParameters = SequestParameterTestData.getSimpleSequestParametersMap();
    idParameters.put("enzymeName", "Trypsin");
    workflowBuilder.setParameterMap(idParameters);
  }

  private void setupMetadataProvider() {
    final CommonMetadataProvider commonMetadataProvider = new TestCommonMetadataProvider(getUserHomeFolderId());
    workflowBuilder.setCommonMetadataProvider(commonMetadataProvider);
  }

  private void setupDatabase() {
    final Database database = createDatabase();
    workflowBuilder.setDatabaseId(database.getId());
  }

}
