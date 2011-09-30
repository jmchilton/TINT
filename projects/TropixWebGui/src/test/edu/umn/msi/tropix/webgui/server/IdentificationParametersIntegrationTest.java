package edu.umn.msi.tropix.webgui.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationParametersService;

public class IdentificationParametersIntegrationTest extends WebIntegrationTest {

  @Inject
  private IdentificationParametersService identificationParametersService;

  private void testParameters(final IdentificationType parameterType, final Map<String, String> inputParameters) {
    final JobDescription jobDescription = newJobDescription();

    final CreateIdentificationParametersDescription createParametersDescription = new CreateIdentificationParametersDescription();
    createParametersDescription.setJobDescription(jobDescription);
    createParametersDescription.setName("param name");
    createParametersDescription.setParameters(StringParameterSet.fromMap(inputParameters));
    createParametersDescription.setParameterType(parameterType.getParameterType());
    createParametersDescription.setDestinationId(getUserHomeFolderId());

    final Set<ActivityDescription> descriptions = new HashSet<ActivityDescription>();
    descriptions.add(createParametersDescription);
    descriptions.add(ActivityDescriptions.createCommitDescription(createParametersDescription));
    submit(descriptions);

    assertJobCompleteNormally(jobDescription);

    final IdentificationParameters parameters = (IdentificationParameters) loadLastAddedObject();
    assert parameters.getType().equals(parameterType.getParameterType());

    final Map<String, String> loadedParametersMap = identificationParametersService.getParameterMap(parameters.getId());
    for(Map.Entry<String, String> inputParameterEntry : inputParameters.entrySet()) {
      assert loadedParametersMap.get(inputParameterEntry.getKey()).equals(inputParameterEntry.getValue());
    }

  }

  @Test(groups = "integration")
  public void saveAndLoad() throws InterruptedException {
    logon();
    launchMessageProcessingThread();
    testParameters(IdentificationType.MYRIMATCH, ImmutableMap.<String, String>builder().put("classSizeMultiplier", "4").build());
    testParameters(IdentificationType.SEQUEST, ImmutableMap.<String, String>builder().put("enzymeName", "Trypsin").build());
    testParameters(IdentificationType.OMSSA, ImmutableMap.<String, String>builder().put("doublenum", "5").build());
    finishMessageProcessing();
  }
}
