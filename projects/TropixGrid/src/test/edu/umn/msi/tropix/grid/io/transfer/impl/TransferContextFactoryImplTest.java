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

package edu.umn.msi.tropix.grid.io.transfer.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.easymock.EasyMock;
import org.globus.gsi.GlobusCredential;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.transfer.TransferUtils;

public class TransferContextFactoryImplTest {
  private TransferContextFactoryImpl factory;
  private TransferUtils utils;

  @BeforeMethod(groups = "unit")
  public void init() {
    factory = new TransferContextFactoryImpl();
    utils = EasyMock.createMock(TransferUtils.class);
    factory.setTransferUtils(utils);
  }

  enum UploadType {
    STREAM, FILE, BYTES, EXCEPTION
  };

  enum DownloadType {
    FILE, STREAM, EXCEPTION
  };

  @Test(groups = "unit")
  public void uploadStream() throws Exception {
    upload(UploadType.STREAM);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void uploadException() throws Exception {
    upload(UploadType.EXCEPTION);
  }

  @Test(groups = "unit")
  public void uploadFile() throws Exception {
    upload(UploadType.FILE);
  }

  @Test(groups = "unit")
  public void uploadBytes() throws Exception {
    upload(UploadType.BYTES);
  }

  @Test(groups = "unit")
  public void downloadFile() throws Exception {
    download(DownloadType.FILE);
  }

  @Test(groups = "unit")
  public void downloadStream() throws Exception {
    download(DownloadType.STREAM);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void downloadException() throws Exception {
    download(DownloadType.EXCEPTION);
  }

  public void download(final DownloadType type) throws Exception {
    final TransferServiceContextReference ref = new TransferServiceContextReference();
    final GlobusCredential proxy = EasyMock.createMock(GlobusCredential.class);
    final Credential credential = Credentials.get(proxy);
    final File file = File.createTempFile("tpx", "");
    file.deleteOnExit();
    EasyMock.expect(utils.get(EasyMock.same(ref), EasyMock.same(proxy)));
    if(type != DownloadType.EXCEPTION) {
      EasyMock.expectLastCall().andReturn(new ByteArrayInputStream("Hello".getBytes()));
    } else {
      EasyMock.expectLastCall().andThrow(new Exception());
    }
    EasyMock.replay(utils);
    final InputContext downloadContext = factory.getDownloadContext(ref, credential);
    if(type.equals(DownloadType.FILE) || type == DownloadType.EXCEPTION) {
      downloadContext.get(file);
    } else if(type.equals(DownloadType.STREAM)) {
      downloadContext.get(new FileOutputStream(file));
    }
    assert FileUtils.readFileToString(file).equals("Hello");
  }

  public void upload(final UploadType type) throws Exception {
    final TransferServiceContextReference ref = new TransferServiceContextReference();
    final GlobusCredential proxy = EasyMock.createMock(GlobusCredential.class);
    final Credential credential = Credentials.get(proxy);
    final OutputContext uc = factory.getUploadContext(ref, credential);
    final File file = File.createTempFile("tpx", "");
    file.deleteOnExit();
    FileUtils.writeStringToFile(file, "Moo!");
    final ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    utils.put(EasyMockUtils.copy(bStream), EasyMock.same(ref), EasyMock.same(proxy));
    if(type.equals(UploadType.EXCEPTION)) {
      EasyMock.expectLastCall().andThrow(new Exception());
    }
    EasyMock.replay(utils);
    if(type.equals(UploadType.FILE) || type.equals(UploadType.EXCEPTION)) {
      uc.put(file);
    } else if(type.equals(UploadType.STREAM)) {
      uc.put(new FileInputStream(file));
    } else if(type.equals(UploadType.BYTES)) {
      uc.put(FileUtils.readFileToByteArray(file));
    }
    EasyMock.verify(utils);
    assert "Moo!".equals(new String(bStream.toByteArray()));
  }

}
