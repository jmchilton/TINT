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
import edu.umn.msi.tropix.jobs.activities.descriptions.FileSourceHolder;
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

    for(final FileSource fileSource : fileSources) {
      String calculatedName = commonMetadataSupplier.getName();
      if(batch && type == ProteomicsRunSource.THERMO) {
        calculatedName = Utils.stripRawExtension(fileSource.getName());
      } else if(batch && type == ProteomicsRunSource.MZXML) {
        calculatedName = Utils.stripMzxmlExtension(fileSource.getName());
      }
      final JobDescription jobDescription = new JobDescription();
      jobDescription.setName(componentConstants.runWizardJobDescriptionName() + calculatedName);

      final FileSourceHolder fileSourceHolder;
      final boolean setSource = type != ProteomicsRunSource.MZXML;
      if(fileSource.isUpload()) {
        final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, fileSource.getId());
        final CreateTropixFileDescription createSourceFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
        add(uploadDescription);
        add(createSourceFileDescription);
        if(type == ProteomicsRunSource.THERMO) {
          createSourceFileDescription.setExtension(StockFileExtensionEnum.THERMO_RAW.getExtension());
        } else if(type == ProteomicsRunSource.MGF) {
          createSourceFileDescription.setExtension(StockFileExtensionEnum.MASCOT_GENERIC_FORMAT.getExtension());
        }
        fileSourceHolder = new FileSourceHolder(createSourceFileDescription);
      } else {
        fileSourceHolder = new FileSourceHolder(fileSource.getId());
      }

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
        final SubmitThermofinniganRunJobDescription submitDescription = ActivityDescriptions.createSubmitThermo(fileSourceHolder,
            serviceAddress, calculatedName);
        submitDescription.setJobDescription(jobDescription);
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
        final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
            .consumes("mzxmlFileId").build());

        add(submitDescription);
        add(pollJobDescription);
        add(createMzxmlDescription);
      } else if(type == ProteomicsRunSource.MGF) {
        final String serviceAddress = proteomicsConvertServicesSelectionPage.getGridService().getServiceAddress();
        final SubmitProteomicsConvertDescription submitDescription = ActivityDescriptions.createSubmitProteomicsConvert(
            fileSourceHolder, serviceAddress, calculatedName);
        submitDescription.setInputName(calculatedName);
        submitDescription.setJobDescription(jobDescription);
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
        final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);
        createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
            .consumes("mzxmlFileId").build());

        add(submitDescription);
        add(pollJobDescription);
        add(createMzxmlDescription);
      } else if(type == ProteomicsRunSource.MZXML) {
        if(fileSourceHolder.hasExistingId()) {
          createProteomicsRunDescription.setMzxmlFileId(fileSourceHolder.getTropixFileObjectId());
        } else {
          final CreateTropixFileDescription createSourceDescription = fileSourceHolder.getCreateTropixFileDescription();
          createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createSourceDescription).produces("objectId")
              .consumes("mzxmlFileId").build());
        }
      }
      if(setSource) {
        if(fileSourceHolder.hasExistingId()) {
          createProteomicsRunDescription.setSourceId(fileSourceHolder.getTropixFileObjectId());
        } else {
          final CreateTropixFileDescription createSourceDescription = fileSourceHolder.getCreateTropixFileDescription();
          final ActivityDependency dependency = ActivityDependency.Builder.on(createSourceDescription).produces("objectId").consumes("sourceId")
              .build();
          createProteomicsRunDescription.addDependency(dependency);
        }
      }
      final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createProteomicsRunDescription);

      add(createProteomicsRunDescription);
      add(commitDescription);
    }

  }
}
