package edu.umn.msi.tropix.webgui.server;

import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.proteomics.sequest.impl.SequestParameterBeanTest;
import edu.umn.msi.tropix.webgui.client.components.newwizards.IdentificationWorkflowBuilder;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ScaffoldSampleTypeWizardPageImpl.ScaffoldSampleType;
import edu.umn.msi.tropix.webgui.client.components.newwizards.TestCommonMetadataProvider;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantProxies;

public class IdentificationWorkflowIntegerationTest extends WebIntegrationTest {

  @Test(groups = "integration")
  public void testIdentificationOnly() throws Exception {
    logon();
    launchMessageProcessingThread();
    final ProteomicsRun run1 = createProteomicsRun("run1", "parentPerScan.mzxml");
    final Database database = createDatabase();

    final IdentificationWorkflowBuilder workflowBuilder = new IdentificationWorkflowBuilder(ConstantProxies.getProxy(ComponentConstants.class));
    workflowBuilder.setUseScaffold(false);
    workflowBuilder.setUseExistingRuns(true);
    workflowBuilder.setSelectedRuns(Lists.newArrayList(run1));
    final Map<String, String> idParameters = SequestParameterBeanTest.getSimpleSequestParametersMap();
    idParameters.put("enzymeName", "Trypsin");

    workflowBuilder.setParameterMap(idParameters);
    workflowBuilder.setScaffoldType(ScaffoldSampleType.MANY_ANALYSIS);
    final CommonMetadataProvider commonMetadataProvider = new TestCommonMetadataProvider(getUserHomeFolderId());
    workflowBuilder.setCommonMetadataProvider(commonMetadataProvider);
    final IdentificationGridService idService = new IdentificationGridService();
    idService.setParameterType(IdentificationType.SEQUEST.getParameterType());
    idService.setServiceAddress("local://Sequest");

    workflowBuilder.setIdService(idService);
    workflowBuilder.setDatabaseId(database.getId());
    final Set<ActivityDescription> descriptions = workflowBuilder.build();
    super.submit(descriptions);

    final CreateIdentificationAnalysisDescription description = super.<CreateIdentificationAnalysisDescription>getActivityDescriptionOfType(
        CreateIdentificationAnalysisDescription.class, descriptions);
    assertActivityCompleteNormally(description);
    finishMessageProcessing();
  }

}
