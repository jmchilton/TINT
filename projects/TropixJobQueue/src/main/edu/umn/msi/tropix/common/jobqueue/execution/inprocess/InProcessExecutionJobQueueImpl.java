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

package edu.umn.msi.tropix.common.jobqueue.execution.inprocess;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionJobQueue;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionStateChangeListener;
import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionType;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.shutdown.ShutdownAware;

@ExecutionType("inprocess")
public class InProcessExecutionJobQueueImpl implements ExecutionJobQueue<InProcessJobDescription>, ShutdownAware {
  private static final Log LOG = LogFactory.getLog(InProcessExecutionJobQueueImpl.class);
  private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
  private final ConcurrentHashMap<String, InProcessJobDescription> map = new ConcurrentHashMap<String, InProcessJobDescription>();
  private final AtomicBoolean shutdown = new AtomicBoolean(false);
  private ExecutionStateChangeListener executionStateChangeListener;
  private Executor executor;

  public void init() {
    executor.execute(new Runnable() {
      public void run() {
        LOG.info("Starting up job poller for inprocess queue");
        do { // Using a do instead of a do while for improved testability
          String ticket = null;
          try {
            ticket = queue.poll(1, TimeUnit.SECONDS);
          } catch(final Exception e) {
            ticket = null;
          }
          if(ticket == null) {
            continue;
          }
          executionStateChangeListener.jobStateChanged(ticket, ExecutionState.RUNNING, null);
          final InProcessJobDescription jobDescription = map.remove(ticket);
          try {
            jobDescription.execute();
            executionStateChangeListener.jobStateChanged(ticket, ExecutionState.COMPLETE, null);
          } catch(final RuntimeException e) {
            ExceptionUtils.logQuietly(LOG, e, "Failed to execute inprocess job description");
            executionStateChangeListener.jobStateChanged(ticket, ExecutionState.FAILED, e);
          }
        } while(!shutdown.get());
        LOG.info("Quit job poller for inprocess queue");
      }
    });
  }

  // TODO
  public void cancel(final String ticket) {
    return;
  }

  public Collection<ExecutionJobInfo<InProcessJobDescription>> listJobs() {
    return Lists.newArrayList();
  }

  public void submitJob(final InProcessJobDescription description) {
    final String ticket = description.getTicket();
    map.put(ticket, description);
    queue.offer(ticket);
  }

  public void destroy() {
    LOG.info("Attempting to shutdown in process queue.");
    shutdown.set(true);
  }

  public void setExecutionStateChangeListener(final ExecutionStateChangeListener executionStateChangeListener) {
    this.executionStateChangeListener = executionStateChangeListener;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

}
