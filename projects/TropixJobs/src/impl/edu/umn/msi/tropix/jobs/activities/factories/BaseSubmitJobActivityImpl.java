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

import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitJobDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.transfer.types.TransferResource;

abstract class BaseSubmitJobActivityImpl<T extends SubmitJobDescription> extends BaseActivityImpl<T> implements Activity {
  private final FactorySupport factorySupport;
  private final SubmitJobFactorySupport submitJobFactorySupport;

  protected BaseSubmitJobActivityImpl(final T activityDescription, final ActivityContext activityContext, final FactorySupport factorySupport, final SubmitJobFactorySupport submitJobFactorySupport) {
    super(activityDescription, activityContext);
    this.factorySupport = factorySupport;
    this.submitJobFactorySupport = submitJobFactorySupport;
  }

  protected TransferResource getDownloadResource(final String tropixFileId) {
    return factorySupport.getStorageDataFactory().getPersistedStorageData(tropixFileId, getCredential()).prepareDownloadResource();      
  }
  
  protected CredentialResource getDelegatedCredential() {
    return submitJobFactorySupport.createCredentialResource(getCredential());
  }
  
  protected <S> S createContext(final Class<S> interfaceClass) {
    return submitJobFactorySupport.createJobContext(getCredential(), getDescription().getServiceUrl(), interfaceClass);
  }
  
}
