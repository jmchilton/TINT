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

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;

import edu.umn.msi.tropix.persistence.dao.Dao;

/**
 * Based on the idea found here http://www.hibernate.org/328.html.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 * @param <T>
 */
public class GenericDaoImpl<T> extends TropixPersistenceTemplate implements Dao<T> {
  private final Class<T> persistentClass;

  /**
   * Override setSessionFactory so the @Inject annotation can be added to it.
   */
  @Inject
  public void setSessionFactory(@Named("sessionFactory") final SessionFactory sessionFactory) {
    super.setSessionFactory(sessionFactory);
  }
  
  @SuppressWarnings("unchecked")
  public GenericDaoImpl() {
    this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public GenericDaoImpl(final Class<T> persistentClass) {
    this.persistentClass = persistentClass;
  }

  public List<T> findAll() {
    return findByCriteria();
  }

  @SuppressWarnings("unchecked")
  public T load(final String id) {
    return (T) get(persistentClass, id);
  }

  /*
   * public void makeTransient(T entity) { delete(entity); }
   */

  public T saveObject(final T entity) {
    saveOrUpdate(entity);
    return entity;
  }

  public void mergeEntity(final T entity) {
    merge(entity);
  }
  
  public void evictEntity(final T entity) {
    getSession().flush();
    getSession().evict(entity);
  }

  /**
   * Use this inside subclasses as a convenience method.
   */
  @SuppressWarnings("unchecked")
  protected List<T> findByCriteria(final Criterion... criterion) {
    final Criteria crit = getSession().createCriteria(persistentClass);
    for(final Criterion c : criterion) {
      crit.add(c);
    }
    return crit.list();
  }

  protected Query createQuery(final String query) {
    return getSession().createQuery(query);
  }

  public void delete(final String id) {
    delete(load(persistentClass, id));
    flush();
  }

}
