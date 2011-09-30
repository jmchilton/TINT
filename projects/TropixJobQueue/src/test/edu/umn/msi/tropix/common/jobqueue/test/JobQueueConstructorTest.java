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

package edu.umn.msi.tropix.common.jobqueue.test;

import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTrackers;
import edu.umn.msi.tropix.common.jobqueue.status.StatusUtils;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.prediction.Models;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.common.test.ConstructorTest;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class JobQueueConstructorTest extends ConstructorTest {
  {
    getClasses().add(JobDescriptionUtils.class);
    getClasses().add(LineProcessingFileProgressTrackers.class);
    getClasses().add(StatusUtils.class);
    getClasses().add(Models.class);
    getClasses().add(ShutdownException.class);
    getClasses().add(Credentials.class);
  }
}
