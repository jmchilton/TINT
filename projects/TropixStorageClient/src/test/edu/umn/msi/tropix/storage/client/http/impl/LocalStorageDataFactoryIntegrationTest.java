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

package edu.umn.msi.tropix.storage.client.http.impl;

import java.io.ByteArrayOutputStream;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClients;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/storage/client/context.xml")
public class LocalStorageDataFactoryIntegrationTest extends FreshConfigTest {

  @Resource
  private ModelStorageDataFactory modelStorageDataFactory;

  @Test(groups = "spring")
  public void transferOps() {
    final StorageData storageData = modelStorageDataFactory.getStorageData("moo", Credentials.getMock("moo"));
    final HttpTransferResource resource = (HttpTransferResource) storageData.prepareUploadResource();
    final String url = resource.getUrl();
    HttpTransferClients.getInstance().getOutputContext(url).put("Moo Cow".getBytes());

    final HttpTransferResource downloadResource = (HttpTransferResource) storageData.prepareDownloadResource();
    final String dUrl = downloadResource.getUrl();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    HttpTransferClients.getInstance().getInputContext(dUrl).get(outputStream);
    assert new String(outputStream.toByteArray()).equals("Moo Cow");
  }
  
  @Test(groups = "spring")
  public void testContexts() {
    final StorageData storageData = modelStorageDataFactory.getStorageData("moo", Credentials.getMock("moo"));
    storageData.getUploadContext().put("Hello World!".getBytes());
    InputContexts.toString(storageData.getDownloadContext()).equals("Hello World!");
  }
  
  @Test(groups = "spring")
  public void testTropixFile() {
    final ModelStorageData storageData = modelStorageDataFactory.getStorageData("moo", Credentials.getMock("moo"));
    storageData.getDataIdentifier().equals(storageData.getTropixFile().getFileId());
  }

}
