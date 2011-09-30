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

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.InitializationTracker;
import edu.umn.msi.tropix.common.concurrent.InitializationTrackers;
import edu.umn.msi.tropix.common.concurrent.PausableCallback;
import edu.umn.msi.tropix.common.concurrent.PausableStateTracker;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorPostProcessedListener;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorQueue;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorRecoverer;
import edu.umn.msi.tropix.common.jobqueue.QueueStage;
import edu.umn.msi.tropix.common.jobqueue.QueueStatusBeanImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionStateChangeListener;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue.ExecutionJobInfo;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackable;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;
import edu.umn.msi.tropix.common.jobqueue.status.PercentComplete;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusEntry;
import edu.umn.msi.tropix.common.jobqueue.status.WasCancelled;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

// TODO: Refactor out queueStatusBeanImpl, and setQueueStage()
@ManagedResource
public class JobProcessorQueueImpl<T extends JobDescription> implements JobProcessorQueue<T>, ExecutionStateChangeListener, PausableCallback {
  private static final Log LOG = LogFactory.getLog(JobProcessorQueueImpl.class);
  private static final long DEFAULT_TICKET_DURATION = 1000 * 60 * 60 * 2; // Keep completed ticket status around for 2 hours by default

  private final ConcurrentHashMap<Ticket, JobInfo> jobMap = new ConcurrentHashMap<Ticket, JobInfo>();

  // Must be set
  private ExecutionJobQueue<T> executionJobQueue = null;
  private JobProcessorRecoverer<T> jobProcessorRecoverer = null;
  private Executor executor = null;
  private JobProcessorPostProcessedListener<T> jobProcessorPostProccessedListener = null;
  private StatusModifier statusModifier;
  private Timer timer = null;
  private PausableStateTracker pausableStateTracker;
  private Supplier<Ticket> ticketSupplier;

  // May be set
  private QueueStatusBeanImpl queueStatusBeanImpl; // Optional
  private InitializationTracker<Ticket> initializationTracker = InitializationTrackers.getDefault();
  private QueueStageTracker queueStageTracker = new QueueStageTrackerImpl();

  /**
   * Amount of time (in milliseconds) that a ticket's status is available after the job has completed.
   */
  private long ticketDuration = DEFAULT_TICKET_DURATION;

  public JobProcessorQueueImpl() {
    LOG.debug("Constructing " + toString());
  }
  
  @PostConstruct
  public void init() {
    LOG.debug("Starting init...");
    Collection<ExecutionJobInfo<T>> jobInfoList = null;

    if(!pausableStateTracker.isPaused()) {
      pausableStateTracker.registerPausableCallback(this);
    }

    try {
      jobInfoList = executionJobQueue.listJobs();
    } catch(final Throwable t) {
      throw ExceptionUtils.logAndConvert(LOG, t, "Failed to fetch jobDescriptions, server should be killed and reconfigured properly");
    }

    if(jobInfoList != null && !jobInfoList.isEmpty()) {
      for(final ExecutionJobInfo<T> executionJobInfo : jobInfoList) {
        final T jobDescription = executionJobInfo.getJobDescription();
        final Ticket ticket = getTicket(jobDescription);
        final String jobType = jobDescription.getJobType();
        LOG.debug("Recovering job with ticket <" + ticket.getValue() + "> and jobType <" + jobType +"> and jobDescription " + jobDescription);
        final JobInfo jobInfo = getJobInfo(ticket, jobType);
        try {
          updateJobState(ticket, executionJobInfo.getExecutionState());
          final JobProcessor<T> jobProcessor = jobProcessorRecoverer.recover(jobDescription);
          if(jobProcessor != null) {
            jobInfo.jobProcessor = jobProcessor;
            initializationTracker.initialize(ticket);
          } else {
            LOG.debug("Failed to recover job corresponding to ticket " + ticket + " jobProcessorRecoverer returned null");
            initializationTracker.fail(ticket);
          }
        } catch(final RuntimeException t) {
          ExceptionUtils.logQuietly(LOG, t, "Failed to recover job with job description " + jobDescription);
          initializationTracker.fail(ticket);
        }
      }
    }
    updateStatus();
    initializationTracker.initializeAll();

    executionJobQueue.init();

    LOG.debug("Ending init...");
  }

