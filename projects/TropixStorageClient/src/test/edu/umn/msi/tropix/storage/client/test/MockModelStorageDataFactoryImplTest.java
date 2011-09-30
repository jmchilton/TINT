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

package edu.umn.msi.tropix.storage.client.test;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class MockModelStorageDataFactoryImplTest {
  private MockModelStorageDataFactoryImpl dataFactoryImpl;
  
  private String serviceUrl;
  private Credential credential;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    dataFactoryImpl = new MockModelStorageDataFactoryImpl();
    
    serviceUrl = UUID.randomUUID().toString();
    credential = Credentials.getMock();
  }
  
  private void testStorage(final ModelStorageData modelStorageData) {
    modelStorageData.getUploadContext().put("Hello World!".getBytes());
    assert "Hello World!".equals(InputContexts.toString(modelStorageData.getDownloadContext()));    
  }

  @Test(groups = "unit")
  public void testTransferResources() {
    final ModelStorageData modelStorageData = dataFactoryImpl.getStorageData(serviceUrl, credential);
    
    assert modelStorageData.prepareDownloadResource() != null;
    assert modelStorageData.prepareUploadResource() != null;
    
  }
  
  @Test(groups = "unit")
  public void testEquals() {
    final ModelStorageData modelStorageData = dataFactoryImpl.getStorageData(serviceUrl, credential);
    final ModelStorageData equalModelStorageData = dataFactoryImpl.getStorageData(modelStorageData.getDataIdentifier(), serviceUrl, credential);
    final ModelStorageData notEqualStorageData = dataFactoryImpl.getStorageData(serviceUrl, credential);

    assert !modelStorageData.equals(notEqualStorageData);
    assert modelStorageData.equals(equalModelStorageData);
    assert !modelStorageData.equals(new Object());
  }
  
  @Test(groups = "unit")
  public void testGetWithId() {
    final String newId = UUID.randomUUID().toString();
    final ModelStorageData modelStorageData = dataFactoryImpl.getStorageData(newId, serviceUrl, credential);
    assert modelStorageData.getDataIdentifier().equals(newId);
    assert modelStorageData.getTropixFile().getFileId().equals(newId);
    assert modelStorageData.getTropixFile().getId() != null;

    testStorage(modelStorageData);    
  }
  
  @Test(groups = "unit")
  public void testNewId() {
    final ModelStorageData modelStorageData = dataFactoryImpl.getStorageData(serviceUrl, credential);
    assert modelStorageData.getTropixFile().getFileId().equals(modelStorageData.getDataIdentifier());
    
    assert modelStorageData == dataFactoryImpl.getNewlyIssuedStorageDataObject();
    assert Iterables.elementsEqual(Lists.newArrayList(modelStorageData), dataFactoryImpl.getNewlyIssuedStorageDataObjects());

    testStorage(modelStorageData);
  }
  
  @Test(groups = "unit")
  public void testGetFromFile() {
    final TropixFile file = new TropixFile();
    file.setStorageServiceUrl(serviceUrl);
    file.setFileId(UUID.randomUUID().toString());
    final ModelStorageData modelStorageData = dataFactoryImpl.getStorageData(file, credential);
    
    assert modelStorageData.getDataIdentifier().equals(file.getFileId());

    testStorage(modelStorageData);
  }
  
  
}
