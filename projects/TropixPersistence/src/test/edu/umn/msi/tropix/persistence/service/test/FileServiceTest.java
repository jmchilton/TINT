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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.FileService;

public class FileServiceTest extends ServiceTest {
  @Autowired
  private FileService fileService;

  @Test
  public void recordAndGetPhysicalFile() {
    final String fileId = newId();
    fileService.recordLength(fileId, 2312341);
    assert fileService.loadPhysicalFile(fileId).getSize() == 2312341;
  }
  
  private TropixFile saveFile(final User owner) {
    final String id = newId(), fileId = newId();
    final TropixFile file = new TropixFile();
    file.setId(id);
    file.setFileId(fileId);

    saveNewTropixObject(file, owner);
    return file;
  }

  @Test
  public void canRead() {
    final User owner = createTempUser();
    final TropixFile file = saveFile(owner);
    final String fileId = file.getFileId();

    assert fileService.canReadFile(owner.getCagridId(), fileId);

    final User otherUser = createTempUser();
    assert !fileService.canReadFile(otherUser.getCagridId(), fileId);

    getTropixObjectDao().addRole(file.getId(), "read", otherUser);
    assert fileService.canReadFile(otherUser.getCagridId(), fileId);
  }
  
  @Test(dataProviderClass = TestNGDataProviders.class, dataProvider = "bool1")
  public void canReadAll(final boolean canRead) {
    final List<String> fileIds = Lists.newArrayList();
    final User owner = createTempUser();
    for(int i = 0; i < 204; i++) {
      final TropixFile file = saveFile((!canRead && i == 189) ? createTempUser() : owner);
      final String fileId = file.getFileId();
      fileIds.add(fileId);
    }

    assert canRead == fileService.canReadAll(owner.getCagridId(), Iterables.toArray(fileIds, String.class));
  }
  
  
  @Test
  public void filesExist() {    
    final User owner = createTempUser();
    final TropixFile file = saveFile(owner), file2 = saveFile(owner);
    
    assert fileService.filesExist(new String[] {file.getFileId(), file2.getFileId()});
    assert !fileService.filesExist(new String[] {file.getFileId(), file2.getFileId(), UUID.randomUUID().toString()});        
  }

  @Test
  public void delete() {
    final User owner = createTempUser();
    final TropixFile file = saveFile(owner);
    final String fileId = file.getFileId();

    assert !fileService.canDeleteFile(owner.getCagridId(), fileId);
  }
}
