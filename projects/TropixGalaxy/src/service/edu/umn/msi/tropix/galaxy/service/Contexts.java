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

import java.util.Map;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.common.io.Directory;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils.ParamVisitor;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Tool;

class Contexts {
    
  static Context expandPathsAndBuildContext(final Tool tool, 
                                            final RootInput rootInput, 
                                            final Directory stagingDirectory) {
    GalaxyDataUtils.visitParams(tool, rootInput, new ParamVisitor() {
      public void visit(final String key, final Input fileInput, final Param param) {
        if(param.getType() == ParamType.DATA) {
          fileInput.setValue(Directories.buildAbsolutePath(stagingDirectory, fileInput.getValue()));
        }
      }
    });
    
    final Map<String, String> outputMap = Maps.newHashMap();
    for(final Data output : tool.getOutputs().getData()) {
      outputMap.put(output.getName(), Directories.buildAbsolutePath(stagingDirectory, output.getName()));
    }
        
    return new ContextBuilder().buildContext(tool, rootInput, outputMap);
    
  }

}
