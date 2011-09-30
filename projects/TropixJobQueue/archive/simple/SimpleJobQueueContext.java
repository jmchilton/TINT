package edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple;

import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;

import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.jobqueue.simplejobdescription.SimpleJobDescriptionType;

public interface SimpleJobQueueContext extends FileJobQueueContext {
  public void submitJob(SimpleJobDescriptionType simpleJobDescription, DelegatedCredentialReference dcReference);

}
