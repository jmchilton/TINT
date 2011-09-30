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

package edu.umn.msi.tropix.common.jobqueue.execution;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class DelegatingExecutionJobQueueObserverImpl<T extends ExecutionJob> implements ExecutionJobQueueObserver<T> {
  private static final Log LOG = LogFactory.getLog(DelegatingExecutionJobQueueObserverImpl.class);
  private List<ExecutionJobQueueObserver<T>> observers = Collections.emptyList();

  public void setObservers(final List<ExecutionJobQueueObserver<T>> observers) {
    this.observers = observers;
  }

  public void onExecutionStateChange(final T job, final ExecutionState executionState, final Throwable throwable) {
    executeWithEachObserver("state change", new ObserverAction<T>() {
      public void run(final ExecutionJobQueueObserver<T> observer) {
        observer.onExecutionStateChange(job, executionState, throwable);
      }
    });
  }

  public void onSubmission(final JobDescriptionType jobDescription) {
    executeWithEachObserver("onSubmission", new ObserverAction<T>() {
      public void run(final ExecutionJobQueueObserver<T> observer) {
        observer.onSubmission(jobDescription);
      }
    });
  }

  public void onShutdown() {
    executeWithEachObserver("onShutdown", new ObserverAction<T>() {
      public void run(final ExecutionJobQueueObserver<T> observer) {
        observer.onShutdown();
      }
    });
  }

  public void onStartup() {
    executeWithEachObserver("onStartup", new ObserverAction<T>() {
      public void run(final ExecutionJobQueueObserver<T> observer) {
        observer.onStartup();
      }
    });
  }

  private interface ObserverAction<T extends ExecutionJob> {
    void run(ExecutionJobQueueObserver<T> observer);
  }

  private void executeWithEachObserver(final String actionDescription, final ObserverAction<T> action) {
    for(final ExecutionJobQueueObserver<T> observer : observers) {
      try {
        action.run(observer);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(LOG, t, "Failed to execute " + actionDescription + " on queue observer " + observer);
      }
    }
  }

}
