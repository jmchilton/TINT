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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.jobqueue.execution.system.Queue;

public class PersistentQueueServiceImpl implements PersistentQueueService {
  private static final Log LOG = LogFactory.getLog(PersistentQueueServiceImpl.class);
  private EntityManager entityManager;

  @PersistenceContext(unitName = "systemjobqueue")
  public void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public JobImpl removeJob(final Queue queue, final String ticket) {
    final QueueImpl queueImpl = (QueueImpl) queue;

    entityManager.merge(queueImpl);

    final JobImpl jobImpl = queueImpl.remove(ticket);

    final JobImpl attachedJobImpl = entityManager.find(JobImpl.class, jobImpl.getTicket());

    entityManager.remove(attachedJobImpl);

    entityManager.flush();

    entityManager.clear();

    return jobImpl;
  }

  public QueueImpl load(final Long queueId) {
    QueueImpl queueImpl = entityManager.find(QueueImpl.class, queueId);

    if(queueImpl == null) {
      LOG.debug("Queue with id " + queueId + " does not exist in persistent context, creating new queue");
      final List<JobImpl> jobs = new LinkedList<JobImpl>();
      queueImpl = new QueueImpl(queueId, 0L, 0L, jobs);
      entityManager.persist(queueImpl);
    }

    entityManager.flush();

    entityManager.clear();

    return queueImpl;
  }

  public JobImpl popJob(final Queue queue) {
    final QueueImpl queueImpl = (QueueImpl) queue;

    // Merge queue back into persistent context
    entityManager.merge(queueImpl);

    final JobImpl jobImpl = queueImpl.popJob();

    if(jobImpl != null) {
      // All job instances are detached, find job instance and remove it
      final JobImpl attachedJobImpl = entityManager.find(JobImpl.class, jobImpl.getTicket());

      entityManager.remove(attachedJobImpl);
    }
    entityManager.flush();

    entityManager.clear();

    return jobImpl;
  }

  public JobImpl pushJob(final Queue queue, final String ticket, final String jobDescription) {
    final QueueImpl queueImpl = (QueueImpl) queue;

    // Before adding the following two lines, Hiberante would throw transient errors as follows:
    // object references an unsaved transient instance - save the transient instance before flushing
    // merge makes perfect sense, not sure why the flush is needed...
    entityManager.merge(queueImpl);

    entityManager.flush();

    final long jobPosition = queueImpl.getAndIncrementNumCreatedJobs();

    final JobImpl jobImpl = new JobImpl(ticket, jobPosition, jobDescription, queueImpl);

    entityManager.persist(jobImpl);

    queueImpl.pushJob(jobImpl);

    entityManager.flush();

    entityManager.clear();

    return jobImpl;
  }

}
