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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.StringPredicates;
import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.TempFileSuppliers;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackable;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTracker;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.DTAListWriter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAConverter;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;
import edu.umn.msi.tropix.proteomics.sequest.SequestLineCallback;
import edu.umn.msi.tropix.proteomics.sequest.SequestParameterTranslator;

public class SequestJobProcessorImpl extends IdentificationJobProcessorImpl<SequestParameters> implements ProgressTrackable {
  private static final Log LOG = LogFactory.getLog(SequestJobProcessorImpl.class);
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final Supplier<File> TEMP_FILE_SUPPLIER = TempFileSuppliers.getDefaultTempFileSupplier();
  private static final String PARAMS_PATH = "sequest.params";
  private static final String FILES_PATH = "dta_files";
  private DTAListWriter dtaListWriter;
  private MzXMLToDTAConverter mzxmlToDtaConverter;
  private SequestParameterTranslator parameterTranslator;
  private LineProcessingFileProgressTracker sequestProgressTracker;
  private SequestLineCallback sequestLineCallback;
  private final FileUtils fileUtils = FileUtilsFactory.getInstance();
  private ZipUtils zipUtils = ZipUtilsFactory.getInstance();
  private boolean includeParams;
  private boolean includeDta;
  private long allowedDroppedFiles = 0L;

  private void initializeOutputTracker() {
    if(sequestProgressTracker != null) {
      final File stagingDirectoryFile = new File(getStagingDirectory().getAbsolutePath());
      final File parent = stagingDirectoryFile.getParentFile();
      if(parent != null && parent.exists()) {
        final File stdoutFile = new File(getStagingDirectory().getAbsolutePath(), "output_log");
        this.getJobDescription().getJobDescriptionType().setStdout(stdoutFile.getAbsolutePath());
        fileUtils.touch(stdoutFile);
        sequestProgressTracker.setTrackedFile(stdoutFile);
        final int dtaCount = fileUtils.listFiles(stagingDirectoryFile, new String[] {"dta"}, false).size();
        sequestProgressTracker.setLineCallback(sequestLineCallback);
        sequestLineCallback.setDtaCount(dtaCount);
      }
    }
  }
  
  private String getSequestDatabaseName() {
    return SequestUtils.sanitizeDatabaseName(super.getDatabaseName());
  }

  @Override
  protected void doPreprocessing() {
    LOG.debug("Attempting to download database file for sequest job");
    getDatabase().get(getStagingDirectory().getOutputContext(getSequestDatabaseName()));

    LOG.debug("About to translate sequest parameters");
    final SequestParameters inputParameters = getParameters();
    
    final String paramFileContents = parameterTranslator.getSequestParameters(getParameters(), Directories.buildAbsolutePath(getStagingDirectory(), getSequestDatabaseName()));
    LOG.debug("About to write sequest parameters to param file ");
    getStagingDirectory().getOutputContext(PARAMS_PATH).put(paramFileContents.getBytes());

    writeDTACollection();
    initializeOutputTracker();
    getJobDescription().getJobDescriptionType().setArgument(new String[] {"-P" + Directories.buildAbsolutePath(getStagingDirectory(), PARAMS_PATH), "-R" + Directories.buildAbsolutePath(getStagingDirectory(), FILES_PATH)});
  }

  @Override
  protected void initialize() {
    initializeOutputTracker();
  }

  @Override
  protected void doPostprocessing() {
    if(wasCompletedNormally()) {
      LOG.debug("postprocess called for sequest job created");
      final Iterable<String> resources = getStagingDirectory().getResourceNames(null);
      Iterable<String> outResources = Iterables.filter(resources, StringPredicates.endsWith("out"));
      final Iterable<String> dtaResources = Iterables.filter(resources, StringPredicates.endsWith("dta"));

      final long outNum = Iterables.size(outResources), dtaNum = Iterables.size(dtaResources);

      if(outNum != dtaNum) {
        boolean failJob = false;
        if((outNum + allowedDroppedFiles) < dtaNum) {
          failJob = true;
        }
        LOG.warn("Invalid number of output files created -- expected " + dtaNum + " found " + outNum + " failing job? " + failJob);
        if(failJob) {
          throw new IllegalStateException("Failed to produce the correct number of files.");
        }
      }

      if(includeDta) {
        outResources = Iterables.concat(outResources, dtaResources);
      }
      if(includeParams) {
        outResources = Iterables.concat(outResources, Arrays.asList(PARAMS_PATH));
      }

      final Iterable<InputContext> inputContexts = Iterables.transform(outResources, new Function<String, InputContext>() {
        public InputContext apply(final String resource) {
          return getStagingDirectory().getInputContext(resource);
        }
      });
      final OutputStream resultsStream = getResourceTracker().newStream();
      try {
        zipUtils.zipContextsToStream(inputContexts, outResources, resultsStream);
      } finally {
        IO_UTILS.closeQuietly(resultsStream);
      }
    }
  }

  private void writeDTACollection() {
    final File mzxmlFile = TEMP_FILE_SUPPLIER.get();
    InputStream mzxmlStream = null;
    try {
      writeMzXML(mzxmlFile);
      LOG.debug("converting mzxml to dta");
      mzxmlStream = fileUtils.getFileInputStream(mzxmlFile);
      final DTAList dtaList = mzxmlToDtaConverter.mzxmlToDTA(mzxmlStream, null);
      LOG.debug("writing dta files");
      final Iterable<String> resources = dtaListWriter.writeFiles(getStagingDirectory(), dtaList);
      getStagingDirectory().getOutputContext(FILES_PATH).put((Joiner.on(System.getProperty("line.separator")).join(resources) + System.getProperty("line.separator")).getBytes());
    } finally {
      fileUtils.deleteQuietly(mzxmlFile);
      IO_UTILS.closeQuietly(mzxmlStream);
    }
  }

  public void setDTAListWriter(final DTAListWriter dtaListWriter) {
    this.dtaListWriter = dtaListWriter;
  }

  public void setMzXMLToDtaConverter(final MzXMLToDTAConverter mzxmlToDtaConverter) {
    this.mzxmlToDtaConverter = mzxmlToDtaConverter;

  }

  public void setParameterTranslator(final SequestParameterTranslator parameterTranslator) {
    this.parameterTranslator = parameterTranslator;
  }

  public void setZipUtils(final ZipUtils zipUtils) {
    this.zipUtils = zipUtils;
  }

  public void setIncludeParams(final boolean includeParams) {
    this.includeParams = includeParams;
  }

  public void setincludeDta(final boolean includeDta) {
    this.includeDta = includeDta;
  }

  public void setProgressTracker(final LineProcessingFileProgressTracker sequestProgressTracker) {
    this.sequestProgressTracker = sequestProgressTracker;
  }

  public void setSequestLineCallback(final SequestLineCallback sequestLineCallback) {
    this.sequestLineCallback = sequestLineCallback;
  }

  public ProgressTracker getProgressTracker() {
    return sequestProgressTracker;
  }

  public void setAllowedDroppedFiles(final long allowedDroppedFiles) {
    this.allowedDroppedFiles = allowedDroppedFiles;
  }

}
