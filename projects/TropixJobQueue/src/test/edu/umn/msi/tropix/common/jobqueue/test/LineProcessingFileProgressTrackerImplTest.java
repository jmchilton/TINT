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

package edu.umn.msi.tropix.common.jobqueue.test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.LineProcessor;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackingLineCallback;
import edu.umn.msi.tropix.common.jobqueue.progress.impl.LineProcessingFileProgressTrackerSupplierImpl;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class LineProcessingFileProgressTrackerImplTest {
  private final IOUtils ioUtils = IOUtilsFactory.getInstance();
  private LineProcessingFileProgressTrackerSupplierImpl trackerSupplier;
  private LineProcessingFileProgressTracker tracker;
  private ProgressTrackingLineCallback lineCallback;
  private LineProcessor<Reader> lineProcessor;
  private File file;
  private ProgressTrackerCallback ptCallback;

  private void replay() {
    EasyMock.replay(lineCallback, lineProcessor);
  }

  private void verify() {
    EasyMock.verify(lineCallback, lineProcessor);
  }

  @BeforeMethod(groups = "unit")
  public void init() throws IOException {
    trackerSupplier = new LineProcessingFileProgressTrackerSupplierImpl();
    final Supplier<LineProcessor<Reader>> lineProcessorSupplier = EasyMockUtils.createMockSupplier();
    trackerSupplier.setLineProcessorSupplier(lineProcessorSupplier);
    lineProcessor = EasyMock.createMock(LineProcessor.class);
    lineProcessorSupplier.get();
    EasyMock.expectLastCall().andReturn(lineProcessor);
    EasyMock.replay(lineProcessorSupplier);
    tracker = trackerSupplier.get();
    EasyMock.verify(lineProcessorSupplier);
    lineCallback = EasyMock.createMock(ProgressTrackingLineCallback.class);
    ptCallback = EasyMock.createMock(ProgressTrackerCallback.class);
    tracker.setLineCallback(lineCallback);
    file = File.createTempFile("tpx", "tst");
    tracker.setTrackedFile(file);
  }

  @AfterMethod(groups = "unit")
  public void clean() {
    file.delete();
  }

  @Test(groups = "unit")
  public void stop() {
    lineProcessor.stop();
    replay();
    tracker.stop();
    verify();
  }

  @Test(groups = "unit", expectedExceptions = NullPointerException.class)
  public void exception() {
    lineCallback.setProgressTrackerCallback(ptCallback);
    lineProcessor.processLines(EasyMock.isA(Reader.class), EasyMock.same(lineCallback));
    EasyMock.expectLastCall().andThrow(new NullPointerException());
    replay();
    tracker.start(ptCallback);
    verify();
  }

  @Test(groups = "unit")
  public void callback() throws IOException {
    FileUtils.writeStringToFile(file, "Hello");
    lineCallback.setProgressTrackerCallback(ptCallback);
    lineProcessor.processLines(EasyMockUtils.record(new EasyMockUtils.Recorder<Reader>() {
      public void record(final Reader reader) {
        assert ioUtils.toString(reader).equals("Hello");
      }
    }), EasyMock.same(lineCallback));
    replay();
    tracker.start(ptCallback);
    verify();
  }

}
