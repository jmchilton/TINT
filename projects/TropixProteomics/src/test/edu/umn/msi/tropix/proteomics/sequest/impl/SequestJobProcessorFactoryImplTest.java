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

package edu.umn.msi.tropix.proteomics.sequest.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.same;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.io.ByteArrayOutputContextImpl;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.DTAListWriter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAConverter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAOptions;
import edu.umn.msi.tropix.proteomics.sequest.SequestLineCallback;
import edu.umn.msi.tropix.proteomics.sequest.SequestParameterTranslator;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class SequestJobProcessorFactoryImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final String PATH = "C:\\PATH\\moo";
  private static final String SEP = "\\";
  private SequestJobProcessorFactoryImpl factory;
  private CredentialedStagingDirectoryFactory stagingDirectoryFactory;
  private StagingDirectory stagingDirectory;
  private DisposableResourceTracker tracker;
  private InputContext databasePopulator, mzxmlPopulator;
  private MzXMLToDTAConverter converter;
  private DTAList dtaList;
  private DTAListWriter dtaListWriter;
  private Iterator<DTAList.Entry> entry;
  private SequestParameterTranslator sequestParameterTranslator;
  private LineProcessingFileProgressTracker sequestProgressTracker;
  private SequestLineCallback lineCallback;
  private ZipUtils zipUtils;
  private final Credential credential = Credentials.getMock();

  private SequestParameters parameters = new SequestParameters();
  private MockObjectCollection mockObjects;

  @SuppressWarnings("unchecked")
  @BeforeMethod(groups = "unit")
  public void init() {
    factory = new SequestJobProcessorFactoryImpl();
    stagingDirectoryFactory = EasyMock.createMock(CredentialedStagingDirectoryFactory.class);
    stagingDirectory = EasyMock.createMock(StagingDirectory.class);

    parameters = new SequestParameters();

    tracker = createMock(DisposableResourceTracker.class);
    sequestProgressTracker = EasyMock.createMock(LineProcessingFileProgressTracker.class);
    lineCallback = EasyMock.createMock(SequestLineCallback.class);
    sequestParameterTranslator = createMock(SequestParameterTranslator.class);
    databasePopulator = createMock(InputContext.class);
    mzxmlPopulator = InputContexts.forInputStream(ProteomicsTests.getResourceAsStream("validMzXML.mzxml"));
    converter = createMock(MzXMLToDTAConverter.class);
    dtaList = createMock(DTAList.class);
    dtaListWriter = createMock(DTAListWriter.class);
    entry = createMock(Iterator.class);
    zipUtils = createMock(ZipUtils.class);

    factory.setCredentialedStagingDirectoryFactory(stagingDirectoryFactory);
    factory.setSequestParameterTranslator(sequestParameterTranslator);
    factory.setDtaListWriter(dtaListWriter);
    factory.setMzXMLToDtaConverter(converter);
    factory.setDisposableResourceTrackerSupplier(Suppliers.ofInstance(tracker));

    mockObjects = MockObjectCollection.fromObjects(stagingDirectoryFactory, stagingDirectory, tracker, sequestParameterTranslator, databasePopulator, zipUtils, converter, dtaList, dtaListWriter, entry, sequestProgressTracker, lineCallback);

  }

  @Test(groups = "unit")
  public void allFiles() throws IOException {
    test(true, true, false, false, 2, 0);
  }

  @Test(groups = "unit")
  public void noParams() throws IOException {
    test(true, false, false, false, 2, 0);
  }

  @Test(groups = "unit")
  public void notDta() throws IOException {
    test(true, false, false, false, 2, 0);
  }

  @Test(groups = "unit")
  public void justOut() throws IOException {
    test(false, false, false, false, 2, 0);
  }

  @Test(groups = "unit")
  public void setupProgressTracker() throws IOException {
    test(true, true, true, false, 2, 0);
  }

  @Test(groups = "unit")
  public void resetup() throws IOException {
    test(true, true, false, true, 2, 0);
  }

  @Test(groups = "unit")
  public void wrongNumFiles00() throws IOException {
    test(true, true, false, false, 0, 0);
  }

  @Test(groups = "unit")
  public void wrongNumFiles01() throws IOException {
    test(true, true, false, false, 0, 1);
  }

  @Test(groups = "unit")
  public void wrongNumFiles10() throws IOException {
    test(true, true, false, false, 1, 0);
  }

  @Test(groups = "unit")
  public void wrongNumFilesErrorTolerant() throws IOException {
    test(true, true, false, false, 1, 1);
  }

  @Test(groups = "unit")
  public void resetupProgressTracker() throws IOException {
    test(true, true, true, true, 2, 0);
  }

  public void test(final boolean writeDta, final boolean writeParams, final boolean mockTracker, final boolean initialized, final int numWritten, final int numAllowedDropped) throws IOException {
    String path = SequestJobProcessorFactoryImplTest.PATH, sep = SequestJobProcessorFactoryImplTest.SEP;
    factory.setIncludeDta(writeDta);
    factory.setIncludeParams(writeParams);
    factory.setAllowedDroppedFiles(numAllowedDropped);
    
    final File tempDir = FileUtilsFactory.getInstance().createTempDirectory();
    try {
      if(mockTracker) {
        path = tempDir.getAbsolutePath();
        sep = File.separator;
        factory.setProgressTrackerSupplier(Suppliers.ofInstance(sequestProgressTracker));
        factory.setLineCallbackSupplier(Suppliers.ofInstance(lineCallback));
        sequestProgressTracker.setTrackedFile(new File(tempDir, "output_log"));
        sequestProgressTracker.setLineCallback(lineCallback);
        lineCallback.setDtaCount(2);

        final File dtaFile1 = new File(tempDir, "a.123.125.1.dta");
        final File dtaFile2 = new File(tempDir, "a.127.128.2.dta");
        FILE_UTILS.writeStringToFile(dtaFile1, "123");
        FILE_UTILS.writeStringToFile(dtaFile2, "127");
      }

      EasyMock.expect(stagingDirectory.getAbsolutePath()).andStubReturn(path);
      EasyMock.expect(stagingDirectory.getSep()).andStubReturn(sep);

      // File expectedDbFile = new File(stagedDirectory, "db.fasta");
      // File mzxmlFile = new File(stagedDirectory, "input.mzxml");

      final ByteArrayOutputContextImpl dbOutput = new ByteArrayOutputContextImpl(), paramOutput = new ByteArrayOutputContextImpl();

      if(!initialized) {
        EasyMock.expect(stagingDirectory.getOutputContext("_moo.fasta")).andReturn(dbOutput);
        databasePopulator.get(dbOutput);

        EasyMock.expect(stagingDirectory.getOutputContext("sequest.params")).andReturn(paramOutput);
        EasyMock.expect(converter.mzxmlToDTA(isA(InputStream.class), (MzXMLToDTAOptions) isNull())).andReturn(dtaList);
        EasyMock.expect(dtaListWriter.writeFiles(stagingDirectory, dtaList)).andReturn(Lists.newArrayList(path + sep + "a.123.125.1.dta", path + sep + "a.127.128.2.dta"));
        EasyMock.expect(sequestParameterTranslator.getSequestParameters(same(parameters), eq(path + sep + "_moo.fasta"))).andReturn("CONTENTS");

        EasyMock.expect(stagingDirectoryFactory.get(credential)).andReturn(stagingDirectory);
        stagingDirectory.setup();
      } else {
        EasyMock.expect(stagingDirectoryFactory.get(credential, path)).andReturn(stagingDirectory);
      }

      // TODO: Test the output stream points to the right place...
      final OutputStream outputStream = new ByteArrayOutputStream();
      expect(tracker.newStream()).andReturn(outputStream);

      final LinkedList<String> resourcesWritten = new LinkedList<String>(), resourcesOut = new LinkedList<String>();
      final LinkedList<InputContext> inputContexts = new LinkedList<InputContext>();
      InputContext inputContext;
      resourcesWritten.add("sequest.params");
      if(writeParams) {
        resourcesOut.add("sequest.params");
        inputContext = EasyMock.createMock(InputContext.class);
        EasyMock.expect(stagingDirectory.getInputContext("sequest.params")).andReturn(inputContext).anyTimes();
        inputContexts.add(inputContext);
      }
      resourcesWritten.add("a.123.125.1.dta");
      resourcesWritten.add("a.127.128.2.dta");
      if(writeDta) {
        resourcesOut.add("a.123.125.1.dta");
        resourcesOut.add("a.127.128.2.dta");
        inputContext = EasyMock.createMock(InputContext.class);
        EasyMock.expect(stagingDirectory.getInputContext("a.123.125.1.dta")).andReturn(inputContext).anyTimes();
        inputContexts.add(inputContext);

        inputContext = EasyMock.createMock(InputContext.class);
        EasyMock.expect(stagingDirectory.getInputContext("a.127.128.2.dta")).andReturn(inputContext).anyTimes();
        inputContexts.add(inputContext);
      }
      if(numWritten > 0) {
        resourcesOut.add("a.123.125.1.out");
        resourcesWritten.add("a.123.125.1.out");
        inputContext = EasyMock.createMock(InputContext.class);
        EasyMock.expect(stagingDirectory.getInputContext("a.123.125.1.out")).andReturn(inputContext).anyTimes();
        inputContexts.add(inputContext);
      }
      if(numWritten > 1) {
        resourcesOut.add("a.127.128.2.out");
        resourcesWritten.add("a.127.128.2.out");
        inputContext = EasyMock.createMock(InputContext.class);
        EasyMock.expect(stagingDirectory.getInputContext("a.127.128.2.out")).andReturn(inputContext).anyTimes();
        inputContexts.add(inputContext);
      }

      final ByteArrayOutputContextImpl filesContext = new ByteArrayOutputContextImpl();
      EasyMock.expect(stagingDirectory.getOutputContext("dta_files")).andReturn(filesContext).anyTimes();

      zipUtils.zipContextsToStream(EasyMockUtils.<InputContext, Iterable<InputContext>>hasSameUniqueElements(inputContexts), EasyMockUtils.<String, Iterable<String>>hasSameUniqueElements(resourcesOut), same(outputStream));

      EasyMock.expect(stagingDirectory.getResourceNames(null)).andReturn(resourcesWritten);
      stagingDirectory.cleanUp();

      // EasyMockUtils.replayAll(mockObjects);
      mockObjects.replay();
      SequestJobProcessorImpl jobProcessor;
      if(!initialized) {
        final JobProcessorConfiguration configuration = JobProcessorConfigurationFactories.getInstance().get(credential);
        jobProcessor = factory.create(configuration);
      } else {
        final JobDescriptionType jobDescription = new JobDescriptionType();
        JobDescriptionUtils.setJobType(jobDescription, "sequest");
        JobDescriptionUtils.setStagingDirectory(jobDescription, path);
        JobDescriptionUtils.setProxy(jobDescription, credential);
        jobProcessor = factory.recover(ExecutableJobDescriptions.forJobDescriptionType(jobDescription));
      }
      
      jobProcessor.setZipUtils(zipUtils);
      jobProcessor.setDatabase(databasePopulator);
      jobProcessor.setDatabaseName("../@moo.fasta");
      jobProcessor.setInputMzXML(mzxmlPopulator);
      jobProcessor.setInputParameters(parameters);

      if(mockTracker) {
        assert jobProcessor.getProgressTracker() == sequestProgressTracker;
      }
      if(!initialized) {
        final ExecutableJobDescription jobDescription = jobProcessor.preprocess();
        assert new String(paramOutput.toByteArray()).equals("CONTENTS");
        final String[] args = jobDescription.getJobDescriptionType().getArgument();
        assert args[0].equals("-P" + path + sep + "sequest.params");
        assert args[1].equals("-R" + path + sep + "dta_files");
      }
      RuntimeException e = null;
      try {
        jobProcessor.postprocess(true);
      } catch(final RuntimeException exception) {
        e = exception;
      }
      if(numWritten + numAllowedDropped < 2) {
        assert e instanceof IllegalStateException;
        return;
      } else {
        assert e == null;
      }
      mockObjects.verifyAndReset();
      if(!initialized) {
        final String filesContents = new String(filesContext.toByteArray());
        final Set<String> files = Sets.newHashSet(Arrays.asList(filesContents.split(System.getProperty("line.separator"))));
        for(final String resourceWritten : Lists.newArrayList("a.123.125.1.dta", "a.127.128.2.dta")) {
          assert files.contains(path + sep + resourceWritten) : filesContents;
        }
      }
    } finally {
      FILE_UTILS.deleteDirectory(tempDir);
    }
  }

}
