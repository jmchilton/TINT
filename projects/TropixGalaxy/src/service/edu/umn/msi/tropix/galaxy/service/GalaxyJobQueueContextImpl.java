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

package edu.umn.msi.tropix.galaxy.service;

import java.util.List;


import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.galaxy.inputs.cagrid.RootInput;
import edu.umn.msi.tropix.galaxy.tool.cagrid.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@JobType("Galaxy")
class GalaxyJobQueueContextImpl extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements GalaxyJobQueueContext {
  private GalaxyJobProcessorFactory galaxyJobBuilder;

  @LogExceptions
  public void submitJob(final Tool tool, final RootInput input, final String[] fileNames, final TransferResource[] inputFiles, final CredentialResource dcReference) {
    final Credential proxy = getProxy(dcReference);
    final List<InputContext> downloadContexts = getDownloadContexts(inputFiles, proxy);
    final FileJobProcessor<ExecutableJobDescription> jobProcessor = galaxyJobBuilder.createJob(getConfiguration(proxy), GalaxyXmlUtils.convert(tool), GalaxyXmlUtils.convert(input), Lists.newArrayList(fileNames), downloadContexts);
    submitJob(jobProcessor);
  }

  public void setGalaxyJobBuilder(final GalaxyJobProcessorFactory galaxyJobBuilder) {
    this.galaxyJobBuilder = galaxyJobBuilder;
  }
}
