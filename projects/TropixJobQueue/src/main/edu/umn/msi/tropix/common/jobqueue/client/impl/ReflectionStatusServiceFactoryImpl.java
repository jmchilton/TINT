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

import java.lang.reflect.Constructor;

import org.globus.gsi.GlobusCredential;

import edu.umn.msi.tropix.common.jobqueue.client.StatusServiceFactory;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class ReflectionStatusServiceFactoryImpl implements StatusServiceFactory {
  private Class<? extends StatusService> clazz;

  public StatusService getStatusService(final String gridAddress, final Credential proxy) {
    try {
      final Constructor<? extends StatusService> constructor = clazz.getConstructor(String.class, GlobusCredential.class);
      return constructor.newInstance(gridAddress, proxy.getGlobusCredential());
    } catch(final Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public void setClassName(final String string) {
    try {
      setClass((Class<? extends StatusService>) Class.forName(string));
    } catch(final ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public void setClass(final Class<? extends StatusService> clazz) {
    this.clazz = clazz;
  }

}
