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

package edu.umn.msi.tropix.common.jobqueue.configuration;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.grid.credentials.Credential;

class JobProcessorConfigurationImpl implements JobProcessorConfiguration {
  private final Credential proxy;

  public JobProcessorConfigurationImpl(@Nullable final Credential proxy) {
    this.proxy = proxy;
  }

  @Nullable
  public Credential getCredential() {
    return proxy;
  }

  @Override
  public int hashCode() {
    return 17 + (proxy == null ? 0 : proxy.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    boolean equal = false;
    if((obj instanceof JobProcessorConfigurationImpl)) {
      final JobProcessorConfiguration otherConfiguration = (JobProcessorConfiguration) obj;
      equal = proxy == null ? otherConfiguration.getCredential() == null : proxy.equals(otherConfiguration.getCredential());
    }
    return equal;
  }

}
