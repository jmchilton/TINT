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

package edu.umn.msi.tropix.genomics.bowtie.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.globus.exec.generated.JobDescriptionType;
import org.springframework.util.StringUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;
import edu.umn.msi.tropix.genomics.bowtie.input.InputFormat;

public class BowtieJobProcessorImpl extends BaseExecutableJobProcessorImpl {
  private static final Pattern EBWT_NAME_PATTERN = Pattern.compile("(.*?).(rev.)?[1-4].ebwt");
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private BowtieInputOptionsBuilder inputOptionsBuilder = new BowtieInputOptionsBuilderImpl();
  private InputContext indexInputContext;
  private Iterable<InputContext> databaseContexts;
  private BowtieInput inputParameters;

  @Override
  protected void doPreprocessing() {
    final String indexName = writeIndex();

    final List<String> databaseNames = writeDatabases();

    final String commandLineOptions = inputOptionsBuilder.getCommandLineOptions(inputParameters, databaseNames, indexName, null);

    final JobDescriptionType jobDescriptionType = getJobDescription().getJobDescriptionType();
    final String[] args = Iterables.toArray(Iterables.filter(Arrays.asList(commandLineOptions.split("\\s+")), new Predicate<String>() {
      public boolean apply(final String str) {
        return StringUtils.hasText(str.trim());
      }
    }), String.class);
    jobDescriptionType.setArgument(args);
    jobDescriptionType.setStdout(getStagingDirectory().getAbsolutePath() + getStagingDirectory().getSep() + "output.txt");
    jobDescriptionType.setStderr(getStagingDirectory().getAbsolutePath() + getStagingDirectory().getSep() + "log.err");
    jobDescriptionType.setDirectory(getStagingDirectory().getAbsolutePath());
  }

  @Override
  protected void doPostprocessing() {
    if(wasCompletedNormally()) {
      getResourceTracker().add(getStagingDirectory().getInputContext("output.txt"));
    }
  }

  private String writeIndex() {
    final File tempIndexZipFile = FILE_UTILS.createTempFile("tpxtmp", ".zip");
    final List<String> names = Lists.newArrayListWithCapacity(6);
    try {
      indexInputContext.get(tempIndexZipFile);
      ZIP_UTILS.unzipToContexts(tempIndexZipFile, new Function<String, OutputContext>() {
        public OutputContext apply(final String entryName) {
          // Flatten the name so the final resides in the base directory
          final String filename = FilenameUtils.getName(entryName);

          Preconditions.checkState(EBWT_NAME_PATTERN.matcher(filename).matches());
          // Record this filename
          names.add(filename);

          // Get and return outputcontext for that name
          final OutputContext outputContext = getStagingDirectory().getOutputContext(filename);
          return outputContext;
        }
      });
      Preconditions.checkState(names.size() == 6);
      final Matcher nameMatcher = EBWT_NAME_PATTERN.matcher(names.get(0));
      nameMatcher.matches();
      return nameMatcher.group(1);
    } finally {
      FILE_UTILS.deleteQuietly(tempIndexZipFile);
    }
  }

  private List<String> writeDatabases() {
    final List<String> names = Lists.newLinkedList();
    final InputFormat inputFormat = InputFormat.valueOf(inputParameters.getInputsFormat());
    int i = 0;
    for(final InputContext databaseContext : databaseContexts) {
      final String name = "db" + ++i + "." + inputFormat.getExtension();
      names.add(name);
      databaseContext.get(getStagingDirectory().getOutputContext(name));
    }
    return names;
  }

  public void setInputParameters(final BowtieInput inputParameters) {
    this.inputParameters = inputParameters;
  }

  public void setDatabaseContexts(final Iterable<InputContext> databaseContexts) {
    this.databaseContexts = databaseContexts;
  }

  public void setIndexInputContext(final InputContext indexInputContext) {
    this.indexInputContext = indexInputContext;
  }

  @VisibleForTesting
  void setBowtieInputOptionsBuilder(final BowtieInputOptionsBuilder inputOptionsBuilder) {
    this.inputOptionsBuilder = inputOptionsBuilder;
  }

}
