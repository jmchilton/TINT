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
import java.util.UUID;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.files.MockPersistentModelStorageDataFactoryImpl;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Inputs;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.factories.GalaxySupport.GalaxyInputFile;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class GalaxySupportImplTest {

  @Test(groups = "unit")
  public void extractFilesFlat() {
    final Credential credential = Credentials.getMock();

    final Tool tool = new Tool();
    final Param param = new Param();
    param.setType(ParamType.DATA);
    param.setName("file");
    final String id = UUID.randomUUID().toString();
    final Inputs inputs = new Inputs();
    inputs.getInputElement().add(param);
    tool.setInputs(inputs);
    
    final RootInput rootInput = new RootInput();
    final Input input = new Input();
    input.setName("file");
    input.setValue(id);
    rootInput.getInput().add(input);

    
    final MockPersistentModelStorageDataFactoryImpl storageFactory = new MockPersistentModelStorageDataFactoryImpl();
    final TropixFile tropixFile = new TropixFile();
    tropixFile.setId(id);
    tropixFile.setFileId(UUID.randomUUID().toString());
    tropixFile.setStorageServiceUrl(UUID.randomUUID().toString());
    tropixFile.setName("moo.txt");
    
    storageFactory.register(tropixFile);
    final ModelStorageData storage = storageFactory.getStorageData(tropixFile, Credentials.getMock());
    
    final GalaxySupportImpl support = new GalaxySupportImpl(storageFactory);
    final List<GalaxyInputFile> files = support.prepareInputFiles(tool, rootInput, credential);
    assert input.getValue().equals("moo.txt");
    assert files.size() == 1;
    assert files.get(0).getFileName().equals("moo.txt");    
    assert files.get(0).getTransferResource().equals(storage.prepareDownloadResource());
  }
  
}
