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

package edu.umn.msi.tropix.storage.core.monitor;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.core.PersistentFileMapperService;

public class StorageMonitorClosureImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void testApplyNew() {
    final Credential mockCredential = Credentials.getMock("mockid");
    final TropixObjectService tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    final FolderService folderService = EasyMock.createMock(FolderService.class);
    final FileService fileService = EasyMock.createMock(FileService.class);
    final PersistentFileMapperService mapperService = EasyMock.createMock(PersistentFileMapperService.class);
    final MonitorConfig monitorConfig = EasyMock.createMock(MonitorConfig.class);
    final StorageMonitorClosureImpl closure = new StorageMonitorClosureImpl(tropixObjectService, folderService, mapperService, monitorConfig,

    fileService, Suppliers.ofInstance(mockCredential));
    closure.setStorageServiceUrl("http://storage");
    final File directory = FILE_UTILS.createTempDirectory();
    try {
      final File current = directory;
      final File other = new File(new File(".."), "someother");
      final File newFile = new File(new File(new File(directory, "moo"), "submoo"), "filename.txt");
      FILE_UTILS.writeStringToFile(newFile, "moo cow");
      EasyMock.expect(mapperService.pathHasMapping(FilenameUtils.normalize(newFile.getAbsolutePath()))).andStubReturn(false);
      EasyMock.expect(monitorConfig.getDirectories()).andStubReturn(Lists.newArrayList(other, current));
      EasyMock.expect(monitorConfig.getSharedFolderName(current)).andStubReturn("foo");
      final Capture<TropixFile> fileCapture = EasyMockUtils.newCapture();
      final String rootSharedFolderId = UUID.randomUUID().toString(), childSharedFolderId = UUID.randomUUID().toString();
      final VirtualFolder rootSharedFolder = new VirtualFolder();
      rootSharedFolder.setId(rootSharedFolderId);
      EasyMock.expect(folderService.getOrCreateRootVirtualFolderWithName("mockid", "foo")).andStubReturn(rootSharedFolder);
      final VirtualFolder childSharedFolder = new VirtualFolder();
      childSharedFolder.setId(childSharedFolderId);
      EasyMock
          .expect(
              folderService.getOrCreateVirtualPath(EasyMock.eq("mockid"), EasyMock.eq(rootSharedFolderId),
                  EasyMock.aryEq(new String[] {"moo", "submoo"}))).andReturn(childSharedFolder);
      final TropixFile returnedFile = new TropixFile();
      returnedFile.setId(UUID.randomUUID().toString());
      EasyMock.expect(
          tropixObjectService.createFile(EasyMock.eq("mockid"), EasyMock.eq(childSharedFolderId), EasyMock.capture(fileCapture),
              EasyMock.<String>isNull())).andReturn(returnedFile);
      final Capture<String> fileIdCapture = EasyMockUtils.newCapture();
      final Capture<String> fileIdCapture2 = EasyMockUtils.newCapture();
      mapperService.registerMapping(EasyMock.capture(fileIdCapture), EasyMock.eq(FilenameUtils.normalize(newFile.getAbsolutePath())));
      tropixObjectService.addToSharedFolder("mockid", returnedFile.getId(), childSharedFolderId, false);
      fileService.recordLength(EasyMock.capture(fileIdCapture2), EasyMock.eq(newFile.length()));
      EasyMock.replay(folderService, fileService, tropixObjectService, mapperService, monitorConfig);
      closure.apply(newFile);
      EasyMock.verify(folderService, fileService, tropixObjectService, mapperService, monitorConfig);
      TropixFile tropixFile = fileCapture.getValue();
      assert tropixFile.getFileId().equals(fileIdCapture.getValue());
      assert tropixFile.getName().equals("filename.txt");
      assert tropixFile.getCommitted();
      assert tropixFile.getStorageServiceUrl().equals("http://storage");
      assert fileIdCapture.getValue().equals(fileIdCapture2.getValue());
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(directory);
    }
  }

}
