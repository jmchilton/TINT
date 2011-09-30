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

package edu.umn.msi.tropix.jobs.activities.factories;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.TropixObjectDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Revertable;
import edu.umn.msi.tropix.models.TropixObject;

abstract class BaseTropixObjectJobActivityImpl<D extends TropixObjectDescription, T extends TropixObject> extends BaseActivityImpl<D> implements Revertable {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private final FactorySupport factorySupport;
  
  protected BaseTropixObjectJobActivityImpl(final D activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport) {
    super(activityDescription, activityContext);
    this.factorySupport = factorySupport;
  }

  public void rollback() {
    final String objectId = getDescription().getObjectId();
    if(objectId != null) {
      factorySupport.getTropixObjectDeleter().delete(getUserId(), objectId);
    }
  }

  protected String getDestinationId() {
    return getDescription().getDestinationId();
  }
  
  @SuppressWarnings("unchecked")
  protected T getModelObject() {
    final Type superclass = getClass().getGenericSuperclass();
    if (superclass instanceof Class) {
      throw new RuntimeException("Unknown TropixObject type parameter");
    }
    final ParameterizedType parameterized = (ParameterizedType) superclass;
    final Class<T> clazz = (Class<T>) parameterized.getActualTypeArguments()[1];
    final T modelObject = REFLECTION_HELPER.newInstance(clazz);
    getDescription().init(modelObject);
    return modelObject;
  }
  
  protected void updateId(final T object) {
    final String objectId = object.getId();
    Preconditions.checkNotNull(objectId);
    getDescription().setObjectId(objectId);
    // If the object was committed, then fire an event indicating it should
    // now be available.
    if(getDescription().getCommitted()) {
      factorySupport.getEventSupport().objectAdded(getEventBase(), objectId, getDescription().getDestinationId());
    }
  }
     
}