  /**
   * ExecutionState's may be skipped, may come out of order. jobInfo.queueStage and jobInfo.jobProcessor may be not initialized yet.
   */
  private void updateJobState(final Ticket ticket, final ExecutionState executionState) {
    final JobInfo jobInfo = getJobInfo(ticket);
    final QueueStage stage = getQueueStage(executionState);
    boolean postprocess = false;
    boolean updated = false;
    synchronized(jobInfo) {
      final QueueStage currentStage = jobInfo.queueStage;
      // set stage if its more "advanced" in its life cycle than the current stage
      if(currentStage == null || currentStage.equals(QueueStage.PENDING) || (currentStage.equals(QueueStage.RUNNING) && stage.equals(QueueStage.POSTPROCESSING))) {
        updated = true;
        setQueueStage(jobInfo, stage);
      }

      // Check if its time to postprocess
      if(jobInfo.queueStage.equals(QueueStage.POSTPROCESSING) && !jobInfo.postprocessed) {
        jobInfo.postprocessed = true;
        postprocess = true;
      }
      if(jobInfo.progressTracker == null && jobInfo.jobProcessor instanceof ProgressTrackable) {
        try {
          jobInfo.progressTracker = ((ProgressTrackable) jobInfo.jobProcessor).getProgressTracker();
        } catch(final RuntimeException t) {
          ExceptionUtils.logQuietly(LOG, t, "Exception thrown while attempting to get ProgressTracker for job " + jobInfo.jobProcessor);
        }
      }
    }
    if(updated && jobInfo.progressTracker != null) {
      if(stage.equals(QueueStage.RUNNING)) {
        executor.execute(new Runnable() {
          public void run() {
            try {
              jobInfo.progressTracker.start(getProgressTrackerCallback(ticket));
            } catch(final Throwable t) {
              ExceptionUtils.logQuietly(LOG, t, "Error with progressTraacker " + jobInfo.progressTracker);
            }
          }
        });
      } else if(stage.equals(QueueStage.POSTPROCESSING)) {
        try {
          jobInfo.progressTracker.stop();
        } catch(final RuntimeException t) {
          ExceptionUtils.logQuietly(LOG, t, "Error attempting to stop progress tracker " + jobInfo.progressTracker);
        }
      }
    }

    if(postprocess) {
      postprocess(ticket, executionState.equals(ExecutionState.COMPLETE));
    }
  }

  public void jobStateChanged(final String localJobId, final ExecutionState executionState, final Throwable failureCause) {
    final Ticket ticket = getTicket(localJobId);
    final JobInfo jobInfo = getJobInfo(ticket);
    if(jobInfo != null && jobInfo.throwable == null) {
      jobInfo.throwable = failureCause;
    }
    updateJobState(ticket, executionState != null ? executionState : ExecutionState.ABSENT);
  }

  private ProgressTrackerCallback getProgressTrackerCallback(final Ticket ticket) {
    return new ProgressTrackerCallback() {
      private JobInfo info = getJobInfo(ticket);

      public void updateProgress(final Double percentComplete) {
        info.percentComplete = percentComplete;
      }
    };
  }

