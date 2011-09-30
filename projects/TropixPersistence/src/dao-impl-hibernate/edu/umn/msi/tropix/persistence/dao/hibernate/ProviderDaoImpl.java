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

import java.util.List;

import javax.annotation.ManagedBean;

import org.hibernate.Query;

import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.persistence.dao.ProviderDao;

@ManagedBean
class ProviderDaoImpl extends GenericDaoImpl<Provider> implements ProviderDao {

  public boolean canModify(final String userId, final String providerCatalogId) {
    final Query query = getSession().getNamedQuery("canModifyProvider");
    query.setParameter("userId", userId);
    query.setParameter("providerCatalogId", providerCatalogId);
    return ((Long) query.uniqueResult()) != 0L;
  }

  public Provider loadFromCatalogId(final String providerCatalogId) {
    final Query query = super.createQuery("from Provider p where p.catalogId = :providerCatalogId");
    query.setParameter("providerCatalogId", providerCatalogId);
    return (Provider) query.uniqueResult();
  }

  public Iterable<String> getProviderCatalogIds(final String cagridId) {
    final Query query = super.createQuery("select p.catalogId from Provider p, User u where u.cagridId = :cagridId and (u member of p.users or ((select count(g) from Group g where u member of g.users and g member of p.groups) > 0 ))");
    query.setParameter("cagridId", cagridId);
    @SuppressWarnings("unchecked")
    final List<String> ids = query.list();
    return ids;
  }

  public Provider getObjectsProvider(final String objectId) {
    final Query query = super.createQuery("select p from Provider p, TropixObject o where  o.id = :objectId and o member of p.objects");
    query.setParameter("objectId", objectId);
    return (Provider) query.uniqueResult();
  }

}
