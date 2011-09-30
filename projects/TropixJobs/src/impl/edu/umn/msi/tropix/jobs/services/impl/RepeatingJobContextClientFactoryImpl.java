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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Callable;
import edu.umn.msi.tropix.common.concurrent.Repeater;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

// TODO: Move into JobQueue package...
public class RepeatingJobContextClientFactoryImpl implements JobContextClientFactory {
  private static final Log LOG = LogFactory.getLog(RepeatingJobContextClientFactoryImpl.class);
  private JobContextClientFactory baseFactory;
  private Supplier<Repeater<Runnable>> repeaterSupplier;

  private <S> S run(final Callable<S> callable, final String url) {
    class InternalRunnable implements Runnable {
      private S result;

      public void run() {
        try {
          result = callable.call();
        } catch(final RuntimeException e) {
          ExceptionUtils.logQuietly(LOG, e);
          throw e;
        }
      }

      public String toString() {
        return url;
      }
    }
    final Repeater<Runnable> repeater = repeaterSupplier.get();
    final InternalRunnable iRunnable = new InternalRunnable();
    repeater.setBaseRunnable(iRunnable);
    repeater.run();
    return iRunnable.result;
  }

  public <T> T createJobContext(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
    return run(new Callable<T>() {
      public T call() {
        return baseFactory.createJobContext(proxy, serviceUrl, interfaceClass);
      }
    }, serviceUrl);
  }

  public <T> T getJobClient(final Credential proxy, final String serviceUrl, final String ticket, final Class<T> interfaceClass) {
    return run(new Callable<T>() {
      public T call() {
        return baseFactory.getJobClient(proxy, serviceUrl, ticket, interfaceClass);
      }
    }, serviceUrl);
  }

  public <T> T getServiceClient(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
    return run(new Callable<T>() {
      public T call() {
        return baseFactory.getServiceClient(proxy, serviceUrl, interfaceClass);
      }
    }, serviceUrl);
  }

  public void setBaseFactory(final JobContextClientFactory baseFactory) {
    this.baseFactory = baseFactory;
  }

  public void setRepeaterSupplier(final Supplier<Repeater<Runnable>> repeaterSupplier) {
    this.repeaterSupplier = repeaterSupplier;
  }

}
