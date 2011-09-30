package edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple;

import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobqueue.simplejobdescription.SimpleJobDescriptionType;

public interface SimpleJobProcessorFactory {
  public FileJobProcessor<ExecutableJobDescription> buildJob(Credential proxy, SimpleJobDescriptionType simpleJobDescription);
}
