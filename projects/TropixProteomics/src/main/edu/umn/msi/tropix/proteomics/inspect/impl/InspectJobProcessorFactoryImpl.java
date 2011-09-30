package edu.umn.msi.tropix.proteomics.inspect.impl;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;

@JobType("Inspect")
public class InspectJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<InspectJobProcessorImpl> {

  protected InspectJobProcessorImpl create() {
    return new InspectJobProcessorImpl();
  }

}
