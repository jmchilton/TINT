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

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryComponent;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;
import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;

public class GridJobClientFactoryManagerImpl extends AnnotatedBeanProcessor<JobClientFactoryComponent> implements JobClientFactoryManager {
  private final Map<String, JobClientFactory> factoryBeans = Maps.newHashMap();

  protected GridJobClientFactoryManagerImpl() {
    super(JobClientFactoryComponent.class);
  }

  protected void processBeans(final Iterable<Object> annotatedBeans) {
    for(final Object annotatedBean : annotatedBeans) {
      if(annotatedBean instanceof JobClientFactory) {
        final JobClientFactoryComponent annotation = getAnnotation(annotatedBean);
        factoryBeans.put(annotation.serviceName(), (JobClientFactory) annotatedBean);
      }
    }
  }

  public JobClientFactory getFactory(final String serviceAddress) {
    final String serviceName = serviceAddress.substring(serviceAddress.lastIndexOf("/") + 1);
    return factoryBeans.get(serviceName);
  }

}
