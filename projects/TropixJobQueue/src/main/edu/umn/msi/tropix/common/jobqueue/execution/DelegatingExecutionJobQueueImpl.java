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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;

public class DelegatingExecutionJobQueueImpl implements ExecutionJobQueue<JobDescription> {
  private static final Log LOG = LogFactory.getLog(DelegatingExecutionJobQueueImpl.class);
  private final Map<String, ExecutionJobQueue<JobDescription>> executionJobQueueMap = Maps.newHashMap();

  public DelegatingExecutionJobQueueImpl() {
    LOG.debug("Constructing " + toString());
  }
  
  public void cancel(final String localJobId) {
    for(final ExecutionJobQueue<JobDescription> executionJobQueue : executionJobQueueMap.values()) {
      executionJobQueue.cancel(localJobId);
    }
  }

  public Collection<ExecutionJobInfo<JobDescription>> listJobs() {
    final List<ExecutionJobInfo<JobDescription>> jobs = Lists.newLinkedList();
    for(final ExecutionJobQueue<JobDescription> executionJobQueue : executionJobQueueMap.values()) {
      jobs.addAll(executionJobQueue.listJobs());
    }
    return jobs;
  }

  public void submitJob(final JobDescription description) {
    final String executionType = description.getExecutionType();
    final ExecutionJobQueue<JobDescription> executionJobQueue = executionJobQueueMap.get(executionType);
    executionJobQueue.submitJob(description);
  }

  public void init() {
    for(final ExecutionJobQueue<JobDescription> executionJobQueue : executionJobQueueMap.values()) {
      executionJobQueue.init();
    }
  }

  public void setExecutionJobQueues(final Iterable<ExecutionJobQueue<JobDescription>> executionJobQueues) {
    LOG.debug("Setting delegate job queues to " + Iterables.toString(executionJobQueues));
    for(final ExecutionJobQueue<JobDescription> executionJobQueue : executionJobQueues) {
      final ExecutionType executionType = AnnotationUtils.findAnnotation(executionJobQueue.getClass(), ExecutionType.class);
      executionJobQueueMap.put(executionType.value(), executionJobQueue);
    }
  }

}
