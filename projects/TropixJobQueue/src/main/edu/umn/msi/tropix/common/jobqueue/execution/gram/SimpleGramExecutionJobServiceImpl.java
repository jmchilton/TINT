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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleGramExecutionJobServiceImpl implements GramExecutionJobService {
  private final ConcurrentHashMap<String, GramExecutionJobImpl> jobMap = new ConcurrentHashMap<String, GramExecutionJobImpl>();

  public GramExecutionJob loadJob(final String localJobId) {
    return jobMap.get(localJobId);
  }

  public Collection<GramExecutionJob> loadJobs() {
    final LinkedList<GramExecutionJob> jobs = new LinkedList<GramExecutionJob>();
    jobs.addAll(jobMap.values());
    return jobs;
  }

  public GramExecutionJob persistJob(final String handle, final String description, final String localJobId, final String proxy, final String state) {
    final GramExecutionJobImpl job = new GramExecutionJobImpl(localJobId, description, handle, proxy, state);
    jobMap.put(localJobId, job);
    return job;
  }

  public void updateState(final String localJobId, final String state) {
    final GramExecutionJobImpl job = jobMap.get(localJobId);
    synchronized(job) {
      job.setState(state);
    }
  }

  public void delete(final String localJobId) {
    jobMap.remove(localJobId);
  }

}
