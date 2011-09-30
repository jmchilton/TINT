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

package edu.umn.msi.tropix.client.request.actions.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.google.common.collect.ImmutableList;

@ThreadSafe
public class PersistentActionServiceImpl implements PersistentActionService {
  /**
   * JPA EntityManager for interacting with the database.
   */
  @GuardedBy("lock")
  private EntityManager entityManager;

  /**
   * Used to synchronize access to entityManager.
   */
  private final Object lock = new Object();

  @PersistenceContext(unitName = "requestaction")
  public void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void completeAction(final long actionId) {
    synchronized(lock) {
      final RequestAction requestAction = entityManager.find(RequestAction.class, actionId);
      entityManager.remove(requestAction);
      entityManager.flush();
      entityManager.clear();
    }
  }

  public Collection<RequestAction> loadActions() {
    synchronized(lock) {
      final Query query = entityManager.createQuery("select action from RequestAction action");
      @SuppressWarnings("unchecked")
      final List<RequestAction> rawRequestActions = query.getResultList();
      entityManager.flush();
      entityManager.clear();
      return ImmutableList.copyOf(rawRequestActions);
    }
  }

  public void persistRequestAction(final RequestAction requestAction) {
    synchronized(lock) {
      entityManager.persist(requestAction);
      entityManager.flush();
    }
  }

}
