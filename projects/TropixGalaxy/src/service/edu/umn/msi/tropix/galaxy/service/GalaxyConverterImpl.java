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

import org.globus.exec.generated.JobDescriptionType;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.Tool;

class GalaxyConverterImpl implements GalaxyConverter {

  private List<String> splitCommandLine(final String line) {
    if(line == null || line.matches("\\s*")) {
      return Lists.newArrayList();
    }
    final String[] array = line.split("\\s+");
    return Lists.newArrayList(array);
  }

  public void populateJobDescription(final JobDescriptionType jobDescription, final Tool tool) {
    final Command command = tool.getCommand();
    Preconditions.checkNotNull(command);
    final String executable;
    final List<String> arguments;
    if(StringUtils.hasText(command.getInterpreter())) {
      // If no interpreter is specified, then all
      executable = command.getInterpreter();
      arguments = splitCommandLine(command.getValue());
    } else {
      final List<String> splitCommandLine = splitCommandLine(command.getValue());
      // Rip executable out of line and leave arguments.
      executable = splitCommandLine.remove(0);
      arguments = splitCommandLine;
    }
    jobDescription.setExecutable(executable);
    jobDescription.setArgument(Iterables.toArray(arguments, String.class));
  }

}
