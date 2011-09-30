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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.UUID;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.dao.FileTypeDao;

public class FileTypeServiceImplUnitTest {

  @Test
  public void testLoadWithCreateType() {
    final FileTypeResolver fileTypeResolver = EasyMock.createMock(FileTypeResolver.class);
    final FileTypeDao fileTypeDao = EasyMock.createMock(FileTypeDao.class); 
    final FileTypeServiceImpl service = new FileTypeServiceImpl(fileTypeDao, fileTypeResolver);

    // Mock it out so that no type exists yet for BOWTIE_INDEXs and then verify resolver is called.
    // This is a true unit test so I can ensure that fileTypeDao returns null.
    EasyMock.expect(fileTypeDao.getType(StockFileExtensionEnum.BOWTIE_INDEX.getExtension())).andReturn(null);
    final FileType fileType = new FileType();
    fileType.setId(UUID.randomUUID().toString());
    EasyMock.expect(fileTypeResolver.getType(StockFileExtensionEnum.BOWTIE_INDEX)).andReturn(fileType);
    EasyMock.replay(fileTypeResolver, fileTypeDao);
    assert fileType.equals(service.loadPrimaryFileTypeWithExtension(UUID.randomUUID().toString(), StockFileExtensionEnum.BOWTIE_INDEX.getExtension()));
    EasyMock.verify(fileTypeResolver, fileTypeDao);
  }
  
}
