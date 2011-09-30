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

package edu.umn.msi.tropix.labs.catalog;

import java.rmi.RemoteException;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.FullEntry;

public class CatalogUtilsTest {

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void getProviderIdFromServiceIdException() throws RemoteException {
    final CatalogEntryAPI entryApi = EasyMock.createMock(CatalogEntryAPI.class);
    final String serviceId = UUID.randomUUID().toString();
    EasyMock.expect(entryApi.getFullDetails(EasyMock.aryEq(new String[] {serviceId}))).andThrow(new RemoteException("Moo"));
    EasyMock.replay(entryApi);
    CatalogUtils.getProvideIdFromServiceId(entryApi, serviceId);
  }

  @Test(groups = "unit")
  public void getProviderIdFromServiceId() throws RemoteException {
    final CatalogEntryAPI entryApi = EasyMock.createMock(CatalogEntryAPI.class);
    final String serviceId = UUID.randomUUID().toString();
    final FullEntry details = new FullEntry();
    details.setProviderID("PROVIDER");
    EasyMock.expect(entryApi.getFullDetails(EasyMock.aryEq(new String[] {serviceId}))).andReturn(new FullEntry[] {details});
    EasyMock.replay(entryApi);
    assert "PROVIDER".equals(CatalogUtils.getProvideIdFromServiceId(entryApi, serviceId));
    EasyMock.verify(entryApi);
  }
}
