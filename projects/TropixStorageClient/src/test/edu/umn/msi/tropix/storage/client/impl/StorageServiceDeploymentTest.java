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

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.transfer.impl.TransferContextFactoryImpl;
import edu.umn.msi.tropix.storage.client.StorageData;

public class StorageServiceDeploymentTest {

  @Test(groups="deployment")
  public void test() throws GlobusCredentialException {
    TropixFileFactoryGridImpl impl = new TropixFileFactoryGridImpl();
    impl.setTransferContextFactory(new TransferContextFactoryImpl());
    ModelStorageDataFactoryImpl factory = new ModelStorageDataFactoryImpl();
    factory.setTropixFileFactory(impl);
    StorageData storageData = factory.getStorageData("https://localhost:8443/caGridTransfer/services/cagrid/TropixStorage", Credentials.get(new GlobusCredential("/home/john/chilton2.msi.umn.edu-cert.pem", "/home/john/chilton2.msi.umn.edu-key.pem")));
    storageData.getUploadContext().put("Moo Cow".getBytes());
    storageData.getDownloadContext().get(FileUtilsFactory.getInstance().createTempFile());
  }
}
