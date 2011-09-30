package edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple.impl;

import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;

import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple.SimpleJobProcessorFactory;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple.SimpleJobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.jobqueue.simplejobdescription.SimpleJobDescriptionType;

public class SimpleJobQueueContextImpl extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements SimpleJobQueueContext {
  private SimpleJobProcessorFactory simpleJobProcessorFactory;

  public void submitJob(final SimpleJobDescriptionType simpleJobDescription, final DelegatedCredentialReference dcReference) {
    final FileJobProcessor<ExecutableJobDescription> jobProcessor = simpleJobProcessorFactory.buildJob(getProxy(dcReference), simpleJobDescription);
    super.submitJob(jobProcessor);
  }

  public void setSimpleJobProcessorFactory(final SimpleJobProcessorFactory simpleJobProcessorFactory) {
    this.simpleJobProcessorFactory = simpleJobProcessorFactory;
  }

}
