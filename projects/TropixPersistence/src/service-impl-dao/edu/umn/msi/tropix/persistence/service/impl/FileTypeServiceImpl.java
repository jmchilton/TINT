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

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import edu.umn.msi.tropix.persistence.dao.FileTypeDao;
import edu.umn.msi.tropix.persistence.service.FileTypeService;

@ManagedBean
class FileTypeServiceImpl implements FileTypeService {
  private final FileTypeDao fileTypeDao;
  private final FileTypeResolver fileTypeResolver;
  
  @Inject
  FileTypeServiceImpl(final FileTypeDao fileTypeDao, final FileTypeResolver fileTypeResolver) {
    this.fileTypeDao = fileTypeDao;
    this.fileTypeResolver = fileTypeResolver;
  }

  public FileType create(final String userId, final FileType fileType) {
    return fileTypeDao.saveObject(fileType); 
  }

  public FileType[] listFileTypes(final String userId) {
    return Iterables.toArray(fileTypeDao.findAll(), FileType.class);
  }

  public FileType load(final String userId, final String fileTypeId) {
    return fileTypeDao.load(fileTypeId);
  }

  public void update(final String userId, final FileType fileType) {
    final FileType loadedFileType = fileTypeDao.load(fileType.getId());
    loadedFileType.setShortName(fileType.getShortName());
    loadedFileType.setExtension(fileType.getExtension());
    fileTypeDao.mergeEntity(loadedFileType);
  }

  public FileType loadPrimaryFileTypeWithExtension(final String userId, final String extension) {
    FileType fileType = fileTypeDao.getType(extension);
    if(fileType == null) {
      for(final StockFileExtensionI stockExtension : StockFileExtensionEnum.values()) {
        if(stockExtension.getExtension().toLowerCase().equals(extension.toLowerCase())) {
          fileType = fileTypeResolver.getType(stockExtension);
        }
      }
    }
    return fileType;
  }

}
