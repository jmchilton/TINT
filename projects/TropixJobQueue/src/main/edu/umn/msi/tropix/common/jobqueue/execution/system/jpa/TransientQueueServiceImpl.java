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

package edu.umn.msi.tropix.common.jobqueue.execution.system.jpa;

import java.util.Map;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;

public class TransientQueueServiceImpl implements PersistentQueueService {
  private final Map<Long, QueueImpl> queueMap = Maps.newHashMap();
  
  public QueueImpl load(final Long queueId) {
    if(!queueMap.containsKey(queueId)) {
      queueMap.put(queueId, new QueueImpl(queueId));
    }
    return queueMap.get(queueId);
  }

  public JobImpl popJob(final Queue queue) {
    final QueueImpl queueImpl = (QueueImpl) queue;
    return queueImpl.popJob();
  }

  public JobImpl pushJob(final Queue queue, final String ticket, final String jobDescription) {
    final QueueImpl queueImpl = (QueueImpl) queue;
    final JobImpl jobImpl = new JobImpl(ticket, queueImpl.getAndIncrementNumCreatedJobs(), jobDescription, queueImpl);
    queueImpl.pushJob(jobImpl);
    return jobImpl;
  }

  public JobImpl removeJob(final Queue queue, final String ticket) {
    final QueueImpl queueImpl = (QueueImpl) queue;
    return queueImpl.remove(ticket);
  }

}
