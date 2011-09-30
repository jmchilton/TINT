package edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple.impl;

import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple.SimpleJobProcessorFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobqueue.simplejobdescription.SimpleJobDescriptionType;

public class SimpleJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<SimpleJobProcessorImpl> implements SimpleJobProcessorFactory {

  protected SimpleJobProcessorImpl create() {
    return new SimpleJobProcessorImpl();
  }

  public FileJobProcessor<ExecutableJobDescription> buildJob(final Credential proxy, final SimpleJobDescriptionType simpleJobDescription) {
    final SimpleJobProcessorImpl processor = create(new JobProcessorConfiguration(proxy));
    processor.simpleJobDescription = simpleJobDescription;
    return processor;
  }
}
