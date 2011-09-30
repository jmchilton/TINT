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

import edu.umn.msi.tropix.common.jobqueue.status.EstimatedExecutionTime;
import edu.umn.msi.tropix.common.jobqueue.status.EstimatedPendingTime;
import edu.umn.msi.tropix.common.jobqueue.status.PercentComplete;
import edu.umn.msi.tropix.common.jobqueue.status.QueuePosition;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusEntry;
import edu.umn.msi.tropix.common.jobqueue.status.StatusUtils;

public class StatusUtilsTest {

  @Test(groups = "unit")
  public void percentAbsent() {
    final Status status = new Status();
    assert null == StatusUtils.getPercentComplete(status);
    status.setStatusEntry(new StatusEntry[] {new QueuePosition(), new QueuePosition()});
    assert null == StatusUtils.getPercentComplete(status);
  }

  @Test(groups = "unit")
  public void entryAbsent() {
    final Status status = new Status();
    assert null == StatusUtils.getStatusEntry(status, QueuePosition.class);
    status.setStatusEntry(new StatusEntry[] {new PercentComplete(), new PercentComplete()});
    assert null == StatusUtils.getStatusEntry(status, QueuePosition.class);
  }

  @Test(groups = "unit")
  public void statusToString() {
    Status status;

    for(final StageEnumeration stage : new StageEnumeration[] {StageEnumeration.Absent, StageEnumeration.Preprocessing, StageEnumeration.Postprocessing, StageEnumeration.Timedout, StageEnumeration.Complete, StageEnumeration.Failed}) {
      status = new Status();
      status.setStage(new Stage(stage));
      StatusUtils.toString(status);
    }

    status = new Status();
    status.setStage(new Stage(StageEnumeration.Pending));
    final QueuePosition queuePosition = new QueuePosition();
    queuePosition.setQueueSize(123);
    queuePosition.setValue(56);
    status.setQueuePosition(queuePosition);
    StatusUtils.toString(status);

    final EstimatedPendingTime pendingTime = new EstimatedPendingTime(14324);
    status.setStatusEntry(new StatusEntry[] {pendingTime});
    StatusUtils.toString(status);

    status = new Status();
    status.setStage(new Stage(StageEnumeration.Running));
    StatusUtils.toString(status);

    final EstimatedExecutionTime runningTime = new EstimatedExecutionTime(2342);
    status.setStatusEntry(new StatusEntry[] {runningTime});
    StatusUtils.toString(status);
  }
}
