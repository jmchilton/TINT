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

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class ModelStorageDataFactoryImplTest {

  @Test(groups = "unit")
  public void get() {
    final ModelStorageDataFactoryImpl factory = new ModelStorageDataFactoryImpl();
    final TropixFileFactory tfFactory = EasyMock.createMock(TropixFileFactory.class);
    factory.setTropixFileFactory(tfFactory);
    final Credential proxy = EasyMock.createMock(Credential.class);

    TropixFile tropixFile = new TropixFile();
    ModelStorageData mds = EasyMock.createMock(ModelStorageData.class);
    final String serviceUrl = "http://storage";
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("storageServiceUrl", serviceUrl);
    tfFactory.getStorageData(EasyMockUtils.<TropixFile>isBeanWithProperties(map), EasyMock.same(proxy));
    EasyMock.expectLastCall().andReturn(mds);

    EasyMock.replay(tfFactory);
    assert mds == factory.getStorageData(serviceUrl, proxy);
    EasyMockUtils.verifyAndReset(tfFactory);

    tropixFile = new TropixFile();
    mds = EasyMock.createMock(ModelStorageData.class);
    map.put("fileId", "12345");
    tfFactory.getStorageData(EasyMockUtils.<TropixFile>isBeanWithProperties(map), EasyMock.same(proxy));
    EasyMock.expectLastCall().andReturn(mds);

    EasyMock.replay(tfFactory);
    assert mds == factory.getStorageData("12345", serviceUrl, proxy);
    EasyMockUtils.verifyAndReset(tfFactory);

    tropixFile = new TropixFile();
    mds = EasyMock.createMock(ModelStorageData.class);
    tfFactory.getStorageData(EasyMock.same(tropixFile), EasyMock.same(proxy));
    EasyMock.expectLastCall().andReturn(mds);
    EasyMock.replay(tfFactory);
    assert mds == factory.getStorageData(tropixFile, proxy);
    EasyMockUtils.verifyAndReset(tfFactory);
  }
}
