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

package edu.umn.msi.tropix.common.jobqueue.status;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StatusUtils {

  public static boolean wasCancelled(final Status status) {
    final WasCancelled wasCancelledObject = getStatusEntry(status, WasCancelled.class);
    return wasCancelledObject != null && wasCancelledObject.isValue();
  }

  public static String getStackTrace(final Status status) {
    final FailureDescription description = getStatusEntry(status, FailureDescription.class);
    return description == null ? null : description.getStackTrace();
  }

  public static FailureDescription getStackTrace(final Throwable cause) {
    final StringWriter writer = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(writer);
    cause.printStackTrace(printWriter);
    printWriter.close();
    writer.flush();
    final FailureDescription fd = new FailureDescription();
    fd.setStackTrace(writer.toString());
    return fd;
  }

  public static Double getPercentComplete(final Status status) {
    final StatusEntry[] entries = status.getStatusEntry();
    if(entries == null) {
      return null;
    }
    Double percentComplete = null;
    for(final StatusEntry entry : entries) {
      if(entry instanceof PercentComplete) {
        percentComplete = ((PercentComplete) entry).getValue();
        break;
      }
    }
    return percentComplete;
  }

  public static <T extends StatusEntry> T getStatusEntry(final Status status, final Class<T> entryClass) {
    final StatusEntry[] entries = status.getStatusEntry();
    T result = null;
    if(entries != null) {
      for(final StatusEntry entry : entries) {
        if(entryClass.isInstance(entry)) {
          result = entryClass.cast(entry);
          break;
        }
      }
    }
    return result;
  }

  public static String toString(final Status status) {
    final StringBuilder statStr = new StringBuilder();
    statStr.append("Status[");
    final StageEnumeration stageEnum = status.getStage().getValue();
    statStr.append("stage=" + stageEnum.getValue());
    if(stageEnum == StageEnumeration.Pending) {
      final QueuePosition queuePosition = status.getQueuePosition();
      if(queuePosition != null) {
        statStr.append(",pos=" + queuePosition.getValue() + "/" + queuePosition.getQueueSize());
      }
      final EstimatedPendingTime pendingTime = getStatusEntry(status, EstimatedPendingTime.class);
      if(pendingTime != null) {
        statStr.append(",estPendingTime=" + pendingTime.getValue() + "ms");
      }
    }
    if(stageEnum == StageEnumeration.Running) {
      statStr.append(",percentComplete=" + getPercentComplete(status));
      final EstimatedExecutionTime estimatedExecutionTime = getStatusEntry(status, EstimatedExecutionTime.class);
      if(estimatedExecutionTime != null) {
        statStr.append(",estExecTime=" + estimatedExecutionTime.getValue());
      }
    }
    statStr.append("]");
    return statStr.toString();
  }
}
