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

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.service.FileTypeService;

public class FileTypeServiceTest extends BaseGwtServiceTest {
  private FileTypeService fileTypeService;
  private FileTypeServiceImpl gwtFileTypeService;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    fileTypeService = EasyMock.createMock(FileTypeService.class);
    gwtFileTypeService = new FileTypeServiceImpl(getUserSession(), getSanitizer(), fileTypeService);
  }
  
  @Test(groups = "unit")
  public void testCreate() {
    final FileType fileType = new FileType();
    fileType.setExtension("ext1");
    fileType.setShortName("A short name");    

    final FileType toReturnFileType= new FileType();
    toReturnFileType.setExtension("ext1");
    toReturnFileType.setShortName("A short name");    
    toReturnFileType.setId(UUID.randomUUID().toString());

    EasyMock.expect(fileTypeService.create(expectUserId(), EasyMock.same(fileType))).andReturn(toReturnFileType);
    EasyMock.replay(fileTypeService);
    assert toReturnFileType.equals(gwtFileTypeService.createFileType(fileType));
    assert getSanitizer().wasSanitized(toReturnFileType);
    EasyMock.verify(fileTypeService);
  }
  
  @Test(groups = "unit")
  public void testUpdate() {
    final FileType fileType = new FileType();
    fileType.setExtension("ext1");
    fileType.setShortName("A short name");    

    fileTypeService.update(expectUserId(), EasyMock.same(fileType));
    EasyMock.replay(fileTypeService);
    gwtFileTypeService.updateFileType(fileType);
    EasyMock.verify(fileTypeService);
  }

}
