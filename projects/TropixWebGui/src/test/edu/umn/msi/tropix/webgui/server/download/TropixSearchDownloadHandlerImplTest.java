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

package edu.umn.msi.tropix.webgui.server.download;

import info.minnesotapartnership.tropix.search.TropixSearchService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.DownloadContextFactory;
import edu.umn.msi.tropix.webgui.server.BaseGwtServiceTest;

public class TropixSearchDownloadHandlerImplTest extends BaseGwtServiceTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private TropixSearchDownloadHandlerImpl handler;
  private ByteArrayOutputStream outputStream;
  private GridServiceFactory<TropixSearchService> serviceFactory;
  private List<TropixSearchService> searchServices;
  private DownloadContextFactory<Credential> downloadContextFactory;
  private Map<String, String> params;

  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    searchServices = Lists.newArrayList();
    downloadContextFactory = EasyMock.createMock(DownloadContextFactory.class);
    serviceFactory = EasyMock.createMock(GridServiceFactory.class);
    handler = new TropixSearchDownloadHandlerImpl(downloadContextFactory, serviceFactory, getUserSession());
    outputStream = new ByteArrayOutputStream();
    params = Maps.newHashMap();
  }

  private TropixSearchService expectGetService(final String serviceUrl) {
    final TropixSearchService service = EasyMock.createMock(TropixSearchService.class);
    searchServices.add(service);
    EasyMock.expect(serviceFactory.getService(serviceUrl, getUserSession().getProxy())).andStubReturn(service);
    return service;
  }

  private void download() {
    EasyMock.replay(downloadContextFactory, serviceFactory);
    EasyMockUtils.replayAll(searchServices);
    handler.processDownloadRequest(outputStream, Functions.forMap(params));
    EasyMockUtils.verifyAndReset(downloadContextFactory, serviceFactory);
    EasyMockUtils.verifyAndResetAll(searchServices);
  }

  private void expectDownload(final String id, final String contents, final TropixSearchService searchService) throws RemoteException {
    final TransferServiceContextReference tscRef = new TransferServiceContextReference();
    EasyMock.expect(searchService.prepareDownload(id, null)).andReturn(tscRef);
    downloadContextFactory.getDownloadContext(EasyMock.same(tscRef), EasyMock.eq(getUserSession().getProxy()));
    EasyMock.expectLastCall().andReturn(InputContexts.forString(contents));
  }

  @Test(groups = "unit")
  public void testSingleDownload() throws RemoteException {
    final String id = UUID.randomUUID().toString();
    final String serviceUrl = "http://search";
    final TropixSearchService service = expectGetService(serviceUrl);
    params.put("id", id);
    params.put("serviceUrl", serviceUrl);
    params.put("isBatch", "false");
    expectDownload(id, "The contents", service);
    download();
    assert new String(outputStream.toByteArray()).equals("The contents");
  }

  @Test(groups = "unit")
  public void testMultipleDownload() throws RemoteException {
    final List<String> ids = Lists.newArrayList();
    final List<String> serviceUrls = Lists.newArrayList();
    for(int i = 0; i < 3; i++) {
      final String id = UUID.randomUUID().toString();
      ids.add(id);
      final String serviceUrl = "http://search" + i;
      serviceUrls.add(serviceUrl);
      final TropixSearchService service = expectGetService(serviceUrl);
      expectDownload(id, "<contents>" + i + "</contents>", service);
    }
    params.put("id", Joiner.on(",").join(ids));
    params.put("serviceUrl", Joiner.on(", ").join(serviceUrls));
    params.put("names", "foo.zip, moo.txt,moo2.txt");
    params.put("isBatch", "true");
    download();
    final File tempDir = FILE_UTILS.createTempDirectory();
    try {
      final File zipFile = new File(tempDir, "tint-export.zip");
      FILE_UTILS.writeByteArrayToFile(zipFile, outputStream.toByteArray());
      ZIP_UTILS.unzipToDirectory(zipFile, tempDir);
      final File fooZip = new File(tempDir, "foo.zip");
      assert fooZip.exists();
      assert FILE_UTILS.readFileToString(fooZip).equals("<contents>0</contents>");

      final File moo2 = new File(tempDir, "moo2.txt");
      assert moo2.exists();
      assert FILE_UTILS.readFileToString(moo2).equals("<contents>2</contents>");
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(tempDir);
    }
  }

}
