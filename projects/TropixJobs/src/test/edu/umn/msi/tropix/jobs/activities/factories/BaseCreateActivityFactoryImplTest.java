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
import java.util.UUID;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.TropixObjectDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.Revertable;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.models.TropixObject;

class BaseCreateActivityFactoryImplTest<D extends TropixObjectDescription, T extends TropixObject> {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private String objectId;
  private Capture<T> capturedObject;
  private D description;
  private ActivityContext context;
  private MockFactorySupportImpl factorySupport;
  
  protected D getDescription() {
    return description;
  }
  
  protected ActivityContext getContext() {
    return context;
  }
  
  protected Credential matchCredential() {
    return EasyMock.isA(Credential.class);
  }
  
  protected String matchId() {
    return EasyMock.eq(context.getCredential().getIdentity());
  }

  protected String matchDestinationId() {
    return EasyMock.eq(description.getDestinationId());
  }
  
  protected void runAndVerify(final ActivityFactory<D> activityFactory) {
    final Capture<EventBase> eventBaseCapture = EasyMockUtils.newCapture();
    if(description.getCommitted()) {
      factorySupport.getEventSupport().objectAdded(EasyMock.capture(eventBaseCapture), EasyMock.eq(objectId), EasyMock.eq(description.getDestinationId()));
    }
    factorySupport.replay();
    final Activity activity = activityFactory.getActivity(description, context);
    activity.run();    
    factorySupport.verifyAndReset();
    if(description.getCommitted()) {
      final EventBase eventBase = eventBaseCapture.getValue();
      assert description.getJobDescription().getName().equals(eventBase.getJobName()) : "Expecting " + description.getJobDescription().getName() + " got " + eventBase.getJobName();
      assert description.getJobDescription().getId().equals(eventBase.getJobId());
      assert context.getId().equals(eventBase.getWorkflowId());
      assert context.getCredential().getIdentity().equals(eventBase.getUserId());
    }
    TestUtils.verifyCommonMetadata(description, getCapturedObject());
    verifyDescriptionId(description);
    ((Revertable) activity).rollback();
    factorySupport.assertDeleted(description.getObjectId());
  }
  
  @BeforeMethod(groups = "unit")
  protected void init() {
    @SuppressWarnings("unchecked")
    final Class<D> clazz = (Class<D>) getTypeArgument(0); 
    description = TestUtils.init(REFLECTION_HELPER.newInstance(clazz));
    context = TestUtils.getContext();
    factorySupport = new MockFactorySupportImpl();
    objectId = UUID.randomUUID().toString();
  }
  
  protected MockFactorySupportImpl getFactorySupport() {
    return factorySupport;
  }

  private Class<?> getTypeArgument(final int typeArgumentIndex) {
    final ParameterizedType parameterized = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<?>) parameterized.getActualTypeArguments()[typeArgumentIndex];
  }
  
  protected void returnInitializedObject() {
    @SuppressWarnings("unchecked")
    final Class<T> clazz = (Class<T>) getTypeArgument(1); 
    final T modelObject = REFLECTION_HELPER.newInstance(clazz);
    modelObject.setId(objectId);
    initReturnedObject(modelObject);
    EasyMock.expectLastCall().andReturn(modelObject);
  }
  
  protected void initReturnedObject(final T returnedObject) {    
  }

  protected void verifyDescriptionId(final TropixObjectDescription tropixObjectDescription) {
    assert tropixObjectDescription.getObjectId().equals(objectId);
  }

  protected T captureObject() {
    capturedObject = EasyMockUtils.newCapture();
    return EasyMock.capture(capturedObject);
  }

  protected T getCapturedObject() {
    return capturedObject.getValue();
  }

}