  /**
   * Precondition: postprocess has never been called with ticket before, jobMap contains jobInfo for this ticket, and queueStage is POSTPROCESSING.
   * 
   * @param ticket
   * @param executionCompletedNormally
   */
  private void postprocess(final Ticket ticket, final boolean executionCompletedNormally) {
    final Runnable postprocessingRunnable = new Runnable() {
      public String toString() {
        return "Postprocessing for ticket " + ticket.getValue();
      }

      public void run() {
        LOG.trace("postprocess runnable executing");
        boolean completedNormally = executionCompletedNormally;
        final JobInfo jobInfo = getJobInfo(ticket);
        try {
          initializationTracker.waitForInitialization(ticket); // See comment in init()
        } catch(final Throwable t) {
          ExceptionUtils.logQuietly(LOG, t, "Error while waiting for initialization of ticket " + ticket.getValue() + " to preform postprocessing.");
        }
        try {
          jobInfo.jobProcessor.postprocess(completedNormally);
        } catch(final Throwable t) {
          if(jobInfo.throwable == null) {
            jobInfo.throwable = t;
          }
          ExceptionUtils.logQuietly(LOG, t, "Failed to postprocess job with ticket " + ticket.getValue());
          completedNormally = false;
        }

        // jobCompletionListener should be signaled before status is changed to COMPLETED/FAILED.
        try {
          if(jobProcessorPostProccessedListener != null) {
            jobProcessorPostProccessedListener.jobPostProcessed(ticket, jobInfo.jobType, jobInfo.jobProcessor, completedNormally);
          }
        } catch(final Throwable t) {
          ExceptionUtils.logQuietly(LOG, t, "jobProcessorCompletionListener threw an exception");
        }

        setQueueStage(jobInfo, completedNormally ? QueueStage.POSTPROCESSED : QueueStage.FAILED);

        jobInfo.jobProcessor = null; // Not needed anymore, free the memory

        // Eventually remove the status from the jobMap, to prevent memory leak
        timer.schedule(new Runnable() {
          public void run() {
            synchronized(jobMap) {
              jobMap.remove(ticket);
            }
          }
        }, ticketDuration);
      }
    };
    executor.execute(postprocessingRunnable);
  }
  
  public void fail(final Ticket ticket) {
    final JobInfo jobInfo = getJobInfo(ticket);
    setQueueStage(jobInfo, QueueStage.FAILED);
  }

  public void complete(final Ticket ticket) {
    final JobInfo jobInfo = getJobInfo(ticket);
    setQueueStage(jobInfo, QueueStage.COMPLETED);    
  }
  
  public void transferring(final Ticket ticket) {
    final JobInfo jobInfo = getJobInfo(ticket);
    setQueueStage(jobInfo, QueueStage.TRANSFERRING);
  }
  
  public <S extends T> Ticket submitJob(final JobProcessor<S> jobProcessor, final String jobType) {
    if(pausableStateTracker.isPaused()) {
      throw new IllegalStateException("Queue is inactive.");
    }
    final Ticket newTicket = ticketSupplier.get();
    final JobInfo jobInfo = getJobInfo(newTicket, jobType);
    if(jobInfo.jobProcessor != null) {
      throw new IllegalStateException("Job with same ticket as submitted job found.");
    }
    setQueueStage(jobInfo, QueueStage.PREPROCESSING);
    jobInfo.jobProcessor = jobProcessor;
    initializationTracker.initialize(newTicket);

    final Runnable preprocessingRunnable = new Runnable() {
      public String toString() {
        return "Preprocessing for ticket " + newTicket.getValue();
      }

      public void run() {
        T jobDescription = null;
        boolean failed = false;
        try {
          try {
            jobDescription = jobProcessor.preprocess();
          } catch(final Throwable t) {
            if(jobInfo.throwable == null) {
              jobInfo.throwable = t;
            }
            ExceptionUtils.logQuietly(LOG, t, "Error while preprocessing job with jobProcessor " + jobProcessor);
            failed = true;
            return;
          }
          try {
            // This set statement must be executed before submitJob,
            // to prevent an update to RUNNING (or whatever) to come
            // before this set statement.
            synchronized(jobInfo.preprocessingLock) {
              if(jobInfo.cancelled) {
                throw new RuntimeException("Job cancelled during preprocessing");
              }
              setQueueStage(jobInfo, QueueStage.PENDING);
              jobDescription.setTicket(newTicket.getValue());
              // JobDescriptionUtils.setLocalJobId(jobDescription, newTicket.getValue());
              executionJobQueue.submitJob(jobDescription);
            }
          } catch(final RuntimeException t) {
            if(jobInfo.throwable == null) {
              jobInfo.throwable = t;
            }
            ExceptionUtils.logQuietly(LOG, t, "Failed to submit job to executionJobQueue");
            failed = true;
            return;
          }
        } finally {
          if(failed) {
            setQueueStage(jobInfo, QueueStage.POSTPROCESSING);
            postprocess(newTicket, false);
          }
        }
      }
    };
    executor.execute(preprocessingRunnable);
    return newTicket;
  }

