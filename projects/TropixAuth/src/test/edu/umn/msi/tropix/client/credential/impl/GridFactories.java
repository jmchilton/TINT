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

package edu.umn.msi.tropix.client.credential.impl;

import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;

import org.testng.annotations.Test;

public class GridFactories {
  
  @Test(groups = "unit")
  public void constructors() {
    new AuthenticationClientFactories();
    new IFSUserClientFactories();
  }
  
  @Test(groups = "unit")
  public void getAuthClient() {
    assert AuthenticationClientFactories.getInstance().getClient("http://url", new Credential()) instanceof AuthenticationClient;
  }
  
  @Test(groups = "unit")
  public void getIfsClient() {
    assert IFSUserClientFactories.getInstance().getClient("http://user") instanceof IFSUserClient;
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void getIfsClientProblems() {
    IFSUserClientFactories.getInstance().getClient("!@#!@#! 341234 51http://user");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void getAuthClientProblems() {
    AuthenticationClientFactories.getInstance().getClient("!@#$@!#$@ http://url", new Credential());
  }
}
