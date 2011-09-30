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

package edu.umn.msi.tropix.common.jobqueue.impl;

import java.util.Map;

import net.jmchilton.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorRecoverer;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;

public class DispatchingJobProcessorRecovererImpl extends AnnotatedBeanProcessor<JobType> implements JobProcessorRecoverer<JobDescription> {
  private static final Log LOG = LogFactory.getLog(DispatchingJobProcessorRecovererImpl.class);
  private final CountDownLatch latch = new CountDownLatch(1);
  private final Map<String, JobProcessorRecoverer<JobDescription>> jobProcessorRecoverers = Maps.newHashMap();

  public DispatchingJobProcessorRecovererImpl() {
    super(JobType.class);
  }

  protected void processBeans(final Iterable<Object> annotatedBeans) {
    for(final Object annotatedBean : annotatedBeans) {
      if(annotatedBean instanceof JobProcessorRecoverer<?>) {
        @SuppressWarnings("unchecked")
        final JobProcessorRecoverer<JobDescription> factory = (JobProcessorRecoverer<JobDescription>) annotatedBean;
        final JobType jobProcessorType = getAnnotation(factory);
        LOG.info("Registering jobProcessorRecoverer for jobType <" + jobProcessorType.value() + "> -- jobProcessorRecoverer is " + factory.toString());
        jobProcessorRecoverers.put(jobProcessorType.value(), factory);
      }
    }
    latch.countDown();
  }

  public JobProcessor<JobDescription> recover(final JobDescription jobDescription) {
    latch.await();
    final String jobType = jobDescription.getJobType();
    final JobProcessorRecoverer<JobDescription> jobProcessorRecoverer = jobProcessorRecoverers.get(jobType);
    JobProcessor<JobDescription> jobProcessor = null;
    if(jobProcessorRecoverer != null) {
      jobProcessor = jobProcessorRecoverer.recover(jobDescription);
    }
    return jobProcessor;
  }

}
