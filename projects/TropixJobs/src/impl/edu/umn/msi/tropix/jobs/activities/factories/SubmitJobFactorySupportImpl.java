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

package edu.umn.msi.tropix.jobs.activities.factories;

import javax.annotation.ManagedBean;
import javax.inject.Inject;


import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceFactory;
import edu.umn.msi.tropix.jobs.services.JobContextClientFactory;

@ManagedBean
public class SubmitJobFactorySupportImpl implements SubmitJobFactorySupport {
  private final CredentialResourceFactory dcFactory;
  private final JobContextClientFactory jobFactory;
  
  @Inject 
  SubmitJobFactorySupportImpl(final CredentialResourceFactory dcFactory, final JobContextClientFactory jobFactory) {
    this.dcFactory = dcFactory;
    this.jobFactory = jobFactory;
  }
  
  public CredentialResource createCredentialResource(final Credential credential) {
    return dcFactory.createDelegatedCredential(credential);
  }

  public <T> T createJobContext(final Credential proxy, final String serviceUrl, final Class<T> interfaceClass) {
    return jobFactory.createJobContext(proxy, serviceUrl, interfaceClass);
  }

}
