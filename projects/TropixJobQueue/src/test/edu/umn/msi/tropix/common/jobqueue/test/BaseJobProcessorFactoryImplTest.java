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

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
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
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

public class BaseJobProcessorFactoryImplTest {
  @JobType("test")
  class SimpleFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<BaseExecutableJobProcessor> {
    private BaseExecutableJobProcessor baseJobProcessor;

    protected BaseExecutableJobProcessor create() {
      return baseJobProcessor;
    }
  }

  @Test(groups = "unit")
  public void initialize() {
    final BaseExecutableJobProcessor processor = createMock(BaseExecutableJobProcessor.class);
    final Supplier<DisposableResourceTracker> disposableResourceTrackerSupplier = EasyMockUtils.createMockSupplier();
    final CredentialedStagingDirectoryFactory sdFactory = createMock(CredentialedStagingDirectoryFactory.class);
    final Credential proxy = Credentials.getMock();
    final StagingDirectory directory = createMock(StagingDirectory.class);

    final SimpleFactoryImpl factory = new SimpleFactoryImpl();
    factory.baseJobProcessor = processor;

    factory.setCredentialedStagingDirectoryFactory(sdFactory);
    factory.setDisposableResourceTrackerSupplier(disposableResourceTrackerSupplier);
    factory.setUseStaging(true);

    final DisposableResourceTracker tracker = createMock(DisposableResourceTracker.class);
    expect(disposableResourceTrackerSupplier.get()).andReturn(tracker);

    processor.setDisposableResourceTracker(tracker);
    processor.setUseStaging(true);
    final File file = new File("/tmp/1").getAbsoluteFile();
    expect(sdFactory.get(proxy, file.getAbsolutePath())).andReturn(directory);

    final JobDescriptionType jobDescription = new JobDescriptionType();
    final ExecutableJobDescription executableJobDescription = ExecutableJobDescriptions.forJobDescriptionType(jobDescription);
    JobDescriptionUtils.setStagingDirectory(jobDescription, file.getAbsolutePath());
    JobDescriptionUtils.setProxy(jobDescription, proxy);
    processor.setStagingDirectory(directory);
    processor.initialize(executableJobDescription);

    replay(processor, sdFactory, directory, disposableResourceTrackerSupplier);
    assert factory.recover(executableJobDescription) == processor;
    verify(processor, sdFactory, directory, disposableResourceTrackerSupplier);
  }

  @Test(groups = "unit")
  public void get() {
    final BaseExecutableJobProcessor processor = createMock(BaseExecutableJobProcessor.class);
    final Supplier<DisposableResourceTracker> disposableResourceTrackerSupplier = EasyMockUtils.createMockSupplier();
    final CredentialedStagingDirectoryFactory sdFactory = createMock(CredentialedStagingDirectoryFactory.class);
    final StagingDirectory directory = createMock(StagingDirectory.class);
    EasyMock.expect(directory.getSep()).andStubReturn("/");
    final Credential proxy = Credentials.getMock();
    expect(sdFactory.get(proxy)).andReturn(directory);

    final SimpleFactoryImpl factory = new SimpleFactoryImpl();
    factory.baseJobProcessor = processor;

    factory.setCredentialedStagingDirectoryFactory(sdFactory);
    factory.setDisposableResourceTrackerSupplier(disposableResourceTrackerSupplier);
    factory.setUseStaging(false);

    factory.setApplicationPath("/bin/ls");
    factory.setWorkingDirectory("/tmp");

    final DisposableResourceTracker tracker = createMock(DisposableResourceTracker.class);
    expect(disposableResourceTrackerSupplier.get()).andReturn(tracker);

    processor.setDisposableResourceTracker(tracker);
    processor.setUseStaging(false);
    processor.setStagingDirectory(directory);

    final Reference<JobDescriptionType> jobDescriptionReference = EasyMockUtils.newReference();
    processor.setJobDescription(EasyMockUtils.record(jobDescriptionReference));

    directory.setup();
    expect(directory.getAbsolutePath()).andReturn("/tmp/stage1").anyTimes();

    replay(processor, sdFactory, directory, disposableResourceTrackerSupplier);

    assert factory.create(JobProcessorConfigurationFactories.getInstance().get(proxy)) == processor;
    verify(processor, sdFactory, directory, disposableResourceTrackerSupplier);

    final JobDescriptionType jobDescription = jobDescriptionReference.get();
    assert jobDescription.getExecutable().equals("/bin/ls");
    assert jobDescription.getDirectory().equals("/tmp");
    JobDescriptionUtils.getJobType(jobDescription).equals("test");
    JobDescriptionUtils.getStagingDirectory(jobDescription).equals("/tmp/stage1");
    assert jobDescription.getStdout().startsWith("/tmp/stage1/");
    assert jobDescription.getStderr().startsWith("/tmp/stage1/");
  }

}
