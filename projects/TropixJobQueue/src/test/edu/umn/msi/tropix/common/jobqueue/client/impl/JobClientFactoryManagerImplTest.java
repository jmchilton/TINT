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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryComponent;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/common/jobqueue/client/gridJobClientFactoryContext.xml")
public class JobClientFactoryManagerImplTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JobClientFactoryManager manager;

  @Test(groups = "unit")
  public void test() {
    assert AnnotationUtils.findAnnotation(new Component1().getClass(), JobClientFactoryComponent.class) != null;

    final JobClientFactory jobClientFactory = manager.getFactory("testcomponent1");
    assert jobClientFactory != null;
    assert jobClientFactory instanceof Component1;
  }

}
