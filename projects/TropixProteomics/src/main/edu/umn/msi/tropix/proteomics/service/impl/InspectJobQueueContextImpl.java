package edu.umn.msi.tropix.proteomics.service.impl;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.proteomics.inspect.parameters.InspectParameters;
import edu.umn.msi.tropix.proteomics.service.InspectJobQueueContext;

@JobType("Inspect")
public class InspectJobQueueContextImpl extends RegularIdentificationJobQueueContextImpl<InspectParameters> implements InspectJobQueueContext {

}