  public Status getStatus(final Ticket ticket) {
    initializationTracker.waitForInitialization(ticket);
    final LinkedList<StatusEntry> entries = new LinkedList<StatusEntry>();
    final JobInfo jobInfo = jobMap.get(ticket); // jobMap.get(ticket);
    final QueueStage queueStage = jobInfo == null ? QueueStage.ABSENT : jobInfo.queueStage;
    final Status status = new Status();
    status.setStage(new Stage(queueStage.getStageEnumerationValue()));
    if(queueStage.equals(QueueStage.RUNNING)) {
      final Double percentComplete = jobInfo.percentComplete;
      if(percentComplete != null) {
        final PercentComplete percentCompleteObject = new PercentComplete();
        percentCompleteObject.setValue(percentComplete);
        entries.add(percentCompleteObject);
      }
    }
    if(jobInfo != null && jobInfo.cancelled) {
      entries.add(new WasCancelled(true));
    }
    status.setStatusEntry(entries.toArray(new StatusEntry[entries.size()]));
    // If the execution queue might have more information to add to the
    // status allow it too.
    if(queueStage.equals(QueueStage.PENDING) || queueStage.equals(QueueStage.RUNNING) || queueStage.equals(QueueStage.COMPLETED) || queueStage.equals(QueueStage.FAILED)) {
      if(statusModifier != null) {
        statusModifier.extendStatus(ticket.getValue(), status);
      }
    }
    return status;
  }

  public void cancel(final Ticket ticket) {
    final JobInfo jobInfo = jobMap.get(ticket);
    if(jobInfo == null) {
      throw new IllegalStateException("Cancel called when QueueStage was ABSENT");
    }

    synchronized(jobInfo.preprocessingLock) {
      final QueueStage queueStage = jobInfo == null ? QueueStage.ABSENT : jobInfo.queueStage;
      jobInfo.cancelled = true;
      if(queueStage.equals(QueueStage.PENDING) || queueStage.equals(QueueStage.RUNNING)) {
        executionJobQueue.cancel(ticket.getValue());
      } else if(!queueStage.equals(QueueStage.PREPROCESSING)) {
        throw new IllegalStateException("Cancel called when QueueStage wasn't PREPROCESSING, PENDING, or RUNNING");
      }
    }
  }

  private void setQueueStage(final JobInfo jobInfo, final QueueStage queueStage) {
    final QueueStage previousQueueStage = jobInfo.queueStage;
    jobInfo.queueStage = queueStage;
    queueStageTracker.switchStage(previousQueueStage, queueStage);
    updateStatus();
  }

  /**
   * Returns the JobInfo object associated with the given ticket, and atomically creates such an object if one doesn't exist.
   * 
   * @param ticket
   * @return
   */
  private JobInfo getJobInfo(final Ticket ticket, final String jobType) {
    final JobInfo jobInfo = new JobInfo();
    jobMap.putIfAbsent(ticket, jobInfo);    
    final JobInfo cachedJobInfo = jobMap.get(ticket);
    cachedJobInfo.jobType = jobType;
    return cachedJobInfo;
  }

