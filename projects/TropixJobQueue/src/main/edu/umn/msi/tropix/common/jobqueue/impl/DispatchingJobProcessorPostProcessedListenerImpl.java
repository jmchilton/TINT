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
import edu.umn.msi.tropix.common.jobqueue.JobProcessorPostProcessedListener;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;

public class DispatchingJobProcessorPostProcessedListenerImpl extends AnnotatedBeanProcessor<JobType> implements JobProcessorPostProcessedListener<JobDescription> {
  private static final Log LOG = LogFactory.getLog(DispatchingJobProcessorPostProcessedListenerImpl.class);
  private final CountDownLatch latch = new CountDownLatch(1);
  private final Map<String, JobProcessorPostProcessedListener<JobDescription>> jobProcessorPostProcessedListeners = Maps.newHashMap();

  public DispatchingJobProcessorPostProcessedListenerImpl() {
    super(JobType.class);
    LOG.info("Constructing a DispatchingJobProcessorCompletionListenerImpl");
  }

  public <S extends JobDescription> void jobPostProcessed(final Ticket ticket, final String jobType, final JobProcessor<S> jobProcessor, final boolean completeNormally) {
    latch.await();
    final JobProcessorPostProcessedListener<JobDescription> listener = jobProcessorPostProcessedListeners.get(jobType);
    LOG.debug("In jobComplete with jobType " + jobType + " and ticket " + ticket.getValue() + " and listener " + listener + "completeNormally is " + completeNormally);
    listener.jobPostProcessed(ticket, jobType, jobProcessor, completeNormally);
  }

  protected void processBeans(final Iterable<Object> annotatedBeans) {
    for(final Object annotatedBean : annotatedBeans) {
      if(annotatedBean instanceof JobProcessorPostProcessedListener<?>) {
        @SuppressWarnings("unchecked")
        final JobProcessorPostProcessedListener<JobDescription> listener = (JobProcessorPostProcessedListener<JobDescription>) annotatedBean;
        final JobType jobProcessorType = getAnnotation(listener);
        LOG.info("Registering jobProcessorCompletionListener for jobType <" + jobProcessorType.value() + "> -- jobProcessorCompletionListener is " + listener.toString());
        jobProcessorPostProcessedListeners.put(jobProcessorType.value(), listener);
      }
    }
    latch.countDown();
  }

}
