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

package edu.umn.msi.tropix.common.jobqueue.jobprocessors;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

public class BaseJobProcessorFactoryImpl<T extends BaseJobProcessor<? extends JobDescription>> {
  private CredentialedStagingDirectoryFactory stagingDirectoryFactory = null;
  private Supplier<DisposableResourceTracker> disposableResourceTrackerSupplier = null;

  public void setCredentialedStagingDirectoryFactory(final CredentialedStagingDirectoryFactory stagingDirectoryFactory) {
    this.stagingDirectoryFactory = stagingDirectoryFactory;
  }

  public void setDisposableResourceTrackerSupplier(final Supplier<DisposableResourceTracker> disposableResourceTrackerSupplier) {
    this.disposableResourceTrackerSupplier = disposableResourceTrackerSupplier;
  }

  protected void initializeDisposableResourceTracker(final T instance) {
    instance.setDisposableResourceTracker(disposableResourceTrackerSupplier.get());
  }

  protected StagingDirectory getAndSetupStagingDirectory(final JobProcessorConfiguration config) {
    final Credential credential = config.getCredential();
    final StagingDirectory stagingDirectory = stagingDirectoryFactory.get(credential);
    stagingDirectory.setup();
    return stagingDirectory;
  }

  protected CredentialedStagingDirectoryFactory getStagingDirectoryFactory() {
    return stagingDirectoryFactory;
  }

  protected Supplier<DisposableResourceTracker> getDisposableResourceTrackerSupplier() {
    return disposableResourceTrackerSupplier;
  }

}