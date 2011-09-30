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

import java.util.List;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Tool;

@JobType("Galaxy")
class GalaxyJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<GalaxyJobProcessorImpl> implements GalaxyJobProcessorFactory {
  private final GalaxyConverter galaxyConverter;
  private final ToolEvaluator toolEvaluator;

  GalaxyJobProcessorFactoryImpl(final GalaxyConverter galaxyConverter, final ToolEvaluator toolEvaluator) {
    this.galaxyConverter = galaxyConverter;
    this.toolEvaluator = toolEvaluator;
  }

  public GalaxyJobProcessorImpl createJob(final JobProcessorConfiguration config, final Tool tool, final RootInput input, final List<String> inputNames, final List<InputContext> inputs) {
    final GalaxyJobProcessorImpl galaxyJobProcessor = super.create(config);
    galaxyJobProcessor.setTool(tool);
    galaxyJobProcessor.setRootInput(input);
    galaxyJobProcessor.setFileNames(inputNames);
    galaxyJobProcessor.setInputContext(inputs);
    galaxyJobProcessor.setToolEvaluator(toolEvaluator);
    return galaxyJobProcessor;
  }

  protected GalaxyJobProcessorImpl create() {
    final GalaxyJobProcessorImpl processor = new GalaxyJobProcessorImpl(galaxyConverter);
    return processor;
  }

}
