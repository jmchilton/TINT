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

package edu.umn.msi.tropix.storage.core.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;
import edu.umn.msi.tropix.storage.core.access.AccessProvider;
import edu.umn.msi.tropix.storage.core.authorization.AuthorizationProvider;

public class StorageManagerImplTest {
  private StorageManagerImpl service = null;
  private String gridId = null, fileId = null;

  private MockObjectCollection mockObjects;
  private AccessProvider accessProvider;
  private AuthorizationProvider authorizationProvider;
  private FileService fileService;

  @BeforeMethod(groups = "unit")
  public void init() {
    service = new StorageManagerImpl();
    gridId = UUID.randomUUID().toString();
    fileId = UUID.randomUUID().toString();
    accessProvider = EasyMock.createMock(AccessProvider.class);
    authorizationProvider = EasyMock.createMock(AuthorizationProvider.class);
    fileService = EasyMock.createMock(FileService.class);

    service.setAccessProvider(accessProvider);
    service.setAuthorizationProvider(authorizationProvider);
    service.setFileService(fileService);

    mockObjects = MockObjectCollection.fromObjects(accessProvider, authorizationProvider, fileService);
  }

  @Test(groups = "unit")
  public void canDelete() throws RemoteException {
    EasyMock.expect(authorizationProvider.canDelete(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    assert !service.canDelete(fileId, gridId);
    mockObjects.verifyAndReset();
    EasyMock.expect(authorizationProvider.canDelete(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    assert service.canDelete(fileId, gridId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void canUpload() throws RemoteException {
    EasyMock.expect(authorizationProvider.canUpload(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    assert !service.canUpload(fileId, gridId);
    mockObjects.verifyAndReset();
    EasyMock.expect(authorizationProvider.canUpload(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    assert service.canUpload(fileId, gridId);
    mockObjects.verifyAndReset();
  }

  private void expectCannotDownload() {
    EasyMock.expect(authorizationProvider.canDownload(fileId, gridId)).andReturn(false);
  }

  private void expectCanDownload() {
    EasyMock.expect(authorizationProvider.canDownload(fileId, gridId)).andReturn(true);
  }

  
  @Test(groups = "unit")
  public void canDownload() throws RemoteException {
    expectCannotDownload();
    mockObjects.replay();
    assert !service.canDownload(fileId, gridId);
    mockObjects.verifyAndReset();
    expectCanDownload();
    mockObjects.replay();
    assert service.canDownload(fileId, gridId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void exists() throws RemoteException {
    EasyMock.expect(accessProvider.fileExists(fileId)).andReturn(true);
    mockObjects.replay();
    assert service.exists(fileId);
    mockObjects.verifyAndReset();
    EasyMock.expect(accessProvider.fileExists(fileId)).andReturn(false);
    mockObjects.replay();
    assert !service.exists(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void delete() throws RemoteException {
    EasyMock.expect(authorizationProvider.canDelete(fileId, gridId)).andReturn(true);
    EasyMock.expect(accessProvider.deleteFile(fileId)).andReturn(true);
    mockObjects.replay();
    assert service.delete(fileId, gridId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void deleteAccessException() throws RemoteException {
    EasyMock.expect(authorizationProvider.canDelete(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    assert service.delete(fileId, gridId);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void uploadAccessException() throws RemoteException {
    EasyMock.expect(authorizationProvider.canUpload(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    service.upload(fileId, gridId);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void downloadAccessException() throws RemoteException {
    expectCannotDownload();
    mockObjects.replay();
    service.download(fileId, gridId);
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void lengthAccessException() throws RemoteException {
    expectCannotDownload();
    mockObjects.replay();
    service.getLength(fileId, gridId);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void dateModifiedAccessException() throws RemoteException {
    expectCannotDownload();
    mockObjects.replay();
    service.getDateModified(fileId, gridId);
  }

  @Test(groups = "unit")
  public void dateModified() throws RemoteException {
    expectCanDownload();
    EasyMock.expect(accessProvider.getDateModified(fileId)).andReturn(13L);
    mockObjects.replay();
    assert service.getDateModified(fileId, gridId) == 13L;
  }

  @Test(groups = "unit")
  public void length() throws RemoteException {
    expectCanDownload();
    EasyMock.expect(accessProvider.getLength(fileId)).andReturn(13L);
    mockObjects.replay();
    assert service.getLength(fileId, gridId) == 13L;
  }
  
  @Test(groups = "unit")
  public void download() throws RemoteException {
    final HasStreamInputContext file = EasyMock.createMock(HasStreamInputContext.class);
    expectCanDownload();
    EasyMock.expect(accessProvider.getFile(fileId)).andReturn(file);
    mockObjects.replay();
    assert file.equals(service.download(fileId, gridId));
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void upload(final boolean shouldCommit) throws RemoteException {
    if(shouldCommit) {
      service.setCommittingCallerIds(Lists.newArrayList(gridId));
    }
    EasyMock.expect(authorizationProvider.canUpload(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    final InputStream inputStream = new ByteArrayInputStream("moo".getBytes());
    final UploadCallback callback = service.upload(fileId, gridId);
    mockObjects.verifyAndReset();
    EasyMock.expect(accessProvider.putFile(EasyMock.eq(fileId), EasyMock.same(inputStream))).andReturn(123456L);
    fileService.recordLength(fileId, 123456L);
    if(shouldCommit) {
      fileService.commit(fileId);
    }
    mockObjects.replay();
    callback.onUpload(inputStream);
    mockObjects.verifyAndReset();
  }

}
