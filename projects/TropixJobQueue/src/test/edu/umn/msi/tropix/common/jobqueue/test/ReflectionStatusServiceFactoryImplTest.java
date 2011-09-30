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

package edu.umn.msi.tropix.common.jobqueue.test;

import org.globus.gsi.GlobusCredential;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.impl.ReflectionStatusServiceFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class ReflectionStatusServiceFactoryImplTest {

  @Test(groups = "unit")
  public void getStatusService() {
    final ReflectionStatusServiceFactoryImpl factory = new ReflectionStatusServiceFactoryImpl();
    factory.setClassName("edu.umn.msi.tropix.common.jobqueue.test.ReflectionStatusServiceFactoryImplTest$StatusServiceImpl");
    final Credential proxy = Credentials.getMock();
    final StatusServiceImpl service = (StatusServiceImpl) factory.getStatusService("http://moo", proxy);
    assert service.proxy == null;
    assert service.address.equals("http://moo");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void badClass() {
    final ReflectionStatusServiceFactoryImpl factory = new ReflectionStatusServiceFactoryImpl();
    factory.setClassName("edu.umn.msi.tropix.common.jobqueue.test.ReflectionStatusServiceFactoryImplTest$StatusServiceImplM");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void badConstructor() {
    final ReflectionStatusServiceFactoryImpl factory = new ReflectionStatusServiceFactoryImpl();
    try {
      factory.setClassName("edu.umn.msi.tropix.common.jobqueue.test.ReflectionStatusServiceFactoryImplTest$WrongConstructorImpl");
    } catch(final Exception e) {
      assert false;
    }
    factory.getStatusService("http://a", Credentials.getMock());
  }

  public static class WrongConstructorImpl implements StatusService {
    public Status[] getStatuses(final Ticket[] tickets) {
      return null;
    }

  }

  public static class StatusServiceImpl implements StatusService {
    private GlobusCredential proxy;
    private String address;

    public StatusServiceImpl(final String address, final GlobusCredential proxy) {
      this.address = address;
      this.proxy = proxy;
    }

    public Status[] getStatuses(final Ticket[] tickets) {
      return null;
    }

  }

}
