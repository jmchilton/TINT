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
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.proteomics.idpicker.IdPickerJobProcessorFactory;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.service.IdPickerJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("IdPicker")
public class IdPickerJobQueueContextImpl extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements IdPickerJobQueueContext {
  private IdPickerJobProcessorFactory factory;

  @Inject
  public IdPickerJobQueueContextImpl(final IdPickerJobProcessorFactory factory) {
    this.factory = factory;
  }

  @LogExceptions
  public void submitJob(final TransferResource[] pepXmlReferences, final TransferResource databaseResource, final CredentialResource dcReference,
      final IdPickerParameters idPickerParameters) {
    final Credential proxy = getProxy(dcReference);
    final Iterable<InputContext> pepXmlContexts = getDownloadContexts(pepXmlReferences, proxy);
    final InputContext databaseContext = getDownloadContext(databaseResource, proxy);
    final FileJobProcessor<ExecutableJobDescription> jobProcessor = factory.create(getConfiguration(proxy), idPickerParameters, databaseContext,
        pepXmlContexts);
    super.submitJob(jobProcessor);
  }

}
