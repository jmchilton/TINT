package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;

import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.MergeScaffoldSamplesDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitThermofinniganRunJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ScaffoldSampleTypeWizardPageImpl.ScaffoldSampleType;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.Sets;

// TODO: Don't iterate over uploadRuns twice, it won't yield different order but seems in bad form nonetheless.
public class IdentificationWorkflowBuilder {
  private final ComponentConstants componentConstants;

  private boolean createSubfolders;
  private boolean useExistingRuns;
  private boolean useScaffold;
  private ScaffoldSampleType scaffoldType;
  private CommonMetadataProvider commonMetadataProvider;
  private Map<String, String> parameterMap;
  private IdentificationGridService idService;
  private String databaseId;
  private Map<String, String> uploadedRuns;
  private Collection<ProteomicsRun> selectedRuns;
  private QueueGridService rawExtractGridService;
  private boolean analyzeScaffoldAsMudpit;
  private ScaffoldGridService scaffoldGridService;
  private Map<String, String> scaffoldParameterMap;

  public IdentificationWorkflowBuilder(final ComponentConstants componentConstants) {
    this.componentConstants = componentConstants;
  }

  private List<String> getRunNames() {
    final List<String> names = Lists.newArrayList();
    if(uploadedRuns != null) { // Uploaded new runs
      for(final Map.Entry<String, String> uploadedRunEntry : uploadedRuns.entrySet()) {
        final String rawFileBaseName = Utils.stripRawExtension(uploadedRunEntry.getKey());
        names.add(rawFileBaseName);
      }
    } else {
      for(final ProteomicsRun run : selectedRuns) {
        names.add(run.getName());
      }
    }
    return names;
  }

