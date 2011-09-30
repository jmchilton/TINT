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

import javax.inject.Inject;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.proteomics.convert.ProteomicsConvertJobFactory;
import edu.umn.msi.tropix.proteomics.convert.input.ConvertParameters;
import edu.umn.msi.tropix.proteomics.service.ProteomicsConvertJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("ProteomicsConvert")
public class ProteomicsConvertJobQueueContextImpl extends GridFileJobQueueContextImpl<InProcessJobDescription> implements ProteomicsConvertJobQueueContext {
  private ProteomicsConvertJobFactory proteomicsConvertJobFactory;

  @Inject
  public ProteomicsConvertJobQueueContextImpl(final ProteomicsConvertJobFactory proteomicsConvertJobFactory) {
    this.proteomicsConvertJobFactory = proteomicsConvertJobFactory;
  }

  @LogExceptions
  public void submitJob(final TransferResource rawFile, final CredentialResource delegatedReference, final ConvertParameters convertParameters) {
    final Credential proxy = getProxy(delegatedReference);
    final JobProcessorConfiguration jobConfig = getConfiguration(proxy);
    final InputContext rawContext = getDownloadContext(rawFile, proxy);    
    final JobProcessor<InProcessJobDescription> jobProcessor = proteomicsConvertJobFactory.create(jobConfig, rawContext, convertParameters);
    super.submitJob(jobProcessor);
  }

}
