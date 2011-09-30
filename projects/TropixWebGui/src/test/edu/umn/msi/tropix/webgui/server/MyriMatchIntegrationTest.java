package edu.umn.msi.tropix.webgui.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.proteomics.myrimatch.impl.MockMyriMatchConfigCreator;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore.TempFileInfo;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class MyriMatchIntegrationTest extends WebIntegrationTest {

  @Inject
  private TempFileStore tempFileStore;

  @Override
  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {
    new MockMyriMatchConfigCreator(configDirBuilder).build();
  }

  @Test(groups = "integration")
  public void myriMatchIntegration() throws InterruptedException {
    logon();
    launchMessageProcessingThread();

    final TempFileInfo tempFile = tempFileStore.getTempFileInfo("moo");
    OutputContexts.forFile(tempFile.getTempLocation()).put("mzxml".getBytes());

    final TempFileInfo dbTempFile = tempFileStore.getTempFileInfo("db");
    OutputContexts.forFile(dbTempFile.getTempLocation()).put("database".getBytes());

    final JobDescription jobDescription = newJobDescription();

    final UploadFileDescription uploadDescription = new UploadFileDescription();
    uploadDescription.setJobDescription(jobDescription);
    uploadDescription.setInputFilePath(tempFile.getId());

    final CreateTropixFileDescription fileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, true);

    final UploadFileDescription dbUploadDescription = new UploadFileDescription();
    dbUploadDescription.setJobDescription(jobDescription);
    dbUploadDescription.setInputFilePath(dbTempFile.getId());

    final CreateTropixFileDescription dbFileDescription = ActivityDescriptions.createFileFromUpload(dbUploadDescription, true);

    final CreateProteomicsRunDescription createRun = new CreateProteomicsRunDescription();
    createRun.setName("New run");
    createRun.addDependency(ActivityDependency.Builder.on(fileDescription).produces("objectId").consumes("mzxmlFileId").build());

    final CreateDatabaseDescription createDatabase = new CreateDatabaseDescription();
    createDatabase.setDatabaseType("FASTA");
    createDatabase.setName("New Db");
    createDatabase.addDependency(ActivityDependency.Builder.on(dbFileDescription).produces("objectId").consumes("databaseFileId").build());

    final CreateIdentificationParametersDescription parameters = new CreateIdentificationParametersDescription();
    parameters.setJobDescription(jobDescription);
    parameters.setName("param name");
    final Map<String, String> parameterMap = Maps.newHashMap();
    parameterMap.put("maxResults", "6");
    parameters.setParameters(StringParameterSet.fromMap(parameterMap));
    parameters.setParameterType("MyriMatch");

    final SubmitIdentificationAnalysisDescription submit = new SubmitIdentificationAnalysisDescription();
    submit.setJobDescription(jobDescription);
    submit.setParameterType("MyriMatch");
    submit.setServiceUrl("local://MyriMatch");

    submit.addDependency(ActivityDependency.Builder.on(parameters).produces("parametersId").consumes("parametersId").build());
    submit.addDependency(ActivityDependency.Builder.on(createDatabase).produces("objectId").consumes("databaseId").build());
    submit.addDependency(ActivityDependency.Builder.on(createRun).produces("objectId").consumes("runId").build());
    submit.addDependency(ActivityDependency.Builder.on(parameters).produces("objectId").consumes("parametersId").build());

    final PollJobDescription pollDescription = ActivityDescriptions.buildPollDescription(submit);
    final CreateTropixFileDescription resultDescription = ActivityDescriptions.buildCreateResultFile(pollDescription);

    final Set<ActivityDescription> descriptions = new HashSet<ActivityDescription>();
    descriptions.add(createRun);
    descriptions.add(createDatabase);
    descriptions.add(dbFileDescription);
    descriptions.add(dbUploadDescription);
    descriptions.add(submit);
    descriptions.add(fileDescription);
    descriptions.add(parameters);
    descriptions.add(uploadDescription);
    descriptions.add(pollDescription);
    descriptions.add(resultDescription);
    submit(descriptions);

    final ProgressMessage progressMessage = waitForJobComplete(jobDescription);
    assert progressMessage.getJobStatus() == ProgressMessage.JOB_COMPLETE;
    finishMessageProcessing();
  }

}
