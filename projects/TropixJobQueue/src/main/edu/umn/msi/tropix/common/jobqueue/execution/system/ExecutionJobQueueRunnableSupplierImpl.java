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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionFactory;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class ExecutionJobQueueRunnableSupplierImpl implements Supplier<ExecutionJobQueueRunnable> {
  private static final Log LOG = LogFactory.getLog(ExecutionJobQueueRunnableSupplierImpl.class);
  private Function<JobDescriptionType, ExecutionConfiguration> jobDescriptionFunction;
  private ExecutionFactory executionFactory;

  public ExecutionJobQueueRunnable get() {
    return new ExecutionJobQueueRunnable() {
      private ExecutionJobQueueRunnableCallback executionJobQueueRunnableCallback;
      private Job job;

      class ExecutionCallbackImpl implements ExecutionCallback {
        private String localJobId;

        public void onCompletion(final ExecutionHook executionHook) {
          final Throwable t = executionHook.getException();
          if(t != null) {
            ExceptionUtils.logQuietly(LOG, t, "Job with id " + localJobId + " failed.");
            executionJobQueueRunnableCallback.onComplete(ExecutionState.FAILED);
          } else if(executionHook.getKilled()) {
            LOG.info("Job with id " + localJobId + " was killed (timedout? " + executionHook.getTimedOut() + ")");
            executionJobQueueRunnableCallback.onComplete(ExecutionState.FAILED);
          } else {
            LOG.info("Job with id " + localJobId + " finished properly.");
            executionJobQueueRunnableCallback.onComplete(ExecutionState.COMPLETE);
          }
        }
      }

      private ExecutionCallback getExecutionCallback(final Job job) {
        final ExecutionCallbackImpl eCallback = new ExecutionCallbackImpl();
        eCallback.localJobId = job.getTicket();
        return eCallback;
      }

      public void run() {
        ExecutionHook executionHook = null;
        try {
          final JobDescriptionType jobDescription = JobDescriptionUtils.deserialize(job.getDescription());
          final ExecutionConfiguration executionConfiguration = jobDescriptionFunction.apply(jobDescription);
          executionHook = executionFactory.execute(executionConfiguration, getExecutionCallback(job));
        } catch(final Throwable t) {
          ExceptionUtils.logQuietly(LOG, t, "Falied to setup job " + job);
          executionJobQueueRunnableCallback.onComplete(ExecutionState.FAILED);
          return;
        }
        executionJobQueueRunnableCallback.onRunning(executionHook);
      }

      public void setExecutionRunnableCallback(final ExecutionJobQueueRunnableCallback executionJobQueueRunnableCallback) {
        this.executionJobQueueRunnableCallback = executionJobQueueRunnableCallback;
      }

      public void setJob(final Job job) {
        this.job = job;
      }

    };
  }

  public void setJobDescriptionFunction(final Function<JobDescriptionType, ExecutionConfiguration> jobDescriptionFunction) {
    this.jobDescriptionFunction = jobDescriptionFunction;
  }

  public void setExecutionFactory(final ExecutionFactory executionFactory) {
    this.executionFactory = executionFactory;
  }
}
