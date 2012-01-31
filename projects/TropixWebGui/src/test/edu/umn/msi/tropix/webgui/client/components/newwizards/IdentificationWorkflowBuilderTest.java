package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.MergeScaffoldSamplesDescription;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ScaffoldSampleTypeWizardPageImpl.ScaffoldSampleType;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantProxies;

public class IdentificationWorkflowBuilderTest extends BaseWorkflowBuilderTest<IdentificationWorkflowBuilder> {
  private IdentificationGridService idService;
  private QueueGridService rawExtractService;
  private ScaffoldGridService scaffoldGridService;
  private List<ProteomicsRun> runs = Lists.newArrayList();
  private int testRunIndex;
  private String databaseId;
  private Map<String, String> identificationParameters;
  private Map<String, String> scaffoldParameters;
  private Map<String, String> uploadedRunMap;
  ScaffoldSampleType scaffoldType = ScaffoldSampleType.MANY_SAMPLE;

  @BeforeMethod(groups = "unit")
  public void init() {
    final IdentificationWorkflowBuilder builder = new IdentificationWorkflowBuilder(ConstantProxies.getProxy(ComponentConstants.class));
    setWorkflowBuilder(builder);

    testRunIndex = 0;

    // Setup ID Service
    idService = TestGridServices.getTestService(IdentificationGridService.class);
    builder.setIdService(idService);

    rawExtractService = TestGridServices.getTestService(QueueGridService.class);
    builder.setRawExtractGridService(rawExtractService);

    scaffoldGridService = TestGridServices.getTestService(ScaffoldGridService.class);
    builder.setScaffoldGridService(scaffoldGridService);

    builder.setSelectedRuns(runs);
    builder.setUseExistingRuns(false);
    builder.setUseScaffold(true);
    builder.setCreateSubfolders(false);
    CommonMetadataProvider metadataProvider = new TestCommonMetadataProvider();
    builder.setCommonMetadataProvider(metadataProvider);
    identificationParameters = Maps.newHashMap();
    builder.setParameterMap(identificationParameters);
    databaseId = UUID.randomUUID().toString();
    builder.setDatabaseId(databaseId);
    uploadedRunMap = Maps.newHashMap();
    builder.setUploadedRuns(uploadedRunMap);
    scaffoldParameters = Maps.newHashMap();
    builder.setScaffoldParameterMap(scaffoldParameters);
  }

  private void useExistingRuns() {
    getWorkflowBuilder().setUseExistingRuns(true);
  }

  private void noScaffold() {
    getWorkflowBuilder().setUseScaffold(false);
  }

  private void createSubfolders() {
    getWorkflowBuilder().setCreateSubfolders(true);
  }

  @Override
  protected void buildAndVerify() {
    getWorkflowBuilder().setScaffoldType(scaffoldType);
    super.buildAndVerify();
  }

  @Test
  public void testNoScaffoldExistingRun() {
    useExistingRuns();
    registerNewTestRun();
    noScaffold();
    buildAndVerify();
  }

  @Test
  public void testNoScaffoldExistingRuns() {
    useExistingRuns();
    registerNewTestRun();
    registerNewTestRun();
    noScaffold();
    buildAndVerify();
  }

  @Test
  public void testNoScaffoldNewRun() {
    registerNewUpload();
    noScaffold();
    buildAndVerify();
  }

  @Test
  public void testNoScaffoldNewRuns() {
    registerNewUpload();
    registerNewUpload();
    noScaffold();
    buildAndVerify();
  }

  @Test
  public void testManyAnalysesScaffold() {
    registerNewUpload();
    registerNewUpload();
    scaffoldType = ScaffoldSampleType.MANY_ANALYSIS;
    buildAndVerify();
    assertBuiltNDescriptionsOfType(2, CreateScaffoldDriverDescription.class);
    assertBuiltNDescriptionsOfType(2, MergeScaffoldSamplesDescription.class);
    assertBuiltNDescriptionsOfType(2, CreateScaffoldAnalysisDescription.class);
  }

  @Test
  public void testManySamples() {
    registerNewUpload();
    registerNewUpload();
    scaffoldType = ScaffoldSampleType.MANY_SAMPLE;
    buildAndVerify();
    assertBuiltNDescriptionsOfType(1, MergeScaffoldSamplesDescription.class);
    assertBuiltNDescriptionsOfType(1, CreateScaffoldAnalysisDescription.class);
  }

  @Test
  public void testOneSample() {
    registerNewUpload();
    registerNewUpload();
    scaffoldType = ScaffoldSampleType.ONE_SAMPLE;
    buildAndVerify();
    assertBuiltNDescriptionsOfType(1, MergeScaffoldSamplesDescription.class);
    assertBuiltNDescriptionsOfType(1, CreateScaffoldAnalysisDescription.class);
  }

  private void registerNewUpload() {
    final String id = "testid" + testRunIndex;
    final String name = "testRun" + testRunIndex++;
    uploadedRunMap.put(name, id);
  }

  private ProteomicsRun registerNewTestRun() {
    final ProteomicsRun run = new ProteomicsRun();
    run.setName("testRun" + testRunIndex++);
    runs.add(run);
    return run;
  }

}
