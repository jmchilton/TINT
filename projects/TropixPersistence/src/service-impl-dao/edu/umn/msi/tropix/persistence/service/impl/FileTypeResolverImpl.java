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
import javax.inject.Named;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import edu.umn.msi.tropix.persistence.dao.FileTypeDao;

@ManagedBean
class FileTypeResolverImpl implements FileTypeResolver {
  private final MessageSource messages;
  private final FileTypeDao fileTypeDao;
    
  @Inject
  FileTypeResolverImpl(@Named("serviceLayerMessageSource") final MessageSource messages, final FileTypeDao fileTypeDao) {
    this.messages = messages;
    this.fileTypeDao = fileTypeDao;
  }

  public FileType getType(final StockFileExtensionI fileExtension) {
    final String extension = fileExtension.getExtension();
    final String shortName = messages.getMessage("file.type" + fileExtension.getExtension()); 
    return fileTypeDao.getOrCreateType(extension, shortName);
  }

}
