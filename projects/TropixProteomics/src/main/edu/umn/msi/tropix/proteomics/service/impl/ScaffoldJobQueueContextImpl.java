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
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.proteomics.scaffold.ScaffoldJobBuilder;
import edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold;
import edu.umn.msi.tropix.proteomics.service.ScaffoldJobQueueContext;
import edu.umn.msi.tropix.proteomics.xml.XMLConversionUtilities;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("Scaffold")
public class ScaffoldJobQueueContextImpl extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements ScaffoldJobQueueContext {
  private ScaffoldJobBuilder scaffoldJobBuilder;

  @LogExceptions
  public void submitJob(final TransferResource[] references, final CredentialResource dcReference, final Scaffold scaffoldInput) {
    final Credential proxy = getProxy(dcReference);
    final Iterable<InputContext> downloadContexts = getDownloadContexts(references, proxy);
    final FileJobProcessor<ExecutableJobDescription> jobProcessor = scaffoldJobBuilder.createScaffoldJob(getConfiguration(proxy), XMLConversionUtilities.convert(scaffoldInput), downloadContexts);
    super.submitJob(jobProcessor);
  }

  public void setScaffoldJobBuilder(final ScaffoldJobBuilder scaffoldJobBuilder) {
    this.scaffoldJobBuilder = scaffoldJobBuilder;
  }

}
