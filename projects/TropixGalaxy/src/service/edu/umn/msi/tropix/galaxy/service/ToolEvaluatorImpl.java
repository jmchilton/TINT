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

import javax.annotation.ManagedBean;

import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFiles;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.tool.ConfigFileType;

@ManagedBean
class ToolEvaluatorImpl implements ToolEvaluator {

  private String replaceAndEvaluate(final String templateWithPlaceholders, final Context context) {
    return CheetahTemplateEvaluator.evaluate(templateWithPlaceholders, context);
  }

  public void resolve(final Tool tool, final Context context) {
    final Command command = tool.getCommand();
    command.setValue(replaceAndEvaluate(command.getValue(), context));

    final ConfigFiles configfiles = tool.getConfigfiles();
    if(configfiles != null) {
      for(final ConfigFile configFile : tool.getConfigfiles().getConfigfile()) {
        final ConfigFileType type = configFile.getType();
        if(type == ConfigFileType.TEMPLATE) {
          final String contents = configFile.getValue();
          configFile.setValue(replaceAndEvaluate(contents, context));
        }
      }
    }
  }

}
