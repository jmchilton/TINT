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

package edu.umn.msi.tropix.jobs.services.impl;

import org.easymock.EasyMock;
import org.globus.gsi.GlobusCredentialException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Repeater;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

public class RepeatingJobContextClientFactoryTest {
  private JobContextClientFactory factory = null;
  private Credential proxy = null;
  private final String url = "http://moo";
  private RepeatingJobContextClientFactoryImpl repeatingFactory;
  private Checker checker;

  private static class Checker {
    private boolean checked = false;
  }

  @BeforeMethod(groups = "unit")
  public void init() throws GlobusCredentialException {
    factory = EasyMock.createMock(JobContextClientFactory.class);
    proxy = Credentials.getMock();
    repeatingFactory = new RepeatingJobContextClientFactoryImpl();
    repeatingFactory.setBaseFactory(factory);
    checker = new Checker();
    repeatingFactory.setRepeaterSupplier(new Supplier<Repeater<Runnable>>() {
      public Repeater<Runnable> get() {
        return new Repeater<Runnable>() {
          private Runnable runnable;

          public void setBaseRunnable(final Runnable runnable) {
            this.runnable = runnable;
          }

          public void run() {
            checker.checked = true;
            runnable.toString(); // Make sure it doesn't throw exception
            runnable.run();
          }
        };
      }
    });
  }

  @Test(groups = "unit", timeOut = 1000)
  public void serviceClient() {
    EasyMock.expect(factory.getServiceClient(proxy, url, String.class)).andReturn("Hello World!");
    EasyMock.replay(factory);
    assert repeatingFactory.getServiceClient(proxy, url, String.class).equals("Hello World!");
    assert checker.checked;
    EasyMock.verify(factory);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void createJobContext() {
    EasyMock.expect(factory.createJobContext(proxy, url, String.class)).andReturn("cow");
    EasyMock.replay(factory);
    assert repeatingFactory.createJobContext(proxy, url, String.class) == "cow";
    assert checker.checked;
    EasyMock.verify(factory);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void getJobClient() {
    EasyMock.expect(factory.getJobClient(proxy, url, "123", String.class)).andReturn("hello");
    EasyMock.replay(factory);
    assert repeatingFactory.getJobClient(proxy, url, "123", String.class).equals("hello");
    assert checker.checked;
    EasyMock.verify(factory);
  }

}
