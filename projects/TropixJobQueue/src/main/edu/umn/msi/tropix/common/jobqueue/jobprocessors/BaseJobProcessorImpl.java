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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class BaseJobProcessorImpl<T extends JobDescription> implements BaseJobProcessor<T> {
  static final Log LOG = LogFactory.getLog(BaseJobProcessorImpl.class);

  private boolean useStaging = true;
  private Boolean completedNormally = null;
  private DisposableResourceTracker resourceTracker = null;
  private T jobDescription;
  private StagingDirectory stagingDirectory;

  public BaseJobProcessorImpl() {
    super();
  }

  protected boolean wasCompletedNormally() throws IllegalStateException {
    if(completedNormally == null) {
      throw new IllegalStateException("wasCompletedNormally called before postprocess()");
    }
    return completedNormally;
  }

  public final List<DisposableResource> getResults() {
    return resourceTracker.getResources();
  }

  public final void initialize(final T jobDescription) {
    this.jobDescription = jobDescription;
    initialize();
  }

  public void setStagingDirectory(final StagingDirectory stagingDirectory) {
    this.stagingDirectory = stagingDirectory;
  }

  public void setUseStaging(final boolean useStaging) {
    this.useStaging = useStaging;
  }

  protected void initialize() {
  }

  protected void doPostprocessing() {
  }

  protected void doPreprocessing() {
  }

  public final void postprocess(final boolean completedNormally) {
    LOG.trace("postprocess called -- job completed normally? " + completedNormally);
    this.completedNormally = completedNormally;
    try {
      doPostprocessing();
    } finally {
      if(useStaging) {
        try {
          LOG.trace("cleaning up staging directory");
          getStagingDirectory().cleanUp();
          LOG.trace("staging directory cleanup finished properly");
        } catch(final RuntimeException t) {
          ExceptionUtils.logQuietly(LOG, t, "Failed to cleanup staging directory for jobProcessor " + this + " ignoring this error.");
        }
      }
    }
  }

  public final T preprocess() {
    // stagingDirectory.setup() is called in BaseExecutableJobProcessorFactoryImpl...
    doPreprocessing();
    return jobDescription;
  }

  public void setDisposableResourceTracker(final DisposableResourceTracker disposableResourceTracker) {
    this.resourceTracker = disposableResourceTracker;
  }

  protected DisposableResourceTracker getResourceTracker() {
    return resourceTracker;
  }

  protected StagingDirectory getStagingDirectory() {
    return stagingDirectory;
  }

  protected void setJobDescription(final T jobDescription) {
    this.jobDescription = jobDescription;
  }

  protected T getJobDescription() {
    return jobDescription;
  }

}