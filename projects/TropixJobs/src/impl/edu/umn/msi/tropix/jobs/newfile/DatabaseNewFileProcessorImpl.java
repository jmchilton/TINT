package edu.umn.msi.tropix.jobs.newfile;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.client.ActivityClient;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;

@ManagedBean
@ForExtension(edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.FASTA)
public class DatabaseNewFileProcessorImpl implements NewFileProcessor {
  private final ActivityClient activityClient;

  @Inject
  public DatabaseNewFileProcessorImpl(final ActivityClient activityClient) {
    this.activityClient = activityClient;
  }
  
  public void processFile(final NewFileMessage message, final TropixFile tropixFile) {
    final CreateDatabaseDescription description = new CreateDatabaseDescription();
    description.setDestinationId(message.getParentId());
    description.setDatabaseType("FASTA");
    final String name = StockFileExtensionEnum.FASTA.stripExtension(tropixFile.getName());
    description.setName(name);
    description.setCommitted(true);
    description.setDatabaseFileId(tropixFile.getId());
    final JobDescription jobDescription = new JobDescription();
    jobDescription.setName(String.format("Create database %s", name));
    description.setJobDescription(jobDescription);
    activityClient.submit(Sets.<ActivityDescription>newHashSet(description), message.getCredential());
  }
  
}
