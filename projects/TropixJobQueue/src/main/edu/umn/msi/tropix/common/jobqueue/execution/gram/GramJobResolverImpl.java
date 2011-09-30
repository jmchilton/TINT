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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class GramJobResolverImpl implements GramJobResolver {
  private GramJobFactory gramJobFactory;

  public GramJob getGramJob(final GramExecutionJob gramExecutionJob) {
    final String handle = gramExecutionJob.getHandle();
    final String proxyStr = gramExecutionJob.getProxy();
    final Credential proxy = Credentials.fromString(proxyStr);
    final GramJob gramJob = gramJobFactory.getGramJob();
    gramJob.setCredentials(proxy);
    gramJob.setHandle(handle);
    return gramJob;
  }

  public void setGramJobFactory(final GramJobFactory gramJobFactory) {
    this.gramJobFactory = gramJobFactory;
  }
}
