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

package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils.ParamVisitor;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

@ManagedBean
class GalaxySupportImpl implements GalaxySupport {
  private final PersistentModelStorageDataFactory persistentModelStorageDataFactory;

  @Inject
  GalaxySupportImpl(final PersistentModelStorageDataFactory persistentModelStorageDataFactory) {
    this.persistentModelStorageDataFactory = persistentModelStorageDataFactory;
  }

  
  public List<GalaxyInputFile> prepareInputFiles(final Tool tool, final RootInput rootInput, final Credential credential) {
    final List<GalaxyInputFile> files = Lists.newLinkedList();
    GalaxyDataUtils.visitParams(tool, rootInput, new ParamVisitor() {
      public void visit(final String key, final Input fileInput, final Param param) {
        final ParamType paramType = param.getType();
        if(paramType != ParamType.DATA) {
          return;
        }
        final String fileId = fileInput.getValue();
        final ModelStorageData data = persistentModelStorageDataFactory.getPersistedStorageData(fileId, credential);
        final String fileName = data.getTropixFile().getName();
        fileInput.setValue(fileName);
        final GalaxyInputFile fileSummary = new GalaxyInputFile(fileName, data.prepareDownloadResource());
        files.add(fileSummary);
      }      
    });
    return files;
  }

}
