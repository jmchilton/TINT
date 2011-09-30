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

import java.util.Map;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;

/**
 * Delegates to various other implementations of {@link JobClientFactoryManager}
 * depending on the specified serviceAddress.
 * 
 * @author John Chilton
 *
 */
public class DelegatingJobClientFactoryManagerImpl implements JobClientFactoryManager {
  private Map<String, JobClientFactoryManager> jobClientFactoryManagerMap;
  
  public JobClientFactory getFactory(final String serviceAddress) {
    Preconditions.checkArgument(serviceAddress.indexOf(":") > 0, "Invalid serviceAddress " + serviceAddress);
    final String prefix = serviceAddress.substring(0, serviceAddress.indexOf(":"));
    return jobClientFactoryManagerMap.get(prefix).getFactory(serviceAddress);
  }

  public void setJobClientFactoryManagerMap(final Map<String, JobClientFactoryManager> jobClientFactoryManagerMap) {
    this.jobClientFactoryManagerMap = jobClientFactoryManagerMap;
  }
  
}
