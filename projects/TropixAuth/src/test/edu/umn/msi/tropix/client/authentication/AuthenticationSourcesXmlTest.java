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

package edu.umn.msi.tropix.client.authentication;

import java.io.StringReader;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.authentication.config.AuthenticationSources;
import edu.umn.msi.tropix.client.authentication.config.CaGrid;
import edu.umn.msi.tropix.client.authentication.config.Local;
import edu.umn.msi.tropix.common.xml.XMLUtility;

public class AuthenticationSourcesXmlTest {

  @Test(groups = "unit") 
  public void show() {
    final XMLUtility<AuthenticationSources> utility = new XMLUtility<AuthenticationSources>(AuthenticationSources.class);
    
    final AuthenticationSources sources = new AuthenticationSources();
    sources.getAuthenticationSource().add(new Local());
    
    final AuthenticationSources sources2 = utility.deserialize(new StringReader("<authenticationSources xmlns=\"http://msi.umn.edu/tropix/client/authentication/config\"><local /><caGrid dorianServiceUrl=\"http://dorian.com/\" /></authenticationSources>"));
    assert sources2.getAuthenticationSource().get(0) instanceof Local; 
    final CaGrid caGrid = (CaGrid) sources2.getAuthenticationSource().get(1);
    assert caGrid.getDorianServiceUrl().equals("http://dorian.com/");
  }
  
}

