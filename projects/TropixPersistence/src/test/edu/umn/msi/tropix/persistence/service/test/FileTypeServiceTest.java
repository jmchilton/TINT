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

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.FileTypeService;

public class FileTypeServiceTest extends ServiceTest {
  @Inject
  private FileTypeService fileTypeService;
  
  private User tempUser;
  private String userId;
  @BeforeMethod
  public void createUser() {
    tempUser = createTempUser();
    userId = tempUser.getCagridId();
  }
  
  @Test
  public void testGetFileTypeForName() {    
    final String fileName = "dbtest" + StockFileExtensionEnum.FASTA.getExtension();
    final FileType fileType = fileTypeService.getFileTypeForName(userId, fileName);
    Assert.assertEquals(fileTypeService.loadPrimaryFileTypeWithExtension(userId, StockFileExtensionEnum.FASTA.getExtension()).getExtension(), fileType.getExtension());
  }
  
  @Test
  public void testGrabsLongestFileType() {
    final String fileName = "moo" + StockFileExtensionEnum.BOWTIE_INDEX.getExtension();
    final FileType fileType = fileTypeService.getFileTypeForName(userId, fileName);
    Assert.assertEquals(fileTypeService.loadPrimaryFileTypeWithExtension(userId, StockFileExtensionEnum.BOWTIE_INDEX.getExtension()).getExtension(), fileType.getExtension());
  }
  
  @Test
  public void testDoesntGrabLongestIfDoesntMatchAll() {
    final String fileName = "moo" + StockFileExtensionEnum.ZIP.getExtension();
    final FileType fileType = fileTypeService.getFileTypeForName(userId, fileName);
    Assert.assertEquals(fileTypeService.loadPrimaryFileTypeWithExtension(userId, StockFileExtensionEnum.ZIP.getExtension()).getExtension(), fileType.getExtension());
  }
  
  @Test
  public void testHandlesExtraPeriods() {
    final String fileName = "moo.cow" + StockFileExtensionEnum.MASCOT_GENERIC_FORMAT.getExtension();
    final FileType fileType = fileTypeService.getFileTypeForName(userId, fileName);
    Assert.assertEquals(fileTypeService.loadPrimaryFileTypeWithExtension(userId, StockFileExtensionEnum.MASCOT_GENERIC_FORMAT.getExtension()).getExtension(), fileType.getExtension());    
  }
  
  @Test
  public void testFileTypeServiceOps() {
    final FileType type1 = new FileType(), type2 = new FileType();
    type1.setExtension("t1");
    type2.setExtension("t2");
    type1.setShortName("T1 Short");
    type2.setShortName("T2 Short");
        
    final FileType rType1 = fileTypeService.create(userId, type1);
    assert Lists.newArrayList(fileTypeService.listFileTypes(userId)).contains(rType1);
    final FileType rType2 = fileTypeService.create(userId, type2);
    assert Lists.newArrayList(fileTypeService.listFileTypes(userId)).contains(rType1);
    assert Lists.newArrayList(fileTypeService.listFileTypes(userId)).contains(rType2);
    
    FileType lType1 =  fileTypeService.load(userId, rType1.getId());
    assert lType1.getExtension().equals("t1");
    assert lType1.getShortName().equals("T1 Short");
    
    final FileType uType1 = new FileType();
    uType1.setId(rType1.getId());
    uType1.setShortName("updatedname");
    uType1.setExtension("updatedextension");
    
    fileTypeService.update(userId, uType1);
    
    lType1 =  fileTypeService.load(userId, rType1.getId());
    assert lType1.getExtension().equals("updatedextension");
    assert lType1.getShortName().equals("updatedname");
    
    lType1 =  fileTypeService.loadPrimaryFileTypeWithExtension(userId, "updatedextension");
    assert lType1.getExtension().equals("updatedextension");
    assert lType1.getShortName().equals("updatedname");
    
    
  }
}
