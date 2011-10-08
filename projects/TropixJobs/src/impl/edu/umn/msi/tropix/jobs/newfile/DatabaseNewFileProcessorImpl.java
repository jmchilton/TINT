package edu.umn.msi.tropix.jobs.newfile;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.client.ActivityClient;
import edu.umn.msi.tropix.models.TropixFile;

public class DatabaseNewFileProcessorImpl implements NewFileProcessor {
  private final ActivityClient activityClient;

  public DatabaseNewFileProcessorImpl(final ActivityClient activityClient) {
    this.activityClient = activityClient;
  }
  
  public void processFile(final NewFileMessage message, final TropixFile tropixFile) {
    final CreateDatabaseDescription description = new CreateDatabaseDescription();
    description.setDestinationId(message.getParentId());
    description.setDatabaseType("FASTA");
    final String name = getDatabaseName(tropixFile);
    description.setName(name);
    description.setCommitted(true);
    description.setDatabaseFileId(tropixFile.getId());
    final JobDescription jobDescription = new JobDescription();
    jobDescription.setName(String.format("Create database %s", name));
    description.setJobDescription(jobDescription);
    activityClient.submit(Sets.<ActivityDescription>newHashSet(description), message.getCredential());
  }
  
  private String getDatabaseName(final TropixFile file) {
    final String fileName = file.getName();
    Preconditions.checkNotNull(fileName);
    final boolean hasExtension = fileName.toUpperCase().endsWith(".FASTA");
    final int length = fileName.length();
    final String databaseName = hasExtension ? fileName.substring(0, length - ".FASTA".length()) : fileName;
    return databaseName;
  }

}
