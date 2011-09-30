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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class BaseJobProcessorImplTest {

  @Test(groups = "unit")
  public void getResources() {
    final BaseExecutableJobProcessorImpl processor = new BaseExecutableJobProcessorImpl();
    final DisposableResourceTracker mockTracker = createMock(DisposableResourceTracker.class);
    final List<DisposableResource> resources = Arrays.asList();
    processor.setDisposableResourceTracker(mockTracker);
    expect(mockTracker.getResources()).andReturn(resources);
    replay(mockTracker);
    assert processor.getResults() == resources;
    verify(mockTracker);
  }

  @Test(groups = "unit")
  public void postprocessingBranches() {
    postProcessingException(true, true);
    postProcessingException(true, false);
    postProcessingException(false, true);
    postProcessingException(false, false);
  }

  public void postProcessingException(final boolean stageException, final boolean postprocessingException) {
    final BaseExecutableJobProcessorImpl processor = new BaseExecutableJobProcessorImpl() {
      @Override
      public void doPostprocessing() {
        if(postprocessingException) {
          throw new IllegalStateException();
        }
      }
    };
    final StagingDirectory stagingDirectory = createMock(StagingDirectory.class);
    processor.setStagingDirectory(stagingDirectory);
    stagingDirectory.cleanUp();
    if(stageException) {
      expectLastCall().andThrow(new NullPointerException());
    }
    replay(stagingDirectory);
    Exception exception = null;
    try {
      processor.postprocess(true);
    } catch(final Exception e) {
      exception = e;
    }
    if(postprocessingException) {
      assert exception != null;
    } else {
      assert exception == null;
    }
    verify(stagingDirectory);
  }

  @Test(groups = "unit")
  public void setupStaging() {
    // By default staging is used
    BaseExecutableJobProcessorImpl processor = new BaseExecutableJobProcessorImpl();
    final StagingDirectory stagingDirectory = createMock(StagingDirectory.class);
    processor.setStagingDirectory(stagingDirectory);
    replay(stagingDirectory);
    processor.preprocess();
    EasyMockUtils.verifyAndReset(stagingDirectory);
    stagingDirectory.cleanUp();
    replay(stagingDirectory);
    processor.postprocess(false);
    EasyMockUtils.verifyAndReset(stagingDirectory);

    // If setUseStaging is called with true, staging is used
    processor = new BaseExecutableJobProcessorImpl();
    processor.setStagingDirectory(stagingDirectory);
    processor.setUseStaging(true);
    replay(stagingDirectory);
    processor.preprocess();
    EasyMockUtils.verifyAndReset(stagingDirectory);
    stagingDirectory.cleanUp();
    expectLastCall().andThrow(new IllegalStateException());
    replay(stagingDirectory);
    processor.postprocess(false);
    EasyMockUtils.verifyAndReset(stagingDirectory);

    processor = new BaseExecutableJobProcessorImpl();
    processor.setStagingDirectory(stagingDirectory);
    processor.setUseStaging(false);
    replay(stagingDirectory);
    processor.preprocess();
    EasyMockUtils.verifyAndReset(stagingDirectory);
    replay(stagingDirectory);
    processor.postprocess(true);
    EasyMockUtils.verifyAndReset(stagingDirectory);
  }

  @Test(groups = "unit")
  public void parameters() {
    class ParameterProcessor extends BaseExecutableJobProcessorImpl {
      @Override
      public void doPostprocessing() {
        assert getParameter("hello").equals("world");
      }

      @Override
      public void doPreprocessing() {
        saveParameter("hello", "world");
      }
    }
    

    final BaseExecutableJobProcessorImpl processor = new ParameterProcessor();
    // Calling preprocess sets a parameter...
    final JobDescriptionType jobDescription = new JobDescriptionType();
    processor.setJobDescription(jobDescription);
    processor.setUseStaging(false);
    final ExecutableJobDescription eJobDescription = processor.preprocess();

    // Verify the parameter is accessible in postprocess on same object...
    processor.postprocess(true);

    // Verify a new object initialized with the same jobDescription object can
    // access the parameter
    final BaseExecutableJobProcessorImpl newProcessor = new ParameterProcessor();
    newProcessor.initialize(eJobDescription);
    newProcessor.setUseStaging(false);
    newProcessor.postprocess(false);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void exception() {
    class ExceptionProcessor extends BaseExecutableJobProcessorImpl {
      public void expectException() {
        // Expect exception because hasn't completed yet.
        super.wasCompletedNormally();
      }
    }
    
    final ExceptionProcessor processor = new ExceptionProcessor();
    processor.expectException();
  }
}
