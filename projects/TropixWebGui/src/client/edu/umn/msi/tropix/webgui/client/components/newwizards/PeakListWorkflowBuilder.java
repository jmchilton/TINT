package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.List;

import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitProteomicsConvertDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitThermofinniganRunJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.FileSource;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ProteomicsRunSourceTypeWizardPageImpl.ProteomicsRunSource;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;

public class PeakListWorkflowBuilder extends WorkflowBuilder {
  private final ComponentConstants componentConstants;
  private List<FileSource> fileSources;
  private PeakListSourceTypeWizardPage sourcePage;
  private CommonMetadataProvider commonMetadataSupplier;
  private ServiceWizardPage<QueueGridService> thermoServicesSelectionPage;
  private ServiceWizardPage<QueueGridService> proteomicsConvertServicesSelectionPage;

  public PeakListWorkflowBuilder(final ComponentConstants componentConstants) {
    this.componentConstants = componentConstants;
  }

  public void setFileSources(List<FileSource> fileSources) {
    this.fileSources = fileSources;
  }

  public void setSourcePage(PeakListSourceTypeWizardPage sourcePage) {
    this.sourcePage = sourcePage;
  }

  public void setThermoServicesSelectionPage(ServiceWizardPage<QueueGridService> thermoServicesSelectionPage) {
    this.thermoServicesSelectionPage = thermoServicesSelectionPage;
  }

  public void setCommonMetadataProvider(final CommonMetadataProvider commonMetadataSupplier) {
    this.commonMetadataSupplier = commonMetadataSupplier;
  }

  public void setProteomicsConvertServicesSelectionPage(ServiceWizardPage<QueueGridService> proteomicsConvertServicesSelectionPage) {
    this.proteomicsConvertServicesSelectionPage = proteomicsConvertServicesSelectionPage;
  }

  protected void populateDescriptions() {
    final ProteomicsRunSource type = sourcePage.getProteomicsRunSourceProperty().get();
    final boolean batch = sourcePage.getBatchProperty().get();

    final CreateFolderDescription createFolderDescription = new CreateFolderDescription();
    if(batch) {
      ActivityDescriptions.initCommonMetadata(createFolderDescription, commonMetadataSupplier);
      createFolderDescription.setCommitted(true);
      add(createFolderDescription);
    }

    for(FileSource fileSource : fileSources) {
      String calculatedName = commonMetadataSupplier.getName();
      if(batch && type == ProteomicsRunSource.THERMO) {
        calculatedName = Utils.stripRawExtension(fileSource.getName());
      } else if(batch && type == ProteomicsRunSource.MZXML) {
        calculatedName = Utils.stripMzxmlExtension(fileSource.getName());
      }
      final JobDescription jobDescription = new JobDescription();
      jobDescription.setName(componentConstants.runWizardJobDescriptionName() + calculatedName);

      final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, fileSource.getId());

      final CreateProteomicsRunDescription createProteomicsRunDescription = new CreateProteomicsRunDescription();
      createProteomicsRunDescription.setJobDescription(jobDescription);
      if(batch) {
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createFolderDescription).produces("objectId")
            .consumes("destinationId").build());
        createProteomicsRunDescription.setName(calculatedName);
      } else {
        ActivityDescriptions.initCommonMetadata(createProteomicsRunDescription, commonMetadataSupplier);
      }

      if(type == ProteomicsRunSource.THERMO) {
        final String serviceAddress = thermoServicesSelectionPage.getGridService().getServiceAddress();
        final CreateTropixFileDescription createRawFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
        createRawFileDescription.setExtension(StockFileExtensionEnum.THERMO_RAW.getExtension());

        final SubmitThermofinniganRunJobDescription submitDescription = ActivityDescriptions.createSubmitThermo(createRawFileDescription,
            serviceAddress, calculatedName);
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
        final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
            .consumes("mzxmlFileId").build());
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createRawFileDescription).produces("objectId")
            .consumes("sourceId").build());

        add(createRawFileDescription);
        add(submitDescription);
        add(pollJobDescription);
        add(createMzxmlDescription);
      } else if(type == ProteomicsRunSource.MGF) {
        final CreateTropixFileDescription createSourceFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
        createSourceFileDescription.setExtension(StockFileExtensionEnum.MASCOT_GENERIC_FORMAT.getExtension());

        final SubmitProteomicsConvertDescription submitDescription = ActivityDescriptions.createSubmitProteomicsConvert(
            createSourceFileDescription, proteomicsConvertServicesSelectionPage.getGridService().getServiceAddress(), calculatedName);
        submitDescription.setInputName(calculatedName);
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);

        final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
            .consumes("mzxmlFileId").build());
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createSourceFileDescription).produces("objectId")
            .consumes("sourceId").build());

        add(createSourceFileDescription);
        add(submitDescription);
        add(pollJobDescription);
        add(createMzxmlDescription);
      } else if(type == ProteomicsRunSource.MZXML) {
        final CreateTropixFileDescription createMzxmlFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlFileDescription).produces("objectId")
            .consumes("mzxmlFileId").build());
        add(createMzxmlFileDescription);
      }

      add(uploadDescription);
      add(createProteomicsRunDescription);
      final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createProteomicsRunDescription);
      add(commitDescription);
    }

  }

}
