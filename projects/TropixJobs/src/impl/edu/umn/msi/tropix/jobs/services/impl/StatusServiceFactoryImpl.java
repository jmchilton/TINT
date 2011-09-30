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

import edu.umn.msi.tropix.common.jobqueue.client.StatusServiceFactory;
import edu.umn.msi.tropix.common.jobqueue.service.StatusService;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

public class StatusServiceFactoryImpl implements StatusServiceFactory {
  private static final Log LOG = LogFactory.getLog(StatusServiceFactoryImpl.class);
  private JobContextClientFactory jobContextClientFactory;

  public StatusService getStatusService(final String gridAddress, final Credential proxy) {
    LOG.trace("getStatusService called");
    return jobContextClientFactory.getServiceClient(proxy, gridAddress, StatusService.class);
  }

  public void setJobContextClientFactory(final JobContextClientFactory jobContextClientFactory) {
    this.jobContextClientFactory = jobContextClientFactory;
  }
}
