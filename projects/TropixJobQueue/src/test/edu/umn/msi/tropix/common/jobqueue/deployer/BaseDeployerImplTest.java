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

package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;

import org.testng.annotations.Test;


// TODO: Write tests in this class
public class BaseDeployerImplTest extends DeployerTest {

  static class OpenBaseDeployerImpl extends BaseDeployerImpl {

    @Override
    protected void deploy() {
    }
            
  }
  
  @Test(groups = "unit")
  public void testCopyWithReplacements() {
    final OpenBaseDeployerImpl deployer = new OpenBaseDeployerImpl();
    initializeDeployer(deployer);
    
    deployer.copyResourceWithReplacements("test.sh", deployer.newReplacements().put("FOO", "C:\\foo\\"));
    assert super.getContents("test.sh").equals("C:\\foo\\moo -i input.txt");
  }
  
  @Test(groups = "unit")
  public void testAsDirectoryPath() {
    final OpenBaseDeployerImpl deployer = new OpenBaseDeployerImpl();
    assert deployer.asDirectoryPath("/home/john" + File.separator).equals("/home/john" + File.separator);
    assert deployer.asDirectoryPath("/home/john").equals("/home/john" + File.separator);
    assert deployer.asDirectoryPath((String) null).equals("");
    assert deployer.asDirectoryPath("").equals("");
  }
  
  @Test(groups = "unit")
  public void testGetDeploymentDirectory() {
    final OpenBaseDeployerImpl deployer = new OpenBaseDeployerImpl();
    deployer.setDeploymentDirectory(new File("/home/john"));
    assert deployer.getDeploymentDirectory().equals(new File("/home/john"));
  }
    
}
