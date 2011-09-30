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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.execution.DelegatingExecutionJobQueueObserverImpl;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueueObserver;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.system.Job;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class DelegatingExecutionJobQueueObserverImplTest {

  @Test(groups = "unit")
  public void delegationToTwo() {
    for(final Type type : Type.values()) {
      delegation(0, type, false);
      delegation(1, type, true);
      delegation(3, type, false);
    }
  }

  enum Type {
    STATE, SUBMISSION, SHUTDOWN, STARTUP
  }

  public void delegation(final int num, final Type type, final boolean throwException) {
    final List<ExecutionJobQueueObserver<Job>> observers = new ArrayList<ExecutionJobQueueObserver<Job>>(num);
    final Job job = EasyMock.createMock(Job.class);
    final JobDescriptionType jobDescription = new JobDescriptionType();
    for(int i = 0; i < num; i++) {
      final ExecutionJobQueueObserver<Job> observer = EasyMock.createMock(ExecutionJobQueueObserver.class);
      observers.add(observer);
      if(type.equals(Type.STATE)) {
        observer.onExecutionStateChange(job, ExecutionState.COMPLETE, null);
      } else if(type.equals(Type.STARTUP)) {
        observer.onStartup();
      } else if(type.equals(Type.SHUTDOWN)) {
        observer.onShutdown();
      } else if(type.equals(Type.SUBMISSION)) {
        observer.onSubmission(EasyMock.same(jobDescription));
      }
      if(throwException) {
        EasyMock.expectLastCall().andThrow(new RuntimeException());
      }
      EasyMock.replay(observer);
    }
    final DelegatingExecutionJobQueueObserverImpl<Job> dObserver = new DelegatingExecutionJobQueueObserverImpl<Job>();
    dObserver.setObservers(observers);

    if(type.equals(Type.STATE)) {
      dObserver.onExecutionStateChange(job, ExecutionState.COMPLETE, null);
    } else if(type.equals(Type.STARTUP)) {
      dObserver.onStartup();
    } else if(type.equals(Type.SHUTDOWN)) {
      dObserver.onShutdown();
    } else if(type.equals(Type.SUBMISSION)) {
      dObserver.onSubmission(jobDescription);
    }
    EasyMockUtils.verifyAll(observers.toArray());
  }
}
