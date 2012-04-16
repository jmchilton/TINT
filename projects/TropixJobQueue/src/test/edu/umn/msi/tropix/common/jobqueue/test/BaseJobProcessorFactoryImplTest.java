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
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.util.concurrent.Semaphore;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

public class BaseJobProcessorFactoryImplTest {
  private BaseExecutableJobProcessor processor;
  private SimpleFactoryImpl factory;
  private Supplier<DisposableResourceTracker> disposableResourceTrackerSupplier;
  private CredentialedStagingDirectoryFactory sdFactory;
  private Credential proxy;
  private StagingDirectory directory;
  private Capture<JobDescriptionType> jobDescriptionReference;

  @JobType("test")
  class SimpleFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<BaseExecutableJobProcessor> {
    private BaseExecutableJobProcessor baseJobProcessor;

    protected BaseExecutableJobProcessor create() {
      return baseJobProcessor;
    }
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    processor = createMock(BaseExecutableJobProcessor.class);
    factory = new SimpleFactoryImpl();
    factory.baseJobProcessor = processor;
    disposableResourceTrackerSupplier = EasyMockUtils.createMockSupplier();
    sdFactory = createMock(CredentialedStagingDirectoryFactory.class);
    proxy = Credentials.getMock();
    directory = createMock(StagingDirectory.class);
    EasyMock.expect(directory.getSep()).andStubReturn("/");
    processor.setStagingDirectory(directory);

    factory.setCredentialedStagingDirectoryFactory(sdFactory);
    factory.setDisposableResourceTrackerSupplier(disposableResourceTrackerSupplier);

    final DisposableResourceTracker tracker = createMock(DisposableResourceTracker.class);
    expect(disposableResourceTrackerSupplier.get()).andReturn(tracker);

    processor.setDisposableResourceTracker(tracker);

  }

  protected void setUseStaging(final boolean useStaging) {
    // Set value in job processor factory
    factory.setUseStaging(true);
    // Set expectation in job processor
    processor.setUseStaging(true);
  }

  @Test(groups = "unit")
  public void initialize() {
    setUseStaging(true);
    final File file = new File("/tmp/1").getAbsoluteFile();
    expect(sdFactory.get(proxy, file.getAbsolutePath())).andReturn(directory);

    final JobDescriptionType jobDescription = new JobDescriptionType();
    final ExecutableJobDescription executableJobDescription = ExecutableJobDescriptions.forJobDescriptionType(jobDescription);
    JobDescriptionUtils.setStagingDirectory(jobDescription, file.getAbsolutePath());
    JobDescriptionUtils.setProxy(jobDescription, proxy);
    processor.initialize(executableJobDescription);

    replay(processor, sdFactory, directory, disposableResourceTrackerSupplier);
    assert factory.recover(executableJobDescription) == processor;
    verify(processor, sdFactory, directory, disposableResourceTrackerSupplier);
  }

  @Test(groups = "unit")
  public void testNewJobProcessorInitializesJobDescriptionCorrectly() {
    setUseStaging(false);

    createAndVerifyNewJobProcessor();

    final JobDescriptionType jobDescription = jobDescriptionReference.getValue();
    assert jobDescription.getExecutable().equals("/bin/ls");
    assert jobDescription.getDirectory().equals("/tmp");
    JobDescriptionUtils.getJobType(jobDescription).equals("test");
    JobDescriptionUtils.getStagingDirectory(jobDescription).equals("/tmp/stage1");
    assert jobDescription.getStdout().startsWith("/tmp/stage1/");
    assert jobDescription.getStderr().startsWith("/tmp/stage1/");
  }

  private void createAndVerifyNewJobProcessor() {
    expect(sdFactory.get(proxy)).andReturn(directory);
    factory.setApplicationPath("/bin/ls");
    factory.setWorkingDirectory("/tmp");

    jobDescriptionReference = EasyMockUtils.newCapture();
    processor.setJobDescription(EasyMock.capture(jobDescriptionReference));

    directory.setup();
    expect(directory.getAbsolutePath()).andReturn("/tmp/stage1").anyTimes();

    replay(processor, sdFactory, directory, disposableResourceTrackerSupplier);

    assert factory.create(JobProcessorConfigurationFactories.getInstance().get(proxy)) == processor;
    verify(processor, sdFactory, directory, disposableResourceTrackerSupplier);
  }

  @Test(groups = "unit")
  public void testUninitializedProcessingSemaphore() {
    setUseStaging(false);
    factory.setMaxConcurrentProcessingJobs("${should.ignore.this}");
    createAndVerifyNewJobProcessor();
  }

  @Test(groups = "unit")
  public void testProcessingSemaphore() {
    setUseStaging(false);
    factory.setMaxConcurrentProcessingJobs("9");
    final Capture<Semaphore> semaphoreCapture = EasyMockUtils.newCapture();
    processor.setProcessingSemaphore(EasyMock.capture(semaphoreCapture));
    createAndVerifyNewJobProcessor();
    assert semaphoreCapture.hasCaptured();
    assert semaphoreCapture.getValue().availablePermits() == 9;
  }

}
