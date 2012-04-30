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

package edu.umn.msi.tropix.persistence.service.security.impl;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.persistence.dao.hibernate.TropixPersistenceTemplate;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

@ManagedBean
@Named("persistenceSecurityProvider")
class SecurityProviderImpl extends TropixPersistenceTemplate implements SecurityProvider {

  /**
   * Override setSessionFactory so the @Inject annotation can be added to it.
   */
  @Inject
  public void setSessionFactory(@Named("sessionFactory") final SessionFactory sessionFactory) {
    super.setSessionFactory(sessionFactory);
  }

  public boolean canModify(final String tropixObjectId, final String userGridId) {
    final Query query = getSession().getNamedQuery("canEdit");
    query.setParameter("userId", userGridId);
    query.setParameter("objectId", tropixObjectId);
    return ((Long) query.uniqueResult()) > 0;
  }

  public boolean canRead(final String tropixObjectId, final String userGridId) {
    final Query query = getSession().getNamedQuery("canRead");
    query.setParameter("userId", userGridId);
    query.setParameter("objectId", tropixObjectId);
    return ((Long) query.uniqueResult()) > 0;
  }

  public boolean canReadAll(final Iterable<String> tropixObjectIds, final String userGridId) {
    boolean canReadAll = true;
    if(!Iterables.isEmpty(tropixObjectIds)) {
      canReadAll = canReadAllNonEmpty(tropixObjectIds, userGridId);
    }
    return canReadAll;
  }

  private String getParentId(final String tropixObjectId) {
    final Query query = getSession().getNamedQuery("parentFolderId");
    query.setParameter("objectId", tropixObjectId);
    return (String) query.uniqueResult();
  }

  private boolean allHaveParentId(final Iterable<String> tropixObjectIds, final String parentId) {
    boolean allHaveParentId = true;
    for(final List<String> objectIdsPartition : partition(tropixObjectIds)) {
      final Query query = getSession().getNamedQuery("allHaveParentId");
      query.setParameter("parentId", parentId);
      query.setParameterList("objectIds", objectIdsPartition);
      allHaveParentId = ((Long) query.uniqueResult()) >= objectIdsPartition.size();
      if(!allHaveParentId) {
        break;
      }
    }
    return allHaveParentId;

  }

  private boolean canReadAllNonEmpty(final Iterable<String> tropixObjectIds, final String userGridId) {
    // Highly questionable optimization we are no longer using, does reduce joining though
    /*
     * final String firstId = tropixObjectIds.iterator().next();
     * final String parentId = getParentId(firstId);
     * // Optimization to prevent huge joins: Just check the parent is readable and they all share the same parent
     * // the way the API is used this will always be the case.
     * if(parentId != null &&
     * canRead(parentId, userGridId) &&
     * allHaveParentId(tropixObjectIds, parentId)) {
     * return true;
     * }
     */

    boolean canReadAll = true;
    for(final List<String> objectIdsPartition : partition(tropixObjectIds)) {
      final Query query = getSession().getNamedQuery("canReadAll");
      query.setParameter("userId", userGridId);
      query.setParameterList("objectIds", objectIdsPartition);
      canReadAll = ((Long) query.uniqueResult()) >= objectIdsPartition.size();
      if(!canReadAll) {
        break;
      }
    }
    return canReadAll;
  }

}
