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

package edu.umn.msi.tropix.common.jobqueue;

import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;

public enum QueueStage {
  POSTPROCESSING("Postprocessing", false, StageEnumeration.Postprocessing), 
  POSTPROCESSED("Postprocessed", false, StageEnumeration.Postprocessed),
  TRANSFERRING("Transferring", false, StageEnumeration.Transferring),
  PREPROCESSING("Preprocessing", false, StageEnumeration.Preprocessing),
  PENDING("Pending", false, StageEnumeration.Pending), 
  RUNNING("Running", false, StageEnumeration.Running), 
  COMPLETED("Complete", true, StageEnumeration.Complete), 
  FAILED("Failed", true, StageEnumeration.Failed), 
  TIMEDOUT("Timedout", true, StageEnumeration.Timedout), 
  ABSENT("Absent", true, StageEnumeration.Absent);

  private String value;
  private boolean isComplete;
  private StageEnumeration stageEnumerationValue;

  private QueueStage(final String value, final boolean isComplete, final StageEnumeration stageEnumerationValue) {
    this.value = value;
    this.isComplete = isComplete;
    this.stageEnumerationValue = stageEnumerationValue;
  }

  public String toString() {
    return value;
  }

  public boolean isComplete() {
    return isComplete;
  }

  public static QueueStage fromString(final String string) {
    for(final QueueStage status : QueueStage.values()) {
      if(status.toString().equals(string)) {
        return status;
      }
    }
    return null;
  }

  public static QueueStage fromStatusUpdateList(final Status list) {
    return QueueStage.fromString(list.getStage().getValue().getValue());
  }

  public StageEnumeration getStageEnumerationValue() {
    return stageEnumerationValue;
  }
}
