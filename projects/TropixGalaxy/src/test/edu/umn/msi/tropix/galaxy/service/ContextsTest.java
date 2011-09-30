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

import org.testng.annotations.Test;

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

public class ContextsTest {

  @Test(groups = "unit")
  public void testCond() {
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

  }
  
}
