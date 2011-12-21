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

package edu.umn.msi.tropix.galaxy.xml;

import static edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils.convert;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.galaxy.GalaxyTests;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFileType;
import edu.umn.msi.tropix.galaxy.tool.ConfigFiles;
import edu.umn.msi.tropix.galaxy.tool.Inputs;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class GalaxyXmlUtilsTest {

  // Tests that typical real Galaxy XML file can be loaded
  @Test(groups = "unit")
  public void testLoadOut2Xml() {
    GalaxyXmlUtils.load(GalaxyTests.class.getResourceAsStream("out2xml.xml"));
  }

  @Test(groups = "unit")
  public void testLabels() {
    Param param = (Param) GalaxyXmlUtils.load(GalaxyTests.class.getResourceAsStream("out2xml.xml")).getInputs().getInputElement().get(0);
    assert param.getLabel() != null;
    assert param.getLabel().equals("Zipped Sequest Output");
  }

  // Tests that typical real Galaxy XML file can be loaded
  @Test(groups = "unit")
  public void testLoadDecoyFasta() {
    GalaxyXmlUtils.load(GalaxyTests.class.getResourceAsStream("decoy_fasta.xml"));
  }

  @Test(groups = "unit")
  public void convertsInputXml() {
    Class test = org.xml.sax.AttributeList.class;
    final RootInput inputs = new RootInput();
    final Input input1 = new Input();
    input1.setName("Name1");
    input1.setValue("Value1");
    inputs.getInput().add(input1);

    final RootInput convertedInputs = convert(convert(inputs));
    final Input convertedInput = convertedInputs.getInput().get(0);
    assert convertedInput.getName().equals("Name1");
    assert convertedInput.getValue().equals("Value1");
  }

  @Test(groups = "unit")
  public void convertToolXmlFromCaGrid() {
    final edu.umn.msi.tropix.galaxy.tool.cagrid.Tool tool = new edu.umn.msi.tropix.galaxy.tool.cagrid.Tool();
    final edu.umn.msi.tropix.galaxy.tool.cagrid.ConfigFiles files = new edu.umn.msi.tropix.galaxy.tool.cagrid.ConfigFiles();
    final edu.umn.msi.tropix.galaxy.tool.cagrid.ConfigFile file = new edu.umn.msi.tropix.galaxy.tool.cagrid.ConfigFile();
    file.setType(edu.umn.msi.tropix.galaxy.tool.cagrid.ConfigFileType.literal);
    files.setConfigfile(new edu.umn.msi.tropix.galaxy.tool.cagrid.ConfigFile[] {file});
    tool.setConfigfiles(files);
    convert(tool);
  }

  @Test(groups = "unit")
  public void convertsToolXml() {
    final Tool tool = new Tool();
    final Command command = new Command();
    command.setInterpreter("python");
    command.setValue("moo.py --cow");
    tool.setCommand(command);
    final Inputs inputs = new Inputs();
    final Param param = new Param();
    param.setLabel("This is the label");
    inputs.getInputElement().add(param);
    tool.setInputs(inputs);

    final ConfigFile file = new ConfigFile();
    file.setName("name");
    file.setType(ConfigFileType.LITERAL);
    tool.setConfigfiles(new ConfigFiles());
    tool.getConfigfiles().getConfigfile().add(file);

    final Tool convertedTool = convert(convert(tool));
    final Command convertedCommand = convertedTool.getCommand();
    assert convertedCommand.getInterpreter().equals("python");
    assert convertedCommand.getValue().equals("moo.py --cow");
    assert ((Param) convertedTool.getInputs().getInputElement().get(0)).getLabel().equals("This is the label");
  }

  @Test(groups = "unit")
  public void testConstructor() {
    new GalaxyXmlUtils();
  }
}
