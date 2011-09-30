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

package edu.umn.msi.tropix.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.UUID;

import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;
import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.io.FileContext;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;

public class TropixStorageServiceImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private TropixStorageServiceImpl service = null;
  private String gridId = null, fileId = null;
  private TransferServiceContextReferenceFactory tscrFactory = null;
  private StorageManager storageManager = null;

  private MockObjectCollection mockObjects;

  @BeforeMethod(groups = "unit")
  public void init() {
    service = new TropixStorageServiceImpl();
    gridId = UUID.randomUUID().toString();
    fileId = UUID.randomUUID().toString();
    service.setIdentitySupplier(Suppliers.ofInstance(gridId));
    tscrFactory = EasyMock.createMock(TransferServiceContextReferenceFactory.class);
    storageManager = EasyMock.createMock(StorageManager.class);

    service.setTscrFactory(tscrFactory);
    service.setStorageManager(storageManager);
    mockObjects = MockObjectCollection.fromObjects(storageManager, tscrFactory);
  }

  @Test(groups = "unit")
  public void canDelete() throws RemoteException {
    EasyMock.expect(storageManager.canDelete(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    assert !service.canDelete(fileId);
    mockObjects.verifyAndReset();
    EasyMock.expect(storageManager.canDelete(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    assert service.canDelete(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void canUpload() throws RemoteException {
    EasyMock.expect(storageManager.canUpload(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    assert !service.canUpload(fileId);
    mockObjects.verifyAndReset();
    EasyMock.expect(storageManager.canUpload(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    assert service.canUpload(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void canDownload() throws RemoteException {
    EasyMock.expect(storageManager.canDownload(fileId, gridId)).andReturn(false);
    mockObjects.replay();
    assert !service.canDownload(fileId);
    mockObjects.verifyAndReset();
    EasyMock.expect(storageManager.canDownload(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    assert service.canDownload(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void exists() throws RemoteException {
    EasyMock.expect(storageManager.exists(fileId)).andReturn(true);
    mockObjects.replay();
    assert service.exists(fileId);
    mockObjects.verifyAndReset();
    EasyMock.expect(storageManager.exists(fileId)).andReturn(false);
    mockObjects.replay();
    assert !service.exists(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void delete() throws RemoteException {
    EasyMock.expect(storageManager.delete(fileId, gridId)).andReturn(true);
    mockObjects.replay();
    assert service.delete(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void downloadPrefersFile() throws RemoteException {
    final File file = new File("moo");
    EasyMock.expect(storageManager.download(fileId, gridId)).andReturn(new FileContext(file));
    final TransferServiceContextReference tscReference = new TransferServiceContextReference();
    EasyMock.expect(tscrFactory.createTransferContext(EasyMock.same(file), EasyMock.isA(DataDescriptor.class), EasyMock.eq(false))).andReturn(tscReference);
    mockObjects.replay();
    assert tscReference == service.prepareDownload(fileId);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void downloadStream() throws RemoteException {
    EasyMock.expect(storageManager.download(fileId, gridId)).andReturn(InputContexts.forByteArray("test".getBytes()));
    final TransferServiceContextReference tscReference = new TransferServiceContextReference();
    EasyMock.expect(tscrFactory.createTransferContext(EasyMockUtils.inputStreamWithContents("test".getBytes()), EasyMock.isA(DataDescriptor.class))).andReturn(tscReference);
    mockObjects.replay();
    assert tscReference == service.prepareDownload(fileId);
    mockObjects.verifyAndReset();
  }
  
  
  @Test(groups = "unit")
  public void upload() throws RemoteException {
    final UploadCallback callback = EasyMock.createMock(UploadCallback.class);
    final File tempFile = FILE_UTILS.createTempFile("tpxtst", "");
    try {
      FILE_UTILS.writeStringToFile(tempFile, "moo");
      mockObjects.add(callback);
      EasyMock.expect(storageManager.upload(fileId, gridId)).andReturn(callback);
      final TransferServiceContextReference tscReference = new TransferServiceContextReference();
      final Capture<DataStagedCallback> callbackCapture = new Capture<DataStagedCallback>();
      EasyMock.expect(tscrFactory.createTransferContext(EasyMock.isA(DataDescriptor.class), EasyMock.capture(callbackCapture))).andReturn(tscReference);
      mockObjects.replay();
      assert tscReference == service.prepareUpload(fileId);
      mockObjects.verifyAndReset();

      final DataStagedCallback dCallback = callbackCapture.getValue();
      final TransferServiceContextResource resource = EasyMock.createMock(TransferServiceContextResource.class);
      final DataStorageDescriptor dd = new DataStorageDescriptor();
      EasyMock.expect(resource.getDataStorageDescriptor()).andReturn(dd);

      dd.setLocation(tempFile.getAbsolutePath());
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      callback.onUpload(EasyMockUtils.copy(outputStream));
      EasyMock.replay(resource);
      mockObjects.replay();
      dCallback.dataStaged(resource);
      mockObjects.verifyAndReset();
      EasyMock.verify(resource);
      assert new String(outputStream.toByteArray()).equals("moo");
    } finally {
      tempFile.delete();
    }
  }

  @Test(groups = "unit")
  public void uploadPutFileException() throws RemoteException {
    final UploadCallback callback = EasyMock.createMock(UploadCallback.class);
    final File tempFile = FILE_UTILS.createTempFile("tpxtst", "");
    try {
      mockObjects.add(callback);
      EasyMock.expect(storageManager.upload(fileId, gridId)).andReturn(callback);
      final TransferServiceContextReference tscReference = new TransferServiceContextReference();
      final Capture<DataStagedCallback> callbackCapture = new Capture<DataStagedCallback>();
      EasyMock.expect(tscrFactory.createTransferContext(EasyMock.isA(DataDescriptor.class), EasyMock.capture(callbackCapture))).andReturn(tscReference);
      mockObjects.replay();
      assert tscReference == service.prepareUpload(fileId);
      mockObjects.verifyAndReset();

      final DataStagedCallback dCallback = callbackCapture.getValue();
      final TransferServiceContextResource resource = EasyMock.createMock(TransferServiceContextResource.class);
      final DataStorageDescriptor dd = new DataStorageDescriptor();
      dd.setLocation(tempFile.getAbsolutePath());
      EasyMock.expect(resource.getDataStorageDescriptor()).andReturn(dd);
      dd.setLocation(tempFile.getAbsolutePath());
      callback.onUpload(EasyMock.isA(InputStream.class));
      EasyMock.expectLastCall().andThrow(new RuntimeException());
      EasyMock.replay(resource);
      mockObjects.replay();
      dCallback.dataStaged(resource);
      mockObjects.verifyAndReset();
      EasyMock.verify(resource);
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }
}
