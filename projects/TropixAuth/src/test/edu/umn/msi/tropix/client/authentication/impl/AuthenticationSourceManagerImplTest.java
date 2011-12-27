/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.client.authentication.impl;

import java.io.File;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.config.CaGrid;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class AuthenticationSourceManagerImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  @Test(groups = "unit")
  public void getKeys() {
    final File configFile = FILE_UTILS.createTempFile();
    try {
      final AuthenticationSourceManagerImpl manager = new AuthenticationSourceManagerImpl();
      FILE_UTILS.writeStringToFile(configFile, "<authenticationSources xmlns=\"http://msi.umn.edu/tropix/client/authentication/config\"><local /><caGrid title=\"Test Title\" authenticationServiceUrl=\"auth\" dorianServiceUrl=\"http://dorian.com/\" /></authenticationSources>");
      manager.setAuthenticationSourcesFile(configFile);
      assert Iterables.elementsEqual(manager.getAuthenticationSourceKeys(), Lists.newArrayList("Local", "Test Title"));
      FILE_UTILS.writeStringToFile(configFile, "<authenticationSources xmlns=\"http://msi.umn.edu/tropix/client/authentication/config\"><caGrid title=\"Test Title\" authenticationServiceUrl=\"auth\" dorianServiceUrl=\"http://dorian.com/\" /><local /></authenticationSources>");
      manager.setAuthenticationSourcesFile(configFile);
      assert Iterables.elementsEqual(manager.getAuthenticationSourceKeys(), Lists.newArrayList("Test Title", "Local"));
    } finally {
      FILE_UTILS.deleteQuietly(configFile);
    }
  }

  @Test(groups = "unit")
  public void getOptions() {
    final File configFile = FILE_UTILS.createTempFile();
    try {
      final AuthenticationSourceManagerImpl manager = new AuthenticationSourceManagerImpl();
      FILE_UTILS.writeStringToFile(configFile, "<authenticationSources xmlns=\"http://msi.umn.edu/tropix/client/authentication/config\"><local /><caGrid title=\"Test Title\" authenticationServiceUrl=\"auth\" dorianServiceUrl=\"http://dorian.com/\" /></authenticationSources>");
      manager.setAuthenticationSourcesFile(configFile);
      final CredentialCreationOptions options = manager.getAuthenticationOptions("Local");
      //assert options.getAuthenicationSource(CaGrid.class).getIdpUrl().equals("local");
      //assert options.getIfsUrl().equals("local");
      final CredentialCreationOptions gridOptions = manager.getAuthenticationOptions("Test Title");
      assert gridOptions.getAuthenicationSource(CaGrid.class).getAuthenticationServiceUrl().equals("auth");
      assert gridOptions.getAuthenicationSource(CaGrid.class).getDorianServiceUrl().equals("http://dorian.com/");      
    } finally {
      FILE_UTILS.deleteQuietly(configFile);
    }
  }
  
  
  
}
