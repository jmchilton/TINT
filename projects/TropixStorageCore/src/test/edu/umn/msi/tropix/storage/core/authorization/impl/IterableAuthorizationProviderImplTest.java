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

package edu.umn.msi.tropix.storage.core.authorization.impl;

import java.util.UUID;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class IterableAuthorizationProviderImplTest {

  @Test(groups = "unit")
  public void allPermissions() {
    final String fileId = UUID.randomUUID().toString();
    final String id1 = UUID.randomUUID().toString(), id2 = UUID.randomUUID().toString();
    final IterableAuthorizationProviderImpl provider = new IterableAuthorizationProviderImpl();
    provider.setCanReadIds(Lists.newArrayList(id1));
    provider.setCanDeleteIds(Lists.newArrayList(id1, id2));
    provider.setCanWriteIds(Lists.newArrayList(id2));

    provider.setInPermission(null);
    provider.setOutPermission(false);
    assert null == provider.canDownload(fileId, id1);
    assert null == provider.canDownloadAll(new String[] {fileId}, id1);
    assert !provider.canDownload(fileId, id2);
    assert !provider.canDownloadAll(new String[] {fileId}, id2);
    assert !provider.canUpload(fileId, id1);
    assert null == provider.canUpload(fileId, id2);
    assert null == provider.canDelete(fileId, id1);
    assert null == provider.canDelete(fileId, id2);

    provider.setOutPermission(null);
    provider.setInPermission(true);
    provider.setCanDeleteIds(Lists.<String>newArrayList());
    assert provider.canDownload(fileId, id1);
    assert provider.canDownloadAll(new String[]{fileId}, id1);
    assert null == provider.canDownload(fileId, id2);
    assert null == provider.canUpload(fileId, id1);
    assert provider.canUpload(fileId, id2);
    assert null == provider.canDelete(fileId, id1);
    assert null == provider.canDelete(fileId, id2);

  }
}
