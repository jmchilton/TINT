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

package edu.umn.msi.tropix.storage.core.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.umn.msi.tropix.storage.core.PersistentFileMapperService;

class PersistentFileMapperServiceImpl implements PersistentFileMapperService {
  private EntityManager entityManager;

  @PersistenceContext(unitName = "storage")
  void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public boolean pathHasMapping(final String path) {
    final Query query = entityManager.createQuery("select count(e) from FileMapEntry e where e.path = :path");
    query.setParameter("path", path);
    final long count = (Long) query.getSingleResult();
    return count > 0;
  }

  public void registerMapping(final String id, final String path) {
    final FileMapEntry entry = new FileMapEntry();
    entry.setId(id);
    entry.setPath(path);
    entityManager.persist(entry);    
    entityManager.flush();
    entityManager.clear();
  }

  public String getPath(final String id) {
    final Query query = entityManager.createQuery("select path from FileMapEntry e where e.id = :id");
    query.setParameter("id", id);
    @SuppressWarnings("unchecked")
    final List<String> results = (List<String>) query.getResultList();
    if(results.isEmpty()) {
      return null;
    } else {
      return results.iterator().next();
    }
  }

}
