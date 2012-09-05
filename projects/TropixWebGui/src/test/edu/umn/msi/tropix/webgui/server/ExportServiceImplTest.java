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

package edu.umn.msi.tropix.webgui.server;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.galaxy.GalaxyExporter;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.files.MockPersistentModelStorageDataFactoryImpl;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;
import edu.umn.msi.tropix.grid.gridftp.GridFtpFactory;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.webgui.services.object.ExportService.GridFtpServerOptions;

public class ExportServiceImplTest extends BaseGwtServiceTest {

  @Test(groups = "unit", dataProvider = "bool2", dataProviderClass = TestNGDataProviders.class)
  public void testGridFtpExport(final boolean endingSep, final boolean makeDirException) {
    super.init();
    final FileService fileService = EasyMock.createMock(FileService.class);
    final MockPersistentModelStorageDataFactoryImpl storageFactory = new MockPersistentModelStorageDataFactoryImpl();
    final GridFtpFactory gridFtpFactory = EasyMock.createMock(GridFtpFactory.class);

    final ExportServiceImpl service = new ExportServiceImpl(fileService, getUserSession(), storageFactory, gridFtpFactory,
        EasyMock.createMock(GalaxyExporter.class));
    final GridFtpServerOptions options = new GridFtpServerOptions();
    options.setHostname("elmo.msi.umn.edu");
    options.setPort(2811);
    final String path = "/home/john/foo";
    final String inputPath = path + (endingSep ? "/" : "");
    options.setPath(inputPath);
    final String[] ids = new String[] {"id1", "id2"};

    final TropixFile file1 = new TropixFile(), file2 = new TropixFile();
    file1.setId(UUID.randomUUID().toString());
    file2.setId(UUID.randomUUID().toString());
    file1.setStorageServiceUrl(UUID.randomUUID().toString());
    file2.setStorageServiceUrl(UUID.randomUUID().toString());
    file1.setFileId(UUID.randomUUID().toString());
    file2.setFileId(UUID.randomUUID().toString());
    file1.setName("name1");
    file2.setName("name2");
    fileService.getFiles(EasyMock.eq(getUserSession().getGridId()), EasyMock.aryEq(ids));
    EasyMock.expectLastCall().andReturn(new TropixFile[] {file1, file2});
    storageFactory.register(file1);
    storageFactory.register(file2);

    storageFactory.getPersistedStorageData(file1.getId(), getUserSession().getProxy()).getUploadContext().put("contents1".getBytes());
    storageFactory.getPersistedStorageData(file2.getId(), getUserSession().getProxy()).getUploadContext().put("contents2".getBytes());

    final GridFtpClient gridFtpClient = EasyMock.createMock(GridFtpClient.class);
    gridFtpFactory.getClient(options.getHostname(), options.getPort(), getUserSession().getProxy());
    EasyMock.expectLastCall().andReturn(gridFtpClient);
    gridFtpClient.makeDir(path);
    if(makeDirException) {
      EasyMock.expectLastCall().andThrow(new RuntimeException());
    }
    final ByteArrayOutputStream contents1 = new ByteArrayOutputStream();
    final ByteArrayOutputStream contents2 = new ByteArrayOutputStream();
    gridFtpClient.put(EasyMock.eq(path + "/name1"), EasyMockUtils.copy(contents1));
    gridFtpClient.put(EasyMock.eq(path + "/name2"), EasyMockUtils.copy(contents2));
    EasyMock.replay(fileService, gridFtpFactory, gridFtpClient);
    service.export(ids, options);
    EasyMock.verify(fileService, gridFtpFactory, gridFtpClient);
    assert new String(contents1.toByteArray()).equals("contents1");
    assert new String(contents2.toByteArray()).equals("contents2");
  }

}
