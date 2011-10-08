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

import java.util.UUID;

import javax.inject.Inject;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.test.ServiceTest.Destination;

public class TropixObjectServiceCreateFileTest extends ServiceTest {
  @Inject
  private TropixObjectService tropixObjectService;

  @Test
  public void createWithExistingSize() {
    final User user1 = createTempUser();
    final String fileId = UUID.randomUUID().toString();

    /*
    final String id = UUID.randomUUID().toString();
    super.simpleJdbcTemplate.update("INSERT INTO TROPIX_OBJECT (ID) VALUES (?)", id);
    super.simpleJdbcTemplate.update("INSERT INTO FILE (OBJECT_ID, FILE_ID, FILE_SIZE) VALUES (?,?,?)", id, fileId, 100L);
    */

    final TropixFile fileWithSize = new TropixFile();
    //fileWithSize.setFileSize(100L);
    fileWithSize.setFileId(fileId);
    getTropixObjectDao().saveOrUpdateTropixObject(fileWithSize);
    getDaoFactory().getDao(TropixFile.class).evictEntity(fileWithSize);
    
    final TropixFile file = new TropixFile();
    file.setDescription("moo");
    file.setFileId(fileId);
    
    tropixObjectService.createFile(user1.getCagridId(), user1.getHomeFolder().getId(), file, null);
    final TropixFile loadedFile = getTropixObjectDao().loadTropixFileWithFileId(fileId);
    assert loadedFile.getDescription().equals("moo");
    //assert loadedFile.getFileSize() == 100L;
        
  }
  
  @Test
  public void createFileTest() {
    final User user1 = createTempUser();
    final String id1 = newId();

    final TropixFile file1 = new TropixFile();
    file1.setFileId(id1);

    tropixObjectService.createFile(user1.getCagridId(), user1.getHomeFolder().getId(), file1, ".moo");
  }

  @Test
  public void createFileTestNullExtension() {
    final User user1 = createTempUser();
    final String id1 = newId();

    final TropixFile file1 = new TropixFile();
    file1.setFileId(id1);

    tropixObjectService.createFile(user1.getCagridId(), user1.getHomeFolder().getId(), file1, null);

    assert getTropixObjectDao().loadTropixFileWithFileId(id1).getFileType().equals(getFileType(StockFileExtensionEnum.UNKNOWN));
  }
  
  @Test
  public void createFileTestEmptyExtension() {
    final User user1 = createTempUser();
    final String id1 = newId();

    final TropixFile file1 = new TropixFile();
    file1.setFileId(id1);

    tropixObjectService.createFile(user1.getCagridId(), user1.getHomeFolder().getId(), file1, "");
    assert getTropixObjectDao().loadTropixFileWithFileId(id1).getFileType().equals(getFileType(StockFileExtensionEnum.UNKNOWN));

  }
  
  @Test
  public void createFileAllDestinations() {
    final User user1 = createTempUser();
  
    for(Destination destination : super.getTestDestinationsWithNull(user1)) {
      final TropixFile file1 = new TropixFile();
      final String id1 = newId();
      file1.setFileId(id1);

      final TropixFile returnedFile = tropixObjectService.createFile(user1.getCagridId(), destination.getId(), file1, null);
      assert getTropixObjectDao().loadTropixFileWithFileId(id1).getFileType().equals(getFileType(StockFileExtensionEnum.UNKNOWN));
      destination.validate(returnedFile);
      destination.verifyContains(returnedFile);
      
    }
  }

}
