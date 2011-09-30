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

package edu.umn.msi.tropix.common.jobqueue.description;

import org.globus.exec.generated.JobDescriptionType;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;

/**
 * This interface describe {@code JobDescription} implementations that describe external executables to be executed. Information on this executable and how to call it is encoded in an instance of {@code JobDescriptionType}.
 * 
 * @author John Chilton
 * 
 */
public interface ExecutableJobDescription extends JobDescription {
  JobDescriptionType getJobDescriptionType();
}
