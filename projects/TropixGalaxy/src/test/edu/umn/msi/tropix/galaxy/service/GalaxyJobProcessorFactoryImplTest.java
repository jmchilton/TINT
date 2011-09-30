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

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Conditional;
import edu.umn.msi.tropix.galaxy.tool.ConditionalWhen;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFiles;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.Inputs;
import edu.umn.msi.tropix.galaxy.tool.Outputs;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class GalaxyJobProcessorFactoryImplTest extends JobProcessorFactoryTest<GalaxyJobProcessorFactoryImpl> {
  private GalaxyConverter galaxyConverter;
  private ToolEvaluator toolEvaluator;

  @BeforeMethod(groups = "unit")
  public void init() {
    galaxyConverter = EasyMock.createMock(GalaxyConverter.class);
    toolEvaluator = EasyMock.createMock(ToolEvaluator.class);
    setFactory(new GalaxyJobProcessorFactoryImpl(galaxyConverter, toolEvaluator));
    super.init();
    getMockObjects().add(galaxyConverter);
    getMockObjects().add(toolEvaluator);
  }

  @Test(groups = "unit")
  public void create() {
    final Tool tool = new Tool();
    final Outputs outputs = new Outputs();
    tool.setOutputs(outputs);

    final Data output1 = new Data();
    output1.setName("output1");
    outputs.getData().add(output1);

    final Inputs inputs = new Inputs();
    tool.setInputs(inputs);
    final Param toolInput1 = new Param();
    toolInput1.setName("file1");
    toolInput1.setType(ParamType.DATA);
    inputs.getInputElement().add(toolInput1);

    final Param toolInput2 = new Param();
    toolInput2.setName("file2");
    toolInput2.setType(ParamType.DATA);
    inputs.getInputElement().add(toolInput2);

    final Param toolInput3 = new Param();
    toolInput3.setName("foo");
    toolInput3.setType(ParamType.TEXT);
    inputs.getInputElement().add(toolInput3);
    
    final Conditional cond = new Conditional();
    cond.setName("iscool");

    final Param cool1 = new Param();
    cool1.setName("cool1");
    cool1.setType(ParamType.TEXT);
    
    final Param cool2 = new Param();
    cool2.setName("cool2");
    cool2.setType(ParamType.TEXT);
    
    final ConditionalWhen wy = new ConditionalWhen();
    wy.setValue("yes");
    
    final ConditionalWhen wn = new ConditionalWhen();
    wn.setValue("yes");
    
    cond.getWhen().add(wy);
    cond.getWhen().add(wn);
    
    
    final ConfigFiles configFiles = new ConfigFiles();
    tool.setConfigfiles(configFiles);
    final ConfigFile configFile1 = new ConfigFile();
    configFile1.setName("config1.py");
    configFile1.setValue("python contents");
    configFiles.getConfigfile().add(configFile1);

    final RootInput rootInput = new RootInput();
    final Input input1 = new Input();
    input1.setName("file1");
    input1.setValue("file1.txt");
    rootInput.getInput().add(input1);

    final Input input2 = new Input();
    input2.setName("file2");
    input2.setValue("file2.fasta");
    rootInput.getInput().add(input2);

    final Input input3 = new Input();
    input3.setName("foo");
    input3.setValue("bar");
    rootInput.getInput().add(input3);

    getStagingDirectory().setup();
    final List<InputContext> inputContexts = super.getDownloadContexts(3);
    final List<OutputContext> outputContexts = getDirMockOutputContexts("file1.txt", "file2.fasta", "config1.py");

    inputContexts.get(0).get(outputContexts.get(0));
    inputContexts.get(1).get(outputContexts.get(1));
    outputContexts.get(2).put(EasyMock.aryEq("python contents".getBytes()));
    final Capture<Context> contextCapture = EasyMockUtils.newCapture();
    final Capture<JobDescriptionType> jobDescriptionCapture = EasyMockUtils.newCapture();
    toolEvaluator.resolve(EasyMock.same(tool), EasyMock.capture(contextCapture));
    galaxyConverter.populateJobDescription(EasyMock.capture(jobDescriptionCapture), EasyMock.same(tool));
    expectAddResource("output1");
    getStagingDirectory().cleanUp();
    getMockObjects().replay();
    final GalaxyJobProcessorImpl processor = getFactory().createJob(getJobProcessorConfiguration(), tool, rootInput, Lists.newArrayList("file1.txt", "file2.fasta"), inputContexts);

    final ExecutableJobDescription executableJobDescription = processor.preprocess();
    processor.postprocess(true);
    verifyAndReset();
    final JobDescriptionType jobDescription = executableJobDescription.getJobDescriptionType();
    assert jobDescription.getDirectory().equals(getPath());
    assert jobDescription == jobDescriptionCapture.getValue();
    final Context context = contextCapture.getValue();
    assert context.get("output1").toString().equals(getPath() + "/output1") : context.get("output1").toString();
    assert context.get("file1").toString().equals(getPath() + "/file1.txt");
    assert context.get("file2").toString().equals(getPath() + "/file2.fasta");
    assert context.get("foo").toString().equals("bar");
  }
}
