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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.dao.DaoFactory;

@Named("daoFactory")
class DaoFactoryImpl implements DaoFactory {
  private final SessionFactory sessionFactory;
  private final Map<Class<?>, GenericDaoImpl<?>> daoCache = new HashMap<Class<?>, GenericDaoImpl<?>>();

  @Inject
  DaoFactoryImpl(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @SuppressWarnings("unchecked")
  public synchronized <T> Dao<T> getDao(final Class<T> clazz) {
    GenericDaoImpl<T> dao;
    if(daoCache.containsKey(clazz)) {
      dao = (GenericDaoImpl<T>) daoCache.get(clazz);
    } else {
      dao = new GenericDaoImpl<T>(clazz);
      dao.setSessionFactory(sessionFactory);
      daoCache.put(clazz, dao);
    }
    return dao;
  }
}
