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

package edu.umn.msi.tropix.storage.client.impl;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;

public class CredentialBoundModelStorageDataFactoryImplTest {
  private ModelStorageData lastData = EasyMock.createMock(ModelStorageData.class);
  
  private void exepctAndReturnLastData() {
    EasyMock.expectLastCall().andReturn(lastData);
  }
  
  @Test(groups = "unit")
  public void testCredentialBoundProperly() {
    final Credential credential = Credentials.getMock();
    final ModelStorageDataFactory dataFactory = EasyMock.createMock(ModelStorageDataFactory.class);
    
    final CredentialBoundModelStorageDataFactoryImpl cbFactory = new CredentialBoundModelStorageDataFactoryImpl();
    cbFactory.setCredentialSupplier(Suppliers.ofInstance(credential));
    cbFactory.setModelStorageDataFactory(dataFactory);
    
    dataFactory.getStorageData("service", credential);
    exepctAndReturnLastData();
    EasyMock.replay(dataFactory);
    assert lastData == cbFactory.getStorageData("service");    
    EasyMockUtils.verifyAndReset(dataFactory);

    dataFactory.getStorageData("id", "service", credential);
    exepctAndReturnLastData();
    EasyMock.replay(dataFactory);
    assert lastData == cbFactory.getStorageData("id", "service");
    EasyMockUtils.verifyAndReset(dataFactory);

    final TropixFile file = new TropixFile();
    file.setId(UUID.randomUUID().toString());
    dataFactory.getStorageData(file, credential);
    exepctAndReturnLastData();
    EasyMock.replay(dataFactory);
    assert lastData == cbFactory.getStorageData(file);
    EasyMockUtils.verifyAndReset(dataFactory);
    
  }
}