  /**
   * Returns the JobInfo object associated with the given ticket, and atomically creates such an object if one doesn't exist.
   * 
   * @param ticket
   * @return
   */
  private JobInfo getJobInfo(final Ticket ticket) {
    final JobInfo jobInfo = new JobInfo();
    jobMap.putIfAbsent(ticket, jobInfo);
    return jobMap.get(ticket);
  }

  private Ticket getTicket(final T jobDescription) {
    final String localJobId = jobDescription.getTicket();
    return getTicket(localJobId);
  }

  private Ticket getTicket(final String localJobId) {
    final Ticket ticket = new Ticket();
    ticket.setValue(localJobId);
    return ticket;
  }

  private QueueStage getQueueStage(final ExecutionState executionState) {
    assert executionState != null;
    QueueStage stage = null;
    switch(executionState) {
    case PENDING:
      stage = QueueStage.PENDING;
      break;
    case RUNNING:
      stage = QueueStage.RUNNING;
      break;
    case COMPLETE:
    case FAILED:
    case ABSENT:
      stage = QueueStage.POSTPROCESSING;
      break;
    }
    return stage;
  }

  private void updateStatus() {
    try {
      final QueueStatus queueStatus = new QueueStatus();
      queueStatus.setSize((int) (queueStageTracker.getNumPending() + queueStageTracker.getNumPreprocessing()));
      queueStatus.setActive(!pausableStateTracker.isPaused());
      if(queueStatusBeanImpl != null) {
        queueStatusBeanImpl.set(queueStatus);
      }
    } catch(final Exception e) {
      ExceptionUtils.logQuietly(LOG, e);
    }
  }

  public void onShutdown() {
    updateStatus();
  }

  public void onStartup() {
    updateStatus();
  }

  public void setQueueStatusBean(final QueueStatusBeanImpl queueStatusBeanImpl) {
    this.queueStatusBeanImpl = queueStatusBeanImpl;
  }

  public void setTicketSupplier(final Supplier<Ticket> ticketSupplier) {
    this.ticketSupplier = ticketSupplier;
  }

  public void setJobProcessorPostProcessedListener(final JobProcessorPostProcessedListener<T> jobProcessorCompletionListener) {
    LOG.debug("Setting JobProcessorCompletionListener to " + jobProcessorCompletionListener);
    this.jobProcessorPostProccessedListener = jobProcessorCompletionListener;
  }

  public void setTimer(final Timer timer) {
    this.timer = timer;
  }

  @ManagedAttribute
  public void setTicketDuration(final long ticketDuration) {
    this.ticketDuration = ticketDuration;
  }

  @ManagedAttribute
  public long getTicketDuration() {
    return ticketDuration;
  }

  private class JobInfo {
    private String jobType = null;
    private Object throwable = null;
    private boolean cancelled = false;
    private final Object preprocessingLock = new Object(); // State will only change from PREPROCESSING to PENDING with this lock obtained
    private QueueStage queueStage = null;
    private JobProcessor<? extends T> jobProcessor = null;
    private boolean postprocessed = false;
    private Double percentComplete = null;
    private ProgressTracker progressTracker = null;
  }

  public void setExecutionJobQueue(final ExecutionJobQueue<T> executionJobQueue) {
    LOG.debug("Setting ExecutionJobQueue to " + executionJobQueue);
    this.executionJobQueue = executionJobQueue;
  }

  public void setJobProcessorRecoverer(final JobProcessorRecoverer<T> jobProcessorRecoverer) {
    this.jobProcessorRecoverer = jobProcessorRecoverer;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

  public void setInitializationTracker(final InitializationTracker<Ticket> initializationTracker) {
    this.initializationTracker = initializationTracker;
  }

  public void setStatusModifier(final StatusModifier statusModifier) {
    this.statusModifier = statusModifier;
  }

  public void setPausableStateTracker(final PausableStateTracker pausableStateTracker) {
    this.pausableStateTracker = pausableStateTracker;
  }

  public void setQueueStageTracker(final QueueStageTracker queueStageTracker) {
    this.queueStageTracker = queueStageTracker;
  }

}
