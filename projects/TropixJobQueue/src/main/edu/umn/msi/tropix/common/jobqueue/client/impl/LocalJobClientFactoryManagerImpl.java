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

package edu.umn.msi.tropix.common.jobqueue.client.impl;

import java.util.Map;

import net.jmchilton.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;

public class LocalJobClientFactoryManagerImpl extends AnnotatedBeanProcessor<JobType> implements JobClientFactoryManager {
  private static final Log LOG = LogFactory.getLog(LocalJobClientFactoryManagerImpl.class);
  private final CountDownLatch latch = new CountDownLatch(1);
  private final Map<String, JobClientFactory> jobClientFactories = Maps.newHashMap();
  private Function<JobQueueContext, JobClientFactory> localJobClientFactoryFunction;
  
  public LocalJobClientFactoryManagerImpl() {
    super(JobType.class);
  }

  public void setLocalJobClientFactoryFunction(final Function<JobQueueContext, JobClientFactory> localJobClientFactoryFunction) {
    this.localJobClientFactoryFunction = localJobClientFactoryFunction;
  }
  
  protected void processBeans(final Iterable<Object> annotatedBeans) {
    LOG.info("Processing local JobQueueContext beans with @JobType annotations");
    for(final Object annotatedBean : annotatedBeans) {
      if(annotatedBean instanceof JobQueueContext) {
        final JobQueueContext jobQueueContext = (JobQueueContext) annotatedBean;
        final JobType jobProcessorType = getAnnotation(jobQueueContext);
        final String registerMessage = "Registering jobClientFactory for jobType " + jobProcessorType.value() + " -- jobQueueContext is " + jobQueueContext;
        LOG.info(registerMessage);
        final JobClientFactory jobClientFactory = localJobClientFactoryFunction.apply(jobQueueContext);
        jobClientFactories.put(jobProcessorType.value(), jobClientFactory);
      }
    }
    latch.countDown();
  }

  public JobClientFactory getFactory(final String serviceAddress) {
    latch.await();
    final String serviceName = serviceAddress.substring(serviceAddress.lastIndexOf("/") + 1);
    return jobClientFactories.get(serviceName);
  }
  
}
