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

package edu.umn.msi.tropix.grid.impl;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.easymock.EasyMock;
import org.globus.gsi.GlobusCredential;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.grid.EprUtils;
import edu.umn.msi.tropix.grid.GridServiceFactories;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class GridServiceFactoryImplTest {

  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testGet(final boolean nullCredential) {
    final GridServiceFactory<TestClient> factory = GridServiceFactories.getFactoryForClientClass(TestClient.class);
    final GlobusCredential globusCredential = EasyMock.createMock(GlobusCredential.class);
    final Credential cred = nullCredential ? null : Credentials.get(globusCredential);
    final TestClient client = factory.getService("hello", cred);
    assert client.getAddress().equals("hello"); 
    if(nullCredential) {
      assert client.getCredential() == null;
    } else {
      assert client.getCredential() == globusCredential;
    }
  }

  @Test(groups = "unit")
  public void testGetEpr() {
    final GridServiceFactory<TestClient> factory = GridServiceFactories.getFactoryForClientClass(TestClient.class);
    final Credential cred = Credentials.getMock();
    final EndpointReferenceType epr = EprUtils.getEprForAddress("http://moo");
    final TestClient client = factory.getService(epr, cred);
    assert client.getEpr().equals(epr);
  }

}
