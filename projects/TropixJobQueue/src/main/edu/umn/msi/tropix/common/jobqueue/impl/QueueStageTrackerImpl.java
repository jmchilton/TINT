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

package edu.umn.msi.tropix.common.jobqueue.impl;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import edu.umn.msi.tropix.common.jobqueue.QueueStage;

/**
 * Keep rough track of the number of jobs in each state. Rough in the sense that a job might appear to be in both states for a brief time. If more precise measurements are required additional work should be done to handle synchronization.
 * 
 * @author John Chilton
 * 
 */
@ThreadSafe
@ManagedResource
public class QueueStageTrackerImpl implements QueueStageTracker {
  private final AtomicLong numPostprocessing = new AtomicLong();
  private final AtomicLong numPostprocessed = new AtomicLong();
  private final AtomicLong numTransferring = new AtomicLong();
  private final AtomicLong numPreprocessing = new AtomicLong();
  private final AtomicLong numPending = new AtomicLong();
  private final AtomicLong numComplete = new AtomicLong();
  private final AtomicLong numFailed = new AtomicLong();
  private final AtomicLong numRunning = new AtomicLong();

  public void switchStage(@Nullable final QueueStage previousQueueStage, final QueueStage queueStage) {
    if(previousQueueStage != null) {
      switch(previousQueueStage) {
      case PREPROCESSING:
        numPreprocessing.decrementAndGet();
        break;
      case PENDING:
        numPending.decrementAndGet();
        break;
      case POSTPROCESSING:
        numPostprocessing.decrementAndGet();
        break;
      case POSTPROCESSED:
        numPostprocessed.decrementAndGet();
        break;
      case TRANSFERRING:
        numTransferring.decrementAndGet();
        break;
      case RUNNING:
        numRunning.decrementAndGet();
        break;
      }
    }
    switch(queueStage) {
    case PREPROCESSING:
      numPreprocessing.incrementAndGet();
      break;
    case PENDING:
      numPending.incrementAndGet();
      break;
    case POSTPROCESSING:
      numPostprocessing.incrementAndGet();
      break;
    case POSTPROCESSED:
      numPostprocessed.incrementAndGet();
      break;
    case RUNNING:
      numRunning.incrementAndGet();
      break;
    case COMPLETED:
      numComplete.incrementAndGet();
      break;
    case TRANSFERRING:
      numTransferring.incrementAndGet();
      break;
    default:
      numFailed.incrementAndGet();
    }
  }

  @ManagedAttribute
  public long getNumPreprocessing() {
    return numPreprocessing.get();
  }

  @ManagedAttribute
  public long getNumPostprocessing() {
    return numPostprocessing.get();
  }

  @ManagedAttribute
  public long getNumPending() {
    return numPending.get();
  }

  @ManagedAttribute
  public long getNumComplete() {
    return numComplete.get();
  }

  @ManagedAttribute
  public long getNumFailed() {
    return numFailed.get();
  }

  @ManagedAttribute
  public long getNumRunning() {
    return numRunning.get();
  }

  @ManagedAttribute
  public long getPostprocessed() {
    return numPostprocessed.get();
  }

  @ManagedAttribute
  public long getTransferring() {
    return numTransferring.get();
  }
  
}
