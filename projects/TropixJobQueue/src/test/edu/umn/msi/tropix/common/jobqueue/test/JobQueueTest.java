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

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.QueueStage;
import edu.umn.msi.tropix.common.jobqueue.QueueStatusBeanImpl;
import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;

public class JobQueueTest {
  @Test(groups = {"jobqueue", "unit"})
  public void testJobStatusFromString() {
    for(final QueueStage status : QueueStage.values()) {
      assert status.equals(QueueStage.fromString(status.toString()));
    }
    assert null == QueueStage.fromString("moo");
    assert QueueStage.valueOf("ABSENT") == QueueStage.ABSENT;
  }

  @Test(groups = "unit")
  public void queueStatusBeanTest() {
    final QueueStatusBeanImpl bean = new QueueStatusBeanImpl();
    assert bean.get() == null;
    final QueueStatus queueStatus = new QueueStatus();
    bean.set(queueStatus);
    assert bean.get() == queueStatus;
  }
}
