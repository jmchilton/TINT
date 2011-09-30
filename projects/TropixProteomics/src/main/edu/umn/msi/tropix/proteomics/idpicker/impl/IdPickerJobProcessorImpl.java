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

package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.globus.exec.generated.JobDescriptionType;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.PropertiesUtils;
import edu.umn.msi.tropix.common.io.PropertiesUtilsFactory;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;

class IdPickerJobProcessorImpl extends BaseExecutableJobProcessorImpl {
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private static final PropertiesUtils PROPERTIES_UTILS = PropertiesUtilsFactory.getInstance();
  private static final String DATABASE_PATH = "db.fasta";
  private Iterable<InputContext> pepXmlContexts;
  private InputContext databaseContext;
  private IdPickerParameters parameters;
  private FileMaskCreator fileMaskCreator;

  @Override
  protected void doPreprocessing() {
    databaseContext.get(getStagingDirectory().getOutputContext(DATABASE_PATH));

    fileMaskCreator = new FileMaskCreator(parameters);

    writeSampleDirectories();
    savePepXmlFiles();
    getStagingDirectory().getOutputContext("idpQonvert-files").put(fileMaskCreator.getQonvertLines().getBytes());
    getStagingDirectory().getOutputContext("idpAssemble-files").put(fileMaskCreator.getAssembleLines().getBytes());
    writeQonvertCfg();
    writeAssembleCfg();
    writeReportCfg();
    final JobDescriptionType jobDescription = getJobDescription().getJobDescriptionType();
    final List<String> arguments = Lists.newArrayList();
    jobDescription.setArgument(Iterables.toArray(arguments, String.class));
    jobDescription.setDirectory(getStagingDirectory().getAbsolutePath());
  }

  private void writeSampleDirectories() {
    for(final String sampleName : fileMaskCreator.getSampleNames()) {
      getStagingDirectory().makeDirectory(sampleName);
    }
  }

  private void putProperties(final Properties properties, final String name, final String value) {
    if(value != null) {
      properties.put(name, value);
    }
  }

  private void putProperties(final Properties properties, final String name, final Boolean value) {
    putProperties(properties, name, value == null ? null : Boolean.toString(value));
  }

  private void putProperties(final Properties properties, final String name, final Double value) {
    putProperties(properties, name, value == null ? null : Double.toString(value));
  }

  private void putProperties(final Properties properties, final String name, final Integer value) {
    putProperties(properties, name, value == null ? null : Integer.toString(value));
  }

  private void writeQonvertCfg() {
    final Properties properties = new Properties();
    putProperties(properties, "ProteinDatabase", DATABASE_PATH);
    putProperties(properties, "PreserveInputHierarchy", "true");
    putProperties(properties, "HasDecoyDatabase", parameters.getHasDecoyDatabase());
    if(parameters.getDecoyPrefix() != null) {
      putProperties(properties, "DecoyPrefix", parameters.getDecoyPrefix());
    }
    putProperties(properties, "NormalizeSearchScores", parameters.getNormalizeSearchScores());
    putProperties(properties, "OptimizeScoreWeights", parameters.getOptimizeScoreWeights());
    putProperties(properties, "MaxFDR", parameters.getMaxFDRQonvert());
    putProperties(properties, "MaxResultRank", parameters.getMaxResultRank());
    putProperties(properties, "OptimizeScorePermutations", parameters.getOptimizeScorePermutations());
    getStagingDirectory().getOutputContext("idpQonvert.cfg").put(PROPERTIES_UTILS.toString(properties).getBytes());
  }

  private void writeAssembleCfg() {
    final Properties properties = new Properties();
    putProperties(properties, "MaxFDR", parameters.getMaxFDRAssemble());
    putProperties(properties, "MaxResultRank", parameters.getMaxResultRank());
    getStagingDirectory().getOutputContext("idpAssemble.cfg").put(PROPERTIES_UTILS.toString(properties).getBytes());
  }

  private void writeReportCfg() {
    final Properties properties = new Properties();
    putProperties(properties, "MinDistinctPeptides", parameters.getMinDistinctPeptides());
    putProperties(properties, "MaxAmbiguousIds", parameters.getMaxAmbiguousIds());
    putProperties(properties, "MaxFDR", parameters.getMaxFDRReport());
    putProperties(properties, "MinAdditionalPeptides", parameters.getMinAdditionalPeptides());
    putProperties(properties, "MaxResultRank", parameters.getMaxResultRank());
    getStagingDirectory().getOutputContext("idpReport.cfg").put(PROPERTIES_UTILS.toString(properties).getBytes());
  }

  @Override
  protected void doPostprocessing() {
    if(wasCompletedNormally()) {
      getResourceTracker().add(getStagingDirectory().getInputContext("combined-sources.xml"));
      final OutputStream zipOutputStream = getResourceTracker().newStream();
      ZIP_UTILS.zipDirectory(getStagingDirectory(), "combined-sources", zipOutputStream);
    }
  }

  private void savePepXmlFiles() {
    final Iterator<String> inputPathsIter = fileMaskCreator.getPepXmlPaths(getStagingDirectory().getSep()).iterator();
    for(InputContext pepXmlContext : pepXmlContexts) {
      Preconditions.checkState(inputPathsIter.hasNext());
      final String name = inputPathsIter.next();
      pepXmlContext.get(getStagingDirectory().getOutputContext(name));
    }
  }

  public void setPepXmlContexts(final Iterable<InputContext> pepXmlContexts) {
    this.pepXmlContexts = pepXmlContexts;
  }

  public void setDatabaseContext(final InputContext databaseContext) {
    this.databaseContext = databaseContext;
  }

  public void setParameters(final IdPickerParameters parameters) {
    this.parameters = parameters;
  }
}
