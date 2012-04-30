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

import javax.annotation.Nullable;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.persistence.service.FileService;

public class FileServiceAuthorizationProviderImplTest {
  private FileService fileService = null;
  private FileServiceAuthorizationProviderImpl provider = null;
  private String id = null, id2 = null, caller = null;

  private static final Boolean[] ANSWERS = new Boolean[] {true, false};

  @BeforeMethod(groups = "unit")
  public void init() {
    provider = new FileServiceAuthorizationProviderImpl();
    fileService = EasyMock.createMock(FileService.class);
    provider.setFileService(fileService);
    id = UUID.randomUUID().toString();
    id2 = UUID.randomUUID().toString();
    caller = UUID.randomUUID().toString();
  }

  @Test(groups = "unit")
  public void fileServiceOneDoesntExist() {
    expectIdExists(id);
    expectIdDoesntExist(id2);

    EasyMock.expect(fileService.filesExistAndCanReadAll(EasyMock.aryEq(new String[] {id, id2}), EasyMock.eq(caller))).andReturn(false);
    // EasyMock.expect(fileService.filesExist(EasyMock.aryEq(new String[]{id, id2}))).andReturn(false);
    EasyMock.expect(fileService.canReadFile(caller, id)).andStubReturn(true);
    replay();
    assert equalBooleans(null, provider.canDownloadAll(new String[] {id, id2}, caller));
    verify();
  }

  @Test(groups = "unit")
  public void fileServiceCanDownloadAll() {
    EasyMock.expect(fileService.filesExistAndCanReadAll(EasyMock.aryEq(new String[] {id, id2}), EasyMock.eq(caller))).andStubReturn(true);
    // EasyMock.expect(fileService.canReadAll(EasyMock.eq(caller), EasyMock.aryEq(new String[] {id, id2}))).andStubReturn(answer);
    replay();
    assert equalBooleans(true, provider.canDownloadAll(new String[] {id, id2}, caller));
    verify();
  }

  @Test(groups = "unit")
  public void fileServiceCanRead() {
    expectIdDoesntExist();
    replay();
    assert null == provider.canDownload(id, caller);
    verify();

    for(final Boolean answer : ANSWERS) {
      expectIdExists();
      EasyMock.expect(fileService.canReadFile(caller, id)).andReturn(answer);
      replay();
      assert equalBooleans(answer, provider.canDownload(id, caller));
      verify();
    }
  }

  private void verify() {
    EasyMockUtils.verifyAndReset(fileService);
  }

  private void expectIdDoesntExist(final String id) {
    EasyMock.expect(fileService.fileExists(id)).andReturn(false);
  }

  private void expectIdDoesntExist() {
    expectIdDoesntExist(id);
  }

  @Test(groups = "unit")
  public void fileServiceCanDelete() {
    expectIdDoesntExist();
    replay();
    assert null == provider.canDelete(id, caller);
    verify();
    for(final Boolean answer : ANSWERS) {
      expectIdExists();
      EasyMock.expect(fileService.canDeleteFile(caller, id)).andReturn(answer);
      replay();
      assert equalBooleans(answer, provider.canDelete(id, caller));
      verify();
    }
  }

  private void expectIdExists(final String id) {
    EasyMock.expect(fileService.fileExists(id)).andStubReturn(true);
  }

  private void expectIdExists() {
    expectIdExists(id);
  }

  private static boolean equalBooleans(@Nullable final Boolean bool1, @Nullable final Boolean bool2) {
    return bool1 == null ? null == bool2 : bool1.equals(bool2);
  }

  @Test(groups = "unit")
  public void fileServiceCanWrite() {
    expectIdDoesntExist();
    replay();
    assert null == provider.canUpload(id, caller);
    verify();

    for(final Boolean answer : ANSWERS) {
      expectIdExists();
      EasyMock.expect(fileService.canWriteFile(caller, id)).andReturn(answer);
      replay();
      assert equalBooleans(answer, provider.canUpload(id, caller));
      verify();
    }
  }

  private void replay() {
    EasyMock.replay(fileService);
  }

}
