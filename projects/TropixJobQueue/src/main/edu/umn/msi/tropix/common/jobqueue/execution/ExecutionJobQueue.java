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

import java.util.Collection;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;

/**
 * 
 * @author John Chilton
 * 
 * @param <T>
 *          type of object which describes job to execute.
 */
public interface ExecutionJobQueue<T extends JobDescription> {
  void submitJob(T description);

  Collection<ExecutionJobInfo<T>> listJobs();

  void cancel(String localJobId);

  void init();

  public static final class ExecutionJobInfo<T extends JobDescription> {
    private final T jobDescription;
    private final ExecutionState executionState;

    private ExecutionJobInfo(final T jobDescription, final ExecutionState executionState) {
      this.jobDescription = jobDescription;
      this.executionState = executionState;
    }

    public static <T extends JobDescription> ExecutionJobInfo<T> create(final T jobDescription, final ExecutionState executionState) {
      return new ExecutionJobInfo<T>(jobDescription, executionState);
    }

    public T getJobDescription() {
      return jobDescription;
    }

    public ExecutionState getExecutionState() {
      return executionState;
    }
  }

}
