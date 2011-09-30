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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseJobProcessorImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

public class JobProcessorFactoryTest<T extends BaseJobProcessorFactoryImpl<? extends BaseJobProcessorImpl<? extends JobDescription>>> {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private final Map<String, File> savedFiles = Maps.newHashMap();
  private File directory;
  private String path = "/tmp/fakepath";
  private String sep = "/";
  private final Credential credential = Credentials.getMock();
  private CredentialedStagingDirectoryFactory stagingDirectoryFactory;
  private StagingDirectory stagingDirectory;
  private DisposableResourceTracker tracker;
  private MockObjectCollection mockObjects;
  private T factory;

  public JobProcessorFactoryTest() {
    super();
  }

  protected String getPath() {
    return path;
  }

  protected String getRelPathTo(final String... pieces) {
    return Joiner.on(sep).join(Arrays.asList(pieces));
  }

  protected String getPathTo(final String... pieces) {
    return path + sep + getRelPathTo(pieces);
  }

  protected void init(final boolean useRealDirectory) {
    if(useRealDirectory) {
      directory = FILE_UTILS.createTempDirectory();
      path = directory.getAbsolutePath();
      sep = File.separator;
    }
    doInit();
  }

  @AfterMethod(groups = "unit")
  public void cleanUp() {
    FILE_UTILS.deleteDirectoryQuietly(directory);
    deleteFiles(savedFiles.values());
    savedFiles.clear();
  }

  private void deleteFiles(final Iterable<File> files) {
    for(final File file : files) {
      FILE_UTILS.deleteQuietly(file);
    }
  }

  protected void init() {
    doInit();
  }

  private void doInit() {
    stagingDirectoryFactory = EasyMock.createMock(CredentialedStagingDirectoryFactory.class);
    stagingDirectory = EasyMock.createMock(StagingDirectory.class);

    EasyMock.expect(getStagingDirectory().getAbsolutePath()).andStubReturn(path);
    EasyMock.expect(getStagingDirectory().getSep()).andStubReturn(sep);

    tracker = EasyMock.createMock(DisposableResourceTracker.class);

    getFactory().setDisposableResourceTrackerSupplier(Suppliers.ofInstance(getTracker()));
    getFactory().setCredentialedStagingDirectoryFactory(getStagingDirectoryFactory());

    mockObjects = MockObjectCollection.fromObjects(getStagingDirectoryFactory(), getStagingDirectory(), getTracker());

    EasyMock.expect(getStagingDirectoryFactory().get(getCredential())).andStubReturn(getStagingDirectory());
  }

  protected JobProcessorConfiguration getJobProcessorConfiguration() {
    return JobProcessorConfigurationFactories.getInstance().get(getCredential());
  }

  protected MockObjectCollection getMockObjects() {
    return mockObjects;
  }

  protected void replay() {
    mockObjects.replay();
  }

  protected void verifyAndReset() {
    mockObjects.verifyAndReset();

    EasyMock.expect(getStagingDirectory().getAbsolutePath()).andStubReturn(path);
    EasyMock.expect(getStagingDirectory().getSep()).andStubReturn(sep);
  }

  protected void expectAddResource(final String filename) {
    final InputContext inputContext = EasyMock.createMock(InputContext.class);
    EasyMock.expect(getStagingDirectory().getInputContext(filename)).andReturn(inputContext);
    getTracker().add(inputContext);
  }

  protected InputContext getDownloadContext() {
    final InputContext downloadContext = EasyMock.createMock(InputContext.class);
    mockObjects.add(downloadContext);
    return downloadContext;
  }

  protected List<InputContext> getDownloadContexts(final int num) {
    final List<InputContext> inputContexts = Lists.newArrayListWithCapacity(num);
    for(int i = 0; i < num; i++) {
      final InputContext inputContext = EasyMock.createMock(InputContext.class);
      mockObjects.add(inputContext);
      inputContexts.add(inputContext);
    }
    return inputContexts;
  }

  protected InputContext getDirMockInputContextAndReturn(final String path, final InputContext inputContext) {
    EasyMock.expect(getStagingDirectory().getInputContext(path)).andReturn(inputContext);
    return inputContext;
  }

  protected List<OutputContext> getDirMockOutputContexts(final Iterable<String> paths) {
    final List<OutputContext> contexts = Lists.newArrayList();
    for(final String path : paths) {
      contexts.add(getDirMockOutputContext(path));
    }
    return contexts;
  }

  protected ByteArrayOutputStream expectNewTrackerStream() {
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    EasyMock.expect(getTracker().newStream()).andReturn(stream);
    return stream;
  }

  protected OutputContext expectGetDirMockOutputContextAndRecord(final String path) {
    final File file = FILE_UTILS.createTempFile();
    savedFiles.put(path, file);
    return getDirMockOutputContextAndReturn(path, OutputContexts.forFile(file));
  }

  protected byte[] getRecordedOutputContents(final String path) {
    final File savedFile = savedFiles.get(path);
    return FILE_UTILS.readFileToByteArray(savedFile);
  }

  protected String getRecordedOutputAsString(final String path) {
    final File savedFile = savedFiles.get(path);
    Preconditions.checkNotNull(savedFile);
    return FILE_UTILS.readFileToString(savedFile);
  }

  protected OutputContext getDirMockOutputContextAndReturn(final String path, final OutputContext outputContext) {
    EasyMock.expect(getStagingDirectory().getOutputContext(path)).andReturn(outputContext);
    return outputContext;
  }

  protected OutputContext getDirMockOutputContext(final String path) {
    final OutputContext outputContext = EasyMock.createMock(OutputContext.class);
    mockObjects.add(outputContext);
    return getDirMockOutputContextAndReturn(path, outputContext);
  }

  protected List<OutputContext> getDirMockOutputContexts(final String... paths) {
    return getDirMockOutputContexts(Arrays.asList(paths));
  }

  public Credential getCredential() {
    return credential;
  }

  public CredentialedStagingDirectoryFactory getStagingDirectoryFactory() {
    return stagingDirectoryFactory;
  }

  public StagingDirectory getStagingDirectory() {
    return stagingDirectory;
  }

  public void setFactory(final T factory) {
    this.factory = factory;
  }

  protected void setFactoryAndInit(final T factory) {
    setFactory(factory);
    doInit();
  }

  public T getFactory() {
    return factory;
  }

  public DisposableResourceTracker getTracker() {
    return tracker;
  }

  protected void expectRecoverStagingDirectory() {
    EasyMock.expect(getStagingDirectoryFactory().get(getCredential(), getPath())).andReturn(getStagingDirectory());
  }

  protected void expectMakeDirectory(final String directory) {
    getStagingDirectory().makeDirectory(directory);
  }

}
