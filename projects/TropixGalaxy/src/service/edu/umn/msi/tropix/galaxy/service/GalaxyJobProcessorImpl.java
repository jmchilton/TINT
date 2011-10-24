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

package edu.umn.msi.tropix.galaxy.service;

import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;

class GalaxyJobProcessorImpl extends BaseExecutableJobProcessorImpl {
  private static final Log LOG = LogFactory.getLog(GalaxyJobProcessorImpl.class);
  private final GalaxyConverter galaxyConverter;
  private Iterable<InputContext> inputContexts;
  private Iterable<String> fileNames;
  private Tool tool;
  private RootInput rootInput;
  private ToolEvaluator toolEvaluator;

  GalaxyJobProcessorImpl(final GalaxyConverter galaxyConverter) {
    this.galaxyConverter = galaxyConverter;
  }

  void setInputContext(final Iterable<InputContext> inputContexts) {
    this.inputContexts = inputContexts;
  }

  void setFileNames(final Iterable<String> fileNames) {
    this.fileNames = fileNames;
  }

  void setTool(final Tool tool) {
    this.tool = tool;
  }

  void setRootInput(final RootInput rootInput) {
    this.rootInput = rootInput;
  }

  @Override
  protected void doPreprocessing() {
    final Iterator<String> fileNameIterator = fileNames.iterator();
    final Iterator<InputContext> inputContextsIterator = inputContexts.iterator();
    while(fileNameIterator.hasNext()) {
      Preconditions.checkState(inputContextsIterator.hasNext());
      final String fileName = fileNameIterator.next();
      LOG.debug(String.format("In preprocessing with filename %s", fileName));
      // Verify this write attempt isn't trying to write outside the specified staging directory.
      // Later this should be modified to let subdirectories be written.
      Preconditions.checkState(FilenameUtils.getName(fileName).equals(fileName));
      final OutputContext outputContext = getStagingDirectory().getOutputContext(fileName);
      inputContextsIterator.next().get(outputContext);
    }
    if(LOG.isDebugEnabled()) {
      LOG.debug("Pre Expanded Galaxy Tool");
      LOG.debug(GalaxyXmlUtils.serialize(tool));
      LOG.debug("RootInput");
      LOG.debug(GalaxyXmlUtils.serialize(rootInput));
    }
    toolEvaluator.resolve(tool, Contexts.expandPathsAndBuildContext(tool, rootInput, getStagingDirectory()));
    if(LOG.isDebugEnabled()) {
      LOG.debug("Expanded Galaxy Tool");
      LOG.debug(new XMLUtility<Tool>(Tool.class).toString(tool));
    }
    if(tool.getConfigfiles() != null) {
      for(final ConfigFile configFile : tool.getConfigfiles().getConfigfile()) {
        final String configFileName = configFile.getName();
        Preconditions.checkState(FilenameUtils.getName(configFileName).equals(configFileName));
        getStagingDirectory().getOutputContext(configFileName).put(configFile.getValue().getBytes());
      }
    }
    final JobDescriptionType jobDescription = getJobDescription().getJobDescriptionType();
    jobDescription.setDirectory(getStagingDirectory().getAbsolutePath());
    galaxyConverter.populateJobDescription(jobDescription, tool);
  }

  @Override
  protected void doPostprocessing() {
    if(wasCompletedNormally()) {
      final String standardError = InputContexts.toString(getStagingDirectory().getInputContext(BaseExecutableJobProcessorFactoryImpl.DEFAULT_STANDARD_ERROR_FILE_NAME));
      if(StringUtils.hasText(standardError)) {
        throw new IllegalStateException(String.format("Problem executing galaxy tool, standard error was %s", standardError));
      }
    }
    for(final Data output : tool.getOutputs().getData()) {
      final InputContext inputContext = getStagingDirectory().getInputContext(output.getName());
      getResourceTracker().add(inputContext);
    }
  }

  public void setToolEvaluator(final ToolEvaluator toolEvaluator) {
    this.toolEvaluator = toolEvaluator;
  }

}
