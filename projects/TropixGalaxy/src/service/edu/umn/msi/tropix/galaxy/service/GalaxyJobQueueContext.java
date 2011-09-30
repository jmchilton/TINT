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


import edu.umn.msi.tropix.common.jobqueue.service.FileJobQueueContext;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.galaxy.inputs.cagrid.RootInput;
import edu.umn.msi.tropix.galaxy.tool.cagrid.Tool;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public interface GalaxyJobQueueContext extends FileJobQueueContext {

  void submitJob(final Tool tool, RootInput inputs, final String[] fileNames, TransferResource[] inputFiles, CredentialResource delegatedReference);

}
