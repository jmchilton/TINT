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

package edu.umn.msi.tropix.common.jobqueue.execution.system;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.globus.exec.generated.JobDescriptionType;

import com.google.common.annotations.VisibleForTesting;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.impl.StatusModifier;
import edu.umn.msi.tropix.common.jobqueue.status.EstimatedExecutionTime;
import edu.umn.msi.tropix.common.jobqueue.status.EstimatedPendingTime;
import edu.umn.msi.tropix.common.jobqueue.status.QueuePosition;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusEntry;
import edu.umn.msi.tropix.common.jobqueue.status.StatusModifierManager;
import edu.umn.msi.tropix.common.prediction.Model;
import edu.umn.msi.tropix.common.prediction.Models;
import edu.umn.msi.tropix.common.time.TimeProvider;
import edu.umn.msi.tropix.common.time.TimeProviders;

public class StatusExecutionJobQueueObserverImpl implements ExecutionJobQueueObserver<Job>, StatusModifier {
  private TimeProvider timeProvider = TimeProviders.getInstance();
  private final ConcurrentHashMap<String, JobInfo> jobInfoMap = new ConcurrentHashMap<String, JobInfo>();
  // State lock must be obtained before modifying pendingJobs,executingJobs, and/or lastPending.
  // A job should be in pendingJobs iff a Pending state has been obtained and no other state
  // A job should be in executingJobs iff a Running state has been obtained and Complete,Fail,and/or Absent have not
  // been obtained.
  private final TreeSet<JobInfo> pendingJobs = new TreeSet<JobInfo>(JOB_INFO_COMPARATOR);
  private final TreeSet<JobInfo> executingJobs = new TreeSet<JobInfo>(JOB_INFO_COMPARATOR);
  private StatusModifierManager statusModifierManager;
  private Long lastPendingTimeUpdate = null;
  private final Object stateLock = new Object();
  private int numPendingJobs = 0;
  private final int numConcurrentJobs = 1;
  private Model executionTimeModel = Models.getNullModel();

  private double[] getDataVector(final JobInfo jobInfo) {
    return new double[] {};
  }

  private void onPending(final JobInfo jobInfo) {
    synchronized(stateLock) {
      if(!jobInfo.pending || jobInfo.complete) {
        return;
      }
      pendingJobs.add(jobInfo);
      jobInfo.executionTime = executionTimeModel.predict(getDataVector(jobInfo));
      updateState(false);
    }
  }

  private void onRunning(final JobInfo info) {
    synchronized(stateLock) {
      if(info.complete) {
        return;
      }
      info.pending = false;
      if(info.executionTime == null) {
        info.executionTime = executionTimeModel.predict(getDataVector(info));
      }
      info.startTime = timeProvider.currentTimeMillis();
      pendingJobs.remove(info);
      executingJobs.add(info);
      updateState(false);
    }
  }

  private void onDone(final JobInfo jobInfo) {
    synchronized(stateLock) {
      jobInfo.complete = true;
      jobInfo.pending = false;
      pendingJobs.remove(jobInfo);
      executingJobs.remove(jobInfo);
      updateState(false);
    }
  }

  private void onComplete(final JobInfo jobInfo) {
    boolean addedData = false;
    if(jobInfo.startTime != null) {
      final long executionTime = timeProvider.currentTimeMillis() - jobInfo.startTime;
      executionTimeModel.addData(getDataVector(jobInfo), executionTime);
      addedData = true;
    }
    synchronized(stateLock) {
      jobInfo.pending = false;
      jobInfo.complete = true;

      pendingJobs.remove(jobInfo);
      executingJobs.remove(jobInfo);
      updateState(addedData);
    }
  }

  private void updateState(final boolean repredictTimes) {
    if(repredictTimes) {
      for(final JobInfo executingJobInfo : executingJobs) {
        final Double executionTime = executionTimeModel.predict(getDataVector(executingJobInfo));
        executingJobInfo.executionTime = executionTime;
      }
      for(final JobInfo pendingJobInfo : pendingJobs) {
        final Double executionTime = executionTimeModel.predict(getDataVector(pendingJobInfo));
        pendingJobInfo.executionTime = executionTime;
      }
    }
    Double expectedWait = getInitialExpectedWait();
    int position = 1;
    for(final JobInfo pendingJobInfo : pendingJobs) {
      final Double executionTime = pendingJobInfo.executionTime;
      pendingJobInfo.pendingTime = expectedWait;
      pendingJobInfo.pendingPosition = position++;
      if(expectedWait != null && executionTime != null) {
        expectedWait += (executionTime / numConcurrentJobs);
      } else {
        expectedWait = null;
      }
    }
    lastPendingTimeUpdate = timeProvider.currentTimeMillis();
    numPendingJobs = pendingJobs.size();
  }

  private JobInfo putIfAbsent(final JobInfo jobInfo) {
    final JobInfo previousJobInfo = jobInfoMap.putIfAbsent(jobInfo.localJobId, jobInfo);
    if(previousJobInfo == null) {
      statusModifierManager.registerStatusModifier(jobInfo.localJobId, this);
    }
    return jobInfoMap.get(jobInfo.localJobId);
  }

