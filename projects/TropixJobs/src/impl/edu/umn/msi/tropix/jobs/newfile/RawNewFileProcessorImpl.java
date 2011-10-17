package edu.umn.msi.tropix.jobs.newfile;

import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitThermofinniganRunJobDescription;
import edu.umn.msi.tropix.jobs.client.ActivityClient;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;

@ManagedBean
@ForExtension(edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.THERMO_RAW)
public class RawNewFileProcessorImpl implements NewFileProcessor {
  private final ActivityClient activityClient;
  private final Supplier<String> rawExtractServiceUrlSupplier;
  
  @Inject
  public RawNewFileProcessorImpl(final ActivityClient activityClient, 
                                 @Named("rawExtractServiceUrlSupplier") final Supplier<String> rawExtractServiceUrlSupplier) {
    this.activityClient = activityClient;
    this.rawExtractServiceUrlSupplier = rawExtractServiceUrlSupplier;
  }

  public void processFile(final NewFileMessage message, final TropixFile tropixFile) {
    final String name = StockFileExtensionEnum.THERMO_RAW.stripExtension(tropixFile.getName());
    final JobDescription jobDescription = new JobDescription(String.format("Create peak list %s", name));
    final CreateProteomicsRunDescription createProteomicsRunDescription = new CreateProteomicsRunDescription();
    createProteomicsRunDescription.setJobDescription(jobDescription);
    createProteomicsRunDescription.setName(name);
    createProteomicsRunDescription.setDestinationId(message.getParentId());
    createProteomicsRunDescription.setSourceId(tropixFile.getId());
    final SubmitThermofinniganRunJobDescription submitDescription = ActivityDescriptions.createSubmitThermo(createProteomicsRunDescription,
        rawExtractServiceUrlSupplier.get(), name);
    final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
    final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);
    createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
        .consumes("mzxmlFileId").build());

    final Set<ActivityDescription> descriptions = Sets.newHashSet();
    descriptions.add(createProteomicsRunDescription);
    descriptions.add(submitDescription);
    descriptions.add(pollJobDescription);
    descriptions.add(createMzxmlDescription);
    activityClient.submit(descriptions, message.getCredential());
  }

}
