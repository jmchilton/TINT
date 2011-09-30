package edu.umn.msi.tropix.proteomics.service.impl;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagParameters;
import edu.umn.msi.tropix.proteomics.service.TagReconJobQueueContext;

@JobType("TagRecon")
public class TagReconJobQueueContextImpl extends RegularIdentificationJobQueueContextImpl<TagParameters> implements TagReconJobQueueContext {

}