  private void remove(final String localJobId) {
    jobInfoMap.remove(localJobId);
    statusModifierManager.unregisterStatusModifier(localJobId, this);
  }

  public void onExecutionStateChange(final Job job, final ExecutionState executionState, final Throwable throwable) {
    final String localJobId = job.getTicket();
    JobInfo jobInfo = new JobInfo(localJobId);
    final long position = job.getPosition();
    jobInfo.position = position;
    jobInfo = putIfAbsent(jobInfo); // jobInfoMap.putIfAbsent(localJobId, jobInfo); jobInfoMap.get(localJobId);

    switch(executionState) {
    case PENDING:
      onPending(jobInfo);
      break;
    case RUNNING:
      onRunning(jobInfo);
      break;
    case COMPLETE:
      onComplete(jobInfo);
      break;
    case FAILED:
    case ABSENT:
      onDone(jobInfo);
      break;
    }

    if(executionState.equals(ExecutionState.ABSENT)) {
      remove(jobInfo.localJobId);
    }
  }

  public void onSubmission(final JobDescriptionType jobDescription) {
  }

  public void onShutdown() {
  }

  public void onStartup() {
  }

  public void extendStatus(final String localJobId, final Status status) {
    final JobInfo jobInfo = jobInfoMap.get(localJobId);
    final LinkedList<StatusEntry> entries = new LinkedList<StatusEntry>();
    if(status.getStatusEntry() != null) {
      entries.addAll(Arrays.asList(status.getStatusEntry()));
    }
    if(jobInfo != null && jobInfo.pendingPosition != null) {
      final QueuePosition queuePosition = new QueuePosition();
      // Next two lines may be slightly inconsistent in theory (for
      // instance might think queue position is 8 / 7). But synchronizing
      // to fix this would be over kill in my opinion -John.
      queuePosition.setValue(jobInfo.pendingPosition);
      queuePosition.setQueueSize(numPendingJobs);
      status.setQueuePosition(queuePosition);
    }
    if(jobInfo != null && jobInfo.pendingTime != null) {
      final EstimatedPendingTime estimatedPendingTime = new EstimatedPendingTime();
      final long timeSinceUpdate = timeProvider.currentTimeMillis() - this.lastPendingTimeUpdate;
      if(timeSinceUpdate > jobInfo.pendingTime.longValue()) {
        estimatedPendingTime.setValue(0L);
      } else {
        estimatedPendingTime.setValue(jobInfo.pendingTime.longValue() - timeSinceUpdate);
      }
      entries.add(estimatedPendingTime);
    }
    if(jobInfo != null && jobInfo.executionTime != null) {
      final EstimatedExecutionTime estimatedExecutionTime = new EstimatedExecutionTime();
      long executionTime = jobInfo.executionTime.longValue();
      if(jobInfo.startTime != null) {
        final long timeSinceStart = timeProvider.currentTimeMillis() - jobInfo.startTime;
        if(timeSinceStart < executionTime) {
          executionTime -= timeSinceStart;
        } else {
          executionTime = 0;
        }
      }
      estimatedExecutionTime.setValue(executionTime);
      entries.add(estimatedExecutionTime);
    }
    status.setStatusEntry(entries.toArray(new StatusEntry[] {}));
  }

  private class JobInfo {
    JobInfo(final String localJobId) {
      this.localJobId = localJobId;
    }

    private final String localJobId;
    private long position;
    private boolean pending = true;
    private boolean complete = false;
    private Integer pendingPosition = null;
    private Double pendingTime = null;
    private Double executionTime = null;
    private Long startTime = null;
  }

  private static final Comparator<JobInfo> JOB_INFO_COMPARATOR = new Comparator<JobInfo>() {
    public int compare(final JobInfo info1, final JobInfo info2) {
      if(info1.position < info2.position) {
        return -1;
      } else if(info1.position > info2.position) {
        return 1;
      } else {
        return 0;
      }
    }
  };

  private Double getInitialExpectedWait() {
    double totalExpectedWait = 0.0;
    final long now = timeProvider.currentTimeMillis();
    synchronized(stateLock) {
      for(final JobInfo jobInfo : executingJobs) {
        if(jobInfo.executionTime != null) {
          final double expectedWait = (jobInfo.startTime + jobInfo.executionTime) - now;
          if(expectedWait > 0) {
            totalExpectedWait += expectedWait;
          }
        } else {
          return null;
        }
      }
    }
    return totalExpectedWait / numConcurrentJobs;
  }

  public void setExecutionTimeModel(final Model executionTimeModel) {
    this.executionTimeModel = executionTimeModel;
  }

  public void setStatusModifierManager(final StatusModifierManager statusModifierManager) {
    this.statusModifierManager = statusModifierManager;
  }

  @VisibleForTesting
  void setTimeProvider(final TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }

}
