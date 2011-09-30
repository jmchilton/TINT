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

package edu.umn.msi.tropix.labs.requests.rdm;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;

public class ModelStorageDataSupplierImplTest {

  @Test(groups = "unit")
  public void get() {
    final ModelStorageDataSupplierImpl supplier = new ModelStorageDataSupplierImpl();
    final ModelStorageDataFactory modelStorageDataFactory = EasyMock.createMock(ModelStorageDataFactory.class);
    final ModelStorageData modelStorageData = EasyMock.createMock(ModelStorageData.class);
    final Credential proxy = Credentials.getMock();
    final Supplier<Credential> proxySupplier = Suppliers.ofInstance(proxy);
    final String storageServiceUrl = "http://moo";

    supplier.setModelStorageDataFactory(modelStorageDataFactory);
    supplier.setProxySupplier(proxySupplier);
    supplier.setStorageServiceUrl(storageServiceUrl);

    EasyMock.expect(modelStorageDataFactory.getStorageData(storageServiceUrl, proxy)).andReturn(modelStorageData);
    EasyMock.replay(modelStorageDataFactory, modelStorageData);

    assert supplier.get() == modelStorageData;
    EasyMock.verify(modelStorageDataFactory, modelStorageData);

  }

}
