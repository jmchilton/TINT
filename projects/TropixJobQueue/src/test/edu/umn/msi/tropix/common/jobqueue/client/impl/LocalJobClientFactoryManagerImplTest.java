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

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.impl.TestLocalJobClientFactoryManagerImpl.DummyJobClientFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.service.JobQueueContext;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;

@ContextConfiguration
public class LocalJobClientFactoryManagerImplTest extends AbstractTestNGSpringContextTests {

  @Inject
  private TestLocalJobClientFactoryManagerImpl manager;

  @Inject
  private TestJobQueueContextImpl1 context1;

  @Inject
  private TestJobQueueContextImpl2 context2;

  @Test(groups = "unit")
  public void testManagerSetupAndMappingProperly() {
    final DummyJobClientFactoryImpl factory1 = (DummyJobClientFactoryImpl) manager.getFactory("http://test1");
    assert factory1.getContextCreatedWith() == context1;

    final DummyJobClientFactoryImpl factory2 = (DummyJobClientFactoryImpl) manager.getFactory("http://test2");
    assert factory2.getContextCreatedWith() == context2;
  }

}

class TestLocalJobClientFactoryManagerImpl extends LocalJobClientFactoryManagerImpl {
  {
    super.setLocalJobClientFactoryFunction(new Function<JobQueueContext, JobClientFactory>() {

      public DummyJobClientFactoryImpl apply(final JobQueueContext context) {
        final DummyJobClientFactoryImpl factory =
            new DummyJobClientFactoryImpl();
        factory.fromContext = context;
        return factory;
      }

    });
  }

  static class DummyJobClientFactoryImpl implements JobClientFactory {
    private JobQueueContext fromContext;

    JobQueueContext getContextCreatedWith() {
      return fromContext;
    }

    public <T> T createJobContext(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
      return null;
    }

    public <T> T getJobClient(final Credential proxy, final String serviceUrl, final Ticket ticket, final Class<T> interfaceClass) {
      return null;
    }

    public <T> T getServiceClient(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
      return null;
    }

  }

}
