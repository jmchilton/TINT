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

package edu.umn.msi.tropix.jobs.activities.impl.delegatetest;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.jobs.activities.impl.DelegatingActivityFactoryImpl;

class TestActivityDescription1 extends ActivityDescription {

}

class TestActivityDescription2 extends ActivityDescription {

}

class TestActivity implements Activity {
  private Class<?> creationClass;

  TestActivity(final Class<?> clazz) {
    this.creationClass = clazz;
  }

  public void run() throws ShutdownException {

  }

  Class<?> getCreationClass() {
    return creationClass;
  }

}

class TestActivityFactory<T extends ActivityDescription> implements ActivityFactory<T> {

  public Activity getActivity(final ActivityDescription activityDescription, final ActivityContext activityContext) {
    return new TestActivity(getClass());
  }

}

@ManagedBean
@ActivityFactoryFor(TestActivityDescription1.class)
class TestActivityFactory1 extends TestActivityFactory<TestActivityDescription1> {

}

class TestActivityFactory2Parent<T extends ActivityDescription> extends TestActivityFactory<T> {

}

@ManagedBean
@ActivityFactoryFor(TestActivityDescription2.class)
class TestActivityFactory2 extends TestActivityFactory2Parent<TestActivityDescription2> {

}

@ContextConfiguration
public class DelegatingActivityFactoryImplTest extends AbstractTestNGSpringContextTests {
  @Inject
  private DelegatingActivityFactoryImpl factory;

  @Test(groups = "unit", timeOut = 1000)
  public void delegationTest() {
    final TestActivityDescription1 d1 = new TestActivityDescription1();
    final TestActivityDescription2 d2 = new TestActivityDescription2();
    final TestActivity a1 = (TestActivity) factory.getActivity(d1, null);
    assert TestActivityFactory1.class.isAssignableFrom(a1.getCreationClass());
    final TestActivity a2 = (TestActivity) factory.getActivity(d2, null);
    assert TestActivityFactory2.class.isAssignableFrom(a2.getCreationClass());
  }

}
