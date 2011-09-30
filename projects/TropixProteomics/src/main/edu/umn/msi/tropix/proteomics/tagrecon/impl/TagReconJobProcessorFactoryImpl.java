package edu.umn.msi.tropix.proteomics.tagrecon.impl;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;

@JobType("TagRecon")
public class TagReconJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<TagReconJobProcessorImpl> {

  protected TagReconJobProcessorImpl create() {
    return new TagReconJobProcessorImpl();
  }

}
