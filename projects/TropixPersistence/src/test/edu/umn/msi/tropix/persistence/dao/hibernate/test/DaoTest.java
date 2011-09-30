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

package edu.umn.msi.tropix.persistence.dao.hibernate.test;

import java.util.HashSet;
import java.util.UUID;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/persistence/test/applicationContext.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class DaoTest extends AbstractTransactionalTestNGSpringContextTests {

  private final HibernateTemplate hibernateTemplate = new HibernateTemplate();

  public User getNewUser() {
    final User user = new User();
    final String id = newId();
    user.setCagridId(id);
    user.setId(id);
    user.setGroups(new HashSet<Group>());
    return user;
  }

  @Autowired
  public void setSessionFactory(final SessionFactory sessionFactory) {
    hibernateTemplate.setSessionFactory(sessionFactory);
  }

  protected void flush() {
    hibernateTemplate.flush();
  }

  protected String newId() {
    return UUID.randomUUID().toString();
  }
}
