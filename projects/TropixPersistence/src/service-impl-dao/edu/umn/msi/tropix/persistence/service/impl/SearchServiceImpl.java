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

import java.util.Collection;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.ModelUtils;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.service.SearchService;
import edu.umn.msi.tropix.persistence.service.impl.utils.PersistenceModelUtils;
import edu.umn.msi.tropix.persistence.service.impl.utils.Predicates;

@ManagedBean @Named("searchService")
class SearchServiceImpl extends ServiceBase implements SearchService {
  public Collection<TropixObject> getChildren(final String userId, final String objectId) {
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
    return Collections2.filter(ModelUtils.getChildren(object), Predicates.getValidAndCanReadPredicate(getSecurityProvider(), userId));
  }

  public Collection<TropixObject> getTopLevelObjects(final String userId, final String ownerId) {
    return getTropixObjectDao().getTopLevelObjects(userId, ownerId);
  }

  // Search results should return virtual folders...
  private TropixObject[] filterSearchResults(final List<TropixObject> objects) {
    final Iterable<TropixObject> filteredObjects = Iterables.filter(objects, new Predicate<TropixObject>() {
      public boolean apply(final TropixObject object) {
        return !(object instanceof VirtualFolder);
      }
    });
    return Iterables.toArray(filteredObjects, TropixObject.class);
  }

  public TropixObject[] searchObjects(final String userId, final String name, final String description, final String ownerId, final TropixObjectType objectType) {
    final Class<? extends TropixObject> queryType = objectType == null ? TropixObject.class : PersistenceModelUtils.getClass(objectType);
    final String queryName = name == null ? null : toContainsQuery(name);
    final String queryDescription = description == null ? null : toContainsQuery(description);
    return filterSearchResults(getTropixObjectDao().searchObjects(userId, queryType, queryName, queryDescription, ownerId));
  }

  // Takes query and returns a new query that can have anything on either side of the
  // query text, so for instance a search for "mouse" could match "a mouse sample".
  private String toContainsQuery(final String query) {
    return "%" + query + "%";
  }

  public TropixObject[] quickSearchObjects(final String userId, final String query) {
    return filterSearchResults(getTropixObjectDao().quickSearchObjects(userId, toContainsQuery(query)));
  }

}
