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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryComponent;
import edu.umn.msi.tropix.common.jobqueue.ticket.Ticket;
import edu.umn.msi.tropix.grid.credentials.Credential;

@JobClientFactoryComponent(serviceName = "testcomponent1", servicePackage = "moo.cow")
@Component @Scope(BeanDefinition.SCOPE_SINGLETON)
public class Component1 implements JobClientFactory {

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
