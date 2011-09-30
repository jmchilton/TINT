/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.proteomics.itraqquantitation.ITraqQuantitationJobProcessorFactory;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;
import edu.umn.msi.tropix.proteomics.service.ITraqQuantitationJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("ITraqQuantitation")
public class ITraqQuantitationJobQueueContextImpl extends GridFileJobQueueContextImpl<InProcessJobDescription> implements ITraqQuantitationJobQueueContext {
  private static final Log LOG = LogFactory.getLog(ITraqQuantitationJobQueueContextImpl.class);
  private ITraqQuantitationJobProcessorFactory itraqQuantitationJobProcessorFactory;

  public void setItraqQuantitationJobProcessorFactory(final ITraqQuantitationJobProcessorFactory itraqQuantitationJobProcessorFactory) {
    this.itraqQuantitationJobProcessorFactory = itraqQuantitationJobProcessorFactory;
  }

  @LogExceptions
  public void submitJob(final TransferResource[] mzxmlInputs, final TransferResource dataReport, final QuantificationType quantificationType, final QuantificationWeights weights, final CredentialResource delegatedReference) {
    final Credential proxy = getProxy(delegatedReference);
    final JobProcessorConfiguration jobConfig = getConfiguration(proxy);
    final Iterable<InputContext> mzxmlContexts = getDownloadContexts(mzxmlInputs, proxy);
    final InputContext dataContext = getDownloadContext(dataReport, proxy);
    final JobProcessor<InProcessJobDescription> jobProcessor = itraqQuantitationJobProcessorFactory.create(jobConfig, mzxmlContexts, dataContext, quantificationType, weights);
    super.submitJob(jobProcessor);
  }

  @LogExceptions
  public void submitTrainingJob(final TransferResource[] mzxmlInputs, final TransferResource dataReport, final QuantificationType quantificationType, final QuantificationTrainingOptions trainingOptions, final CredentialResource delegatedReference) {
    final Credential proxy = getProxy(delegatedReference);
    final JobProcessorConfiguration jobConfig = getConfiguration(proxy);
    final Iterable<InputContext> mzxmlContexts = getDownloadContexts(mzxmlInputs, proxy);
    final InputContext dataContext = getDownloadContext(dataReport, proxy);
    LOG.info("In submitTrainingJob with training options " + trainingOptions);
    final JobProcessor<InProcessJobDescription> jobProcessor = itraqQuantitationJobProcessorFactory.createTraining(jobConfig, mzxmlContexts, dataContext, quantificationType, trainingOptions);
    super.submitJob(jobProcessor);
  }

}
