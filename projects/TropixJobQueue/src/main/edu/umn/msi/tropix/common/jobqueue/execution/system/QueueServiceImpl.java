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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.concurrent.Callable;
import edu.umn.msi.tropix.common.jobqueue.execution.system.jpa.PersistentQueueService;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class QueueServiceImpl implements QueueService {
  private static final Log LOG = LogFactory.getLog(QueueServiceImpl.class);
  private final Object queueLock = new Object();
  private Queue queue;
  private PersistentQueueService persistentQueueService;

  public Collection<Job> getJobs() {
    synchronized(queueLock) {
      return queue.getJobs();
    }
  }

  public Job popJob() {
    return executeProtectingQueue(new Callable<Job>() {
      public Job call() {
        return persistentQueueService.popJob(queue);
      }
    });
  }

  public Job pushJob(final String ticket, final String jobDescription) {
    return executeProtectingQueue(new Callable<Job>() {
      public Job call() {
        return persistentQueueService.pushJob(queue, ticket, jobDescription);
      }
    });
  }

  public Job removeJob(final String ticket) {
    return executeProtectingQueue(new Callable<Job>() {
      public Job call() {
        return persistentQueueService.removeJob(queue, ticket);
      }
    });
  }

  public void setPersistentQueueService(final PersistentQueueService persistentQueueService) {
    this.persistentQueueService = persistentQueueService;
  }

  public void setQueue(final Queue queue) {
    this.queue = queue;
  }

  private <T> T executeProtectingQueue(final Callable<T> callable) {
    synchronized(queueLock) {
      final Queue backupQueue = queue.copy();
      try {
        return callable.call();
      } catch(final Throwable t) {
        queue = backupQueue;
        throw ExceptionUtils.logAndConvert(LOG, t, "Exception thrown while executing queue operation.", IllegalStateException.class);
      }
    }
  }
}
