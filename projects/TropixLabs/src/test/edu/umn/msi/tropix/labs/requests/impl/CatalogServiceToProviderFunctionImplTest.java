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

package edu.umn.msi.tropix.labs.requests.impl;

import java.rmi.RemoteException;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.FullEntry;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;

public class CatalogServiceToProviderFunctionImplTest {

  @Test(groups = "unit")
  public void apply() throws RemoteException {
    final CatalogEntryAPI entryApi = EasyMock.createMock(CatalogEntryAPI.class);
    final CatalogInstance instance = EasyMock.createMock(CatalogInstance.class);
    final CatalogServiceToProviderFunctionImpl f = new CatalogServiceToProviderFunctionImpl();
    f.setCatalogInstance(instance);
    EasyMock.expect(instance.getCatalogEntryAPI()).andReturn(entryApi);
    final String serviceId = UUID.randomUUID().toString();
    final FullEntry details = new FullEntry();
    details.setProviderID("PROVIDER");
    EasyMock.expect(entryApi.getFullDetails(EasyMock.aryEq(new String[] {serviceId}))).andReturn(new FullEntry[] {details});
    EasyMock.replay(entryApi, instance);
    assert "PROVIDER".equals(f.apply(serviceId));
    EasyMock.verify(entryApi, instance);

  }
}
