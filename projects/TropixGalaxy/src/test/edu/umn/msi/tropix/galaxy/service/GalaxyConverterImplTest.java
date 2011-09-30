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

import java.util.Arrays;

import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class GalaxyConverterImplTest {

  @Test(groups = "unit")
  public void testSimpleCommand() {
    final Tool tool = new Tool();
    final Command command = new Command();
    command.setValue("/bin/test/moo arg1 arg2");
    tool.setCommand(command);

    final GalaxyConverterImpl converter = new GalaxyConverterImpl();
    final JobDescriptionType jobDescription = new JobDescriptionType();
    converter.populateJobDescription(jobDescription, tool);
    jobDescription.getExecutable().equals("/bin/test/moo");
    assert Arrays.equals(jobDescription.getArgument(), new String[] {"arg1", "arg2"}) : Arrays.toString(jobDescription.getArgument());
  }

  @Test(groups = "unit")
  public void testSimpleInterpretedCommand() {
    final Tool tool = new Tool();
    final Command command = new Command();
    command.setInterpreter("python");
    command.setValue("/bin/test/moo arg1 arg2");
    tool.setCommand(command);

    final GalaxyConverterImpl converter = new GalaxyConverterImpl();
    final JobDescriptionType jobDescription = new JobDescriptionType();
    converter.populateJobDescription(jobDescription, tool);
    jobDescription.getExecutable().equals("python");
    assert Arrays.equals(jobDescription.getArgument(), new String[] {"/bin/test/moo", "arg1", "arg2"});
  }

}
