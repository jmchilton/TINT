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

package edu.umn.msi.tropix.genomics.service.impl;


import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.genomics.bowtie.BowtieJobProcessorFactory;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;
import edu.umn.msi.tropix.genomics.service.BowtieJobQueueContext;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("Bowtie")
public class BowtieJobQueueContextImpl extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements BowtieJobQueueContext {
  private BowtieJobProcessorFactory bowtieJobProcessorFactory;

  @LogExceptions
  public void submitJob(final TransferResource bowtieIndex, final TransferResource[] databases, final CredentialResource delegatedReference, final BowtieInput bowtieInput) {
    final Credential proxy = getProxy(delegatedReference);
    final Iterable<InputContext> downloadContexts = getDownloadContexts(databases, proxy);
    final FileJobProcessor<ExecutableJobDescription> jobProcessor = bowtieJobProcessorFactory.createBowtieJob(getConfiguration(proxy), bowtieInput, getDownloadContext(bowtieIndex, proxy), downloadContexts);
    super.submitJob(jobProcessor);
  }

  public void setBowtieJobProcessorFactory(final BowtieJobProcessorFactory bowtieJobProcessorFactory) {
    this.bowtieJobProcessorFactory = bowtieJobProcessorFactory;
  }

}
