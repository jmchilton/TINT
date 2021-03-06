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

package edu.umn.msi.tropix.persistence.dao.hibernate;

import javax.annotation.ManagedBean;

import org.hibernate.Query;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.dao.FileTypeDao;

@ManagedBean
class FileTypeDaoImpl extends GenericDaoImpl<FileType> implements FileTypeDao {

  public synchronized FileType createOrGetType(final String extension, final String shortName) {
    final Query query;
    Preconditions.checkNotNull(extension);
    if(StringUtils.hasText(extension)) {
      if(StringUtils.hasText(shortName)) {
        query = createQuery("from FileType where extension = :extension and shortName = :shortName");
        query.setParameter("shortName", shortName);
        query.setParameter("extension", extension);        
      } else {
        query = createQuery("from FileType where extension = :extension");
        query.setParameter("extension", extension);        
      }
    } else {
      query = createQuery("from FileType where extension = '' or extension is null");      
    }
    final FileType type = (FileType) query.uniqueResult();
    if(type != null) {
      return type;
    } else {
      final FileType newType = new FileType();
      newType.setExtension(extension);
      newType.setShortName(shortName);
      save(newType);
      flush();
      return newType;
    }
  }

  public FileType getType(final String extension) {
    final Query query = createQuery("from FileType where extension = :extension");
    query.setParameter("extension", extension);
    final FileType type = (FileType) query.uniqueResult();
    return type;
  }
  
}
