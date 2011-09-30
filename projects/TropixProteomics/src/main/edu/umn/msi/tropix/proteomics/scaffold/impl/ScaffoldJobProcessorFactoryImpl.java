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

package edu.umn.msi.tropix.proteomics.scaffold.impl;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTrackers;
import edu.umn.msi.tropix.proteomics.scaffold.ScaffoldJobBuilder;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;

@JobType("Scaffold")
public class ScaffoldJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<ScaffoldJobProcessorImpl> implements ScaffoldJobBuilder {
  private String keyPath = null;
  private boolean quietMode = false;
  private Supplier<LineProcessingFileProgressTracker> progressTrackerSupplier = LineProcessingFileProgressTrackers.getDefaultSupplier();

  protected ScaffoldJobProcessorImpl create() {
    final ScaffoldJobProcessorImpl processor = new ScaffoldJobProcessorImpl();
    processor.setKeyPath(keyPath);
    processor.setQuiteMode(quietMode);
    processor.setFileProgressTracker(progressTrackerSupplier.get());
    return processor;
  }

  public ScaffoldJobProcessorImpl createScaffoldJob(final JobProcessorConfiguration config, final Scaffold scaffoldInput, final Iterable<InputContext> downloadContexts) {
    final ScaffoldJobProcessorImpl processor = create(config);
    processor.setDownloadContexts(downloadContexts);
    processor.setScaffoldInput(scaffoldInput);
    return processor;
  }

  public void setKeyPath(final String keyPath) {
    this.keyPath = keyPath;
  }

  public void setQuietMode(final boolean quietMode) {
    this.quietMode = quietMode;
  }

  public void setProgressTrackerSupplier(final Supplier<LineProcessingFileProgressTracker> progressTrackerSupplier) {
    this.progressTrackerSupplier = progressTrackerSupplier;
  }
}
