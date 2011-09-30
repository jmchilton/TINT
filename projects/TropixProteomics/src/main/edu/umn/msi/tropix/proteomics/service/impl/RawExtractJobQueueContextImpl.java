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


import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.proteomics.rawextract.RawExtractJobBuilder;
import edu.umn.msi.tropix.proteomics.service.RawExtractJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("RawExtract")
public class RawExtractJobQueueContextImpl extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements RawExtractJobQueueContext {
  private RawExtractJobBuilder rawExtractJobBuilder;

  public void setRawExtractJobBuilder(final RawExtractJobBuilder rawExtractJobBuilder) {
    this.rawExtractJobBuilder = rawExtractJobBuilder;
  }

  @LogExceptions
  public void submitJob(final TransferResource rawFile, final CredentialResource dcRef, final String parameters, final String rawFileBaseName) {
    final Credential proxy = getProxy(dcRef);
    final InputContext downloadContext = getDownloadContext(rawFile, proxy);
    submitJob(rawExtractJobBuilder.buildJob(getConfiguration(proxy), downloadContext, parameters, rawFileBaseName));
  }

}
