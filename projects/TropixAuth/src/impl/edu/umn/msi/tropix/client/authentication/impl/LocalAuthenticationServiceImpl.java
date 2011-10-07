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

package edu.umn.msi.tropix.client.authentication.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class LocalAuthenticationServiceImpl implements LocalAuthenticationService {
  private EntityManager entityManager;

  @PersistenceContext(unitName = "auth")
  public synchronized void setEntityManager(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public synchronized void createUser(final String username, final String passwordHash) {
    final LocalUser user = new LocalUser();
    user.setUserName(username);
    user.setPasswordHash(passwordHash);
    entityManager.persist(user);
    entityManager.flush();
    entityManager.clear();
  }

  public synchronized String getPasswordHash(final String username) {
    final LocalUser user = entityManager.find(LocalUser.class, username);
    return user.getPasswordHash();
  }

  public synchronized boolean userExists(final String username) {
    final LocalUser user = entityManager.find(LocalUser.class, username);
    return user != null;
  }

  public synchronized void changePassword(final String username, final String passwordHash) {
    final LocalUser user = entityManager.find(LocalUser.class, username);
    user.setPasswordHash(passwordHash);
    entityManager.persist(user);
    entityManager.flush();
    entityManager.clear();
  }

}