  public Set<ActivityDescription> build() {
    final Set<ActivityDescription> descriptions = Sets.newHashSet();
    final List<String> names = getRunNames();

    final CreateFolderDescription createFolder = new CreateFolderDescription();
    ActivityDescriptions.initCommonMetadata(createFolder, commonMetadataProvider);
    createFolder.setCommitted(true);
    descriptions.add(createFolder);

    // Specify folder for each type of object, these will just default to main folder specified above
    // unless createSubfolders is true.
    CreateFolderDescription runFolder = createFolder;
    CreateFolderDescription idFolder = createFolder;
    CreateFolderDescription scaffoldFolder = createFolder;
    if(createSubfolders) {
      if(!useExistingRuns) {
        runFolder = new CreateFolderDescription();
        runFolder.setName(componentConstants.idWorkflowRunFolder());
        runFolder.addDependency(ActivityDependency.Builder.on(createFolder).produces("objectId").consumes("destinationId").build());
        descriptions.add(runFolder);
      }

      idFolder = new CreateFolderDescription();
      idFolder.setName(componentConstants.idWorkflowIdFolder());
      idFolder.addDependency(ActivityDependency.Builder.on(createFolder).produces("objectId").consumes("destinationId").build());
      descriptions.add(idFolder);

      // No need to create folder for scaffold analyses unless there is going to be more than 1, i.e. scaffoldType is MANY_ANALYSIS
      if(useScaffold && (scaffoldType == ScaffoldSampleType.MANY_ANALYSIS)) {
        scaffoldFolder = new CreateFolderDescription();
        scaffoldFolder.setName(componentConstants.idWorkflowScaffoldFolder());
        scaffoldFolder.addDependency(ActivityDependency.Builder.on(createFolder).produces("objectId").consumes("destinationId").build());
        descriptions.add(scaffoldFolder);
      }
    }
    final StringBuilder logMessage = new StringBuilder();
    logMessage.append("Creating workflow: scaffoldType[");
    logMessage.append(scaffoldType.name());
    logMessage.append("], createFolders[");
    logMessage.append(createSubfolders);
    logMessage.append("], name[");
    logMessage.append(commonMetadataProvider.getName());
    logMessage.append("].");
    Log.info(logMessage.toString());
    // final IdentificationGridService idService = ((IdentificationGridService) identificationSelectionPage.getGridService());
    final String appName = idService.getApplicationName();
    final String idServiceAddress = idService.getServiceAddress();
    final CreateIdentificationParametersDescription parametersDescription = new CreateIdentificationParametersDescription();
    parametersDescription.setCommitted(true);
    parametersDescription.setParameterType(idService.getParameterType());
    parametersDescription.setParameters(StringParameterSet.fromMap(parameterMap));
    descriptions.add(parametersDescription);

    final List<SubmitIdentificationAnalysisDescription> submitIdentficationAnalysisDescriptions = Lists.newArrayListWithCapacity(names.size());
    final List<CreateIdentificationAnalysisDescription> createIdentficationAnalysisDescriptions = Lists.newArrayListWithCapacity(names.size());
    for(final String name : names) {
      final String analysisBase = name;
      final String analysisName = analysisBase + " (" + appName + ")";
      final JobDescription jobDescription = new JobDescription(componentConstants.idWizardJobDescriptionName() + analysisName);

      // final String databaseId = databasePage.getDatabase().getId();
      final String runId = null; // We will set this later

      final SubmitIdentificationAnalysisDescription submitDescription = ActivityDescriptions.createSubmitIdentification(parametersDescription,
          databaseId, runId, idServiceAddress);
      submitDescription.setJobDescription(jobDescription);
      final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
      final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

      final CreateIdentificationAnalysisDescription createIdentificationAnalysisDescription = new CreateIdentificationAnalysisDescription();
      createIdentificationAnalysisDescription.setName(analysisName);
      createIdentificationAnalysisDescription.setDescription("");
      createIdentificationAnalysisDescription.setJobDescription(jobDescription);
      createIdentificationAnalysisDescription.setAnalysisType(idService.getParameterType());
      createIdentificationAnalysisDescription.setDatabaseId(databaseId);
      createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(idFolder).produces("objectId").consumes("destinationId")
          .build());
      createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(createAnalysisFileDescription).produces("objectId")
          .consumes("analysisFileId").build());
      createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(parametersDescription).produces("objectId")
          .consumes("parametersId").build());

      createIdentficationAnalysisDescriptions.add(createIdentificationAnalysisDescription);
      submitIdentficationAnalysisDescriptions.add(submitDescription);

      final CommitObjectDescription commitIdDescription = ActivityDescriptions.createCommitDescription(createIdentificationAnalysisDescription);

      descriptions.add(submitDescription);
      descriptions.add(pollJobDescription);
      descriptions.add(createAnalysisFileDescription);
      descriptions.add(createIdentificationAnalysisDescription);
      descriptions.add(commitIdDescription);
    }

    final Iterator<CreateIdentificationAnalysisDescription> createIdIter = createIdentficationAnalysisDescriptions.iterator();
    final Iterator<SubmitIdentificationAnalysisDescription> submitIdIter = submitIdentficationAnalysisDescriptions.iterator();
    if(uploadedRuns != null) { // Uploaded new runs
      final Iterator<String> nameIter = names.iterator();
      for(final Map.Entry<String, String> uploadedRunEntry : uploadedRuns.entrySet()) {
        final CreateIdentificationAnalysisDescription createId = createIdIter.next();
        final SubmitIdentificationAnalysisDescription submitId = submitIdIter.next();
        final String name = nameIter.next();

        final JobDescription jobDescription = new JobDescription(componentConstants.runWizardJobDescriptionName() + name);
        final UploadFileDescription uploadDescription = new UploadFileDescription();
        uploadDescription.setJobDescription(jobDescription);
        uploadDescription.setInputFilePath(uploadedRunEntry.getValue());
        final CreateTropixFileDescription createRawFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, true);
        createRawFileDescription.setExtension(".RAW");
        final SubmitThermofinniganRunJobDescription submitDescription = ActivityDescriptions.createSubmitThermo(createRawFileDescription,
            rawExtractGridService.getServiceAddress(), name);
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
        final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

        final CreateProteomicsRunDescription createProteomicsRunDescription = new CreateProteomicsRunDescription();
        createProteomicsRunDescription.setJobDescription(jobDescription);
        createProteomicsRunDescription.setName(name);
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(runFolder).produces("objectId").consumes("destinationId").build());
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createRawFileDescription).produces("objectId")
            .consumes("sourceId").build());
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
            .consumes("mzxmlFileId").build());
        // createProteomicsRunDescription.setSampleId(sampleWizardPage.getSampleId());

        final CommitObjectDescription commitRunDescription = ActivityDescriptions.createCommitDescription(createProteomicsRunDescription);

        descriptions.add(uploadDescription);
        descriptions.add(createRawFileDescription);
        descriptions.add(submitDescription);
        descriptions.add(pollJobDescription);
        descriptions.add(createMzxmlDescription);
        descriptions.add(createProteomicsRunDescription);
        descriptions.add(commitRunDescription);

        createId.addDependency(ActivityDependency.Builder.on(createProteomicsRunDescription).produces("objectId").consumes("runId").build());
        submitId.addDependency(ActivityDependency.Builder.on(createProteomicsRunDescription).produces("objectId").consumes("runId").build());
      }
    } else {
      for(final ProteomicsRun run : selectedRuns) {
        final CreateIdentificationAnalysisDescription createId = createIdIter.next();
        final SubmitIdentificationAnalysisDescription submitId = submitIdIter.next();
        final String runId = run.getId();
        createId.setRunId(runId);
        submitId.setRunId(runId);
      }
    }

    if(useScaffold) {
      final boolean analyzeAsMudpit = analyzeScaffoldAsMudpit;
      final Collection<MergeScaffoldSamplesDescription> mergeScaffoldSamples = Lists.newArrayList();
      if(scaffoldType.equals(ScaffoldSampleType.MANY_ANALYSIS)) {
        int index = 0;
        for(final String name : names) {
          final CreateIdentificationAnalysisDescription createIdAnalysis = createIdentficationAnalysisDescriptions.get(index++);
          final JobDescription jobDescription = new JobDescription(componentConstants.scaffoldWizardJobDescriptionName() + name);
          final MergeScaffoldSamplesDescription mergeSamples = new MergeScaffoldSamplesDescription();
          mergeSamples.setJobDescription(jobDescription);
          mergeSamples.addName(name);
          mergeSamples.addDependency(ActivityDependency.Builder.on(createIdAnalysis).produces("objectId").consumes("identificationId").build());
          mergeSamples.setMudpit(analyzeAsMudpit);
          mergeScaffoldSamples.add(mergeSamples);
        }
      } else {
        final JobDescription jobDescription = new JobDescription(componentConstants.scaffoldWizardJobDescriptionName() + createFolder.getName());
        final MergeScaffoldSamplesDescription mergeSamples = new MergeScaffoldSamplesDescription();
        mergeSamples.setJobDescription(jobDescription);
        mergeSamples.setMudpit(analyzeAsMudpit);
        if(scaffoldType == ScaffoldSampleType.ONE_SAMPLE) {
          mergeSamples.addName(createFolder.getName());
          mergeSamples.setProduceMultipleSamples(false);
        } else {
          mergeSamples.setProduceMultipleSamples(true);
        }
        int index = 0;
        for(final String name : names) {
          final CreateIdentificationAnalysisDescription createIdAnalysis = createIdentficationAnalysisDescriptions.get(index++);
          mergeSamples.addDependency(ActivityDependency.Builder.on(createIdAnalysis).produces("objectId").consumes("identificationId").build());
          if(scaffoldType == ScaffoldSampleType.MANY_SAMPLE) {
            mergeSamples.addName(name);
          }
        }
        mergeScaffoldSamples.add(mergeSamples);
      }
      for(final MergeScaffoldSamplesDescription mergeSamples : mergeScaffoldSamples) {
        final CreateScaffoldDriverDescription createDriver = new CreateScaffoldDriverDescription();
        createDriver.setJobDescription(mergeSamples.getJobDescription());
        createDriver.setScaffoldVersion(scaffoldGridService.getScaffoldVersion());
        createDriver.setParameterSet(StringParameterSet.fromMap(scaffoldParameterMap));
        createDriver.addDependency(ActivityDependency.Builder.on(mergeSamples).produces("scaffoldSamples").consumes("scaffoldSamples").build());

        final CreateTropixFileDescription createDriverFile = ActivityDescriptions.createFileForScaffoldDriver(createDriver);
        final SubmitScaffoldAnalysisDescription submitDescription = ActivityDescriptions.createSubmitScaffold(createDriver, createDriverFile,
            scaffoldGridService.getServiceAddress());
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
        final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);
        final CreateScaffoldAnalysisDescription createAnalysis = ActivityDescriptions.createScaffoldAnalysis(createDriver, createDriverFile,
            createAnalysisFileDescription);
        final List<String> sampleNames = mergeSamples.getNames().toList();
        createAnalysis.setName(sampleNames.size() == 1 ? sampleNames.get(0) : createFolder.getName());
        createAnalysis.addDependency(ActivityDependency.Builder.on(scaffoldFolder).produces("objectId").consumes("destinationId").build());
        createAnalysis.setScaffoldVersion(scaffoldGridService.getScaffoldVersion());

        final CommitObjectDescription commitScaffoldDescription = ActivityDescriptions.createCommitDescription(createAnalysis);

        descriptions.add(mergeSamples);
        descriptions.add(createDriver);
        descriptions.add(createDriverFile);
        descriptions.add(submitDescription);
        descriptions.add(pollJobDescription);
        descriptions.add(createAnalysisFileDescription);
        descriptions.add(createAnalysis);
        descriptions.add(commitScaffoldDescription);
      }
    }
    return descriptions;
  }

  public void setCreateSubfolders(final boolean createSubfolders) {
    this.createSubfolders = createSubfolders;
  }

  public void setUseExistingRuns(final boolean useExistingRuns) {
    this.useExistingRuns = useExistingRuns;
  }

  public void setUseScaffold(final boolean useScaffold) {
    this.useScaffold = useScaffold;
  }

  public void setScaffoldType(final ScaffoldSampleType scaffoldType) {
    this.scaffoldType = scaffoldType;
  }

  public void setCommonMetadataProvider(final CommonMetadataProvider commonMetadataProvider) {
    this.commonMetadataProvider = commonMetadataProvider;
  }

  public void setParameterMap(final Map<String, String> parameterMap) {
    this.parameterMap = parameterMap;
  }

  public void setIdService(final IdentificationGridService idService) {
    this.idService = idService;
  }

  public void setDatabaseId(final String databaseId) {
    this.databaseId = databaseId;
  }

  public void setUploadedRuns(final Map<String, String> uploadedRuns) {
    this.uploadedRuns = uploadedRuns;
  }

  public void setSelectedRuns(final Collection<ProteomicsRun> selectedRuns) {
    this.selectedRuns = selectedRuns;
  }

  public void setRawExtractGridService(final QueueGridService rawExtractGridService) {
    this.rawExtractGridService = rawExtractGridService;
  }

  public void setAnalyzeScaffoldAsMudpit(final boolean analyzeScaffoldAsMudpit) {
    this.analyzeScaffoldAsMudpit = analyzeScaffoldAsMudpit;
  }

  public void setScaffoldGridService(final ScaffoldGridService scaffoldGridService) {
    this.scaffoldGridService = scaffoldGridService;
  }

  public void setScaffoldParameterMap(final Map<String, String> scaffoldParameterMap) {
    this.scaffoldParameterMap = scaffoldParameterMap;
  }

}
