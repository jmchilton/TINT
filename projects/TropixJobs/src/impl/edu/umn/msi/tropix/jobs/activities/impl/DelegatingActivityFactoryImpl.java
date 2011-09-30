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

package edu.umn.msi.tropix.jobs.activities.impl;

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;

@ManagedBean @Named("activityFactory")
public class DelegatingActivityFactoryImpl extends AnnotatedBeanProcessor<ActivityFactoryFor> implements ActivityFactory<ActivityDescription> {
  private final Map<Class<?>, ActivityFactory<? extends ActivityDescription>> activityFactories = Maps.newHashMap();
  
  public DelegatingActivityFactoryImpl() {
    super(ActivityFactoryFor.class);
  }
  
  @SuppressWarnings("unchecked")
  public Activity getActivity(final ActivityDescription activityDescription, final ActivityContext activityContext) {
    Class<?> descriptionClass = activityDescription.getClass();
    final ActivityFactory factory = activityFactories.get(descriptionClass);
    return factory.getActivity(activityDescription, activityContext);
  }

  @SuppressWarnings("unchecked")
  protected void processBeans(final Iterable<Object> beans) {
    for(Object bean : beans) {
      ActivityFactoryFor annotation = getAnnotation(bean);
      activityFactories.put(annotation.value(), (ActivityFactory<? extends ActivityDescription>) bean);
    }
  }
  
}
