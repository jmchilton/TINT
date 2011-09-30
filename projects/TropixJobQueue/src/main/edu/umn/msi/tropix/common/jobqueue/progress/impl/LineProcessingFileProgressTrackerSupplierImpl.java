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

package edu.umn.msi.tropix.common.jobqueue.progress.impl;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.LineProcessor;
import edu.umn.msi.tropix.common.io.LineProcessors;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackingLineCallback;

public class LineProcessingFileProgressTrackerSupplierImpl implements Supplier<LineProcessingFileProgressTracker> {
  private Supplier<LineProcessor<Reader>> lineProcessorSupplier = LineProcessors.getDefaultSupplier();

  public void setLineProcessorSupplier(final Supplier<LineProcessor<Reader>> lineProcessorSupplier) {
    this.lineProcessorSupplier = lineProcessorSupplier;
  }

  public LineProcessingFileProgressTracker get() {
    final LineProcessingFileProgressTrackerImpl tracker = new LineProcessingFileProgressTrackerImpl();
    tracker.setLineProcessor(lineProcessorSupplier.get());
    return tracker;
  }

  class LineProcessingFileProgressTrackerImpl implements LineProcessingFileProgressTracker {
    private final FileUtils fileUtils = FileUtilsFactory.getInstance();
    private LineProcessor<Reader> lineProcessor;
    private File trackedFile;
    private ProgressTrackingLineCallback lineCallback;

    public void start(final ProgressTrackerCallback callback) {
      final FileReader reader = fileUtils.getFileReader(trackedFile);
      lineCallback.setProgressTrackerCallback(callback);
      try {
        lineProcessor.processLines(reader, lineCallback);
      } finally {
        IOUtils.closeQuietly(reader);
      }
    }

    public void stop() {
      lineProcessor.stop();
    }

    public void setLineProcessor(final LineProcessor<Reader> lineProcessor) {
      this.lineProcessor = lineProcessor;
    }

    public void setTrackedFile(final File trackedFile) {
      this.trackedFile = trackedFile;
    }

    public void setLineCallback(final ProgressTrackingLineCallback lineCallback) {
      this.lineCallback = lineCallback;
    }
  }
}
