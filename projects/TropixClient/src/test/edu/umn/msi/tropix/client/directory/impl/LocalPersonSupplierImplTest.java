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

package edu.umn.msi.tropix.client.directory.impl;

import java.util.Iterator;

import info.minnesotapartnership.tropix.directory.models.Person;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.UserService;

public class LocalPersonSupplierImplTest {

  @Test(groups = "unit")
  public void get() {
    LocalPersonSupplierImpl supplier = new LocalPersonSupplierImpl();
    
    final UserService userService = EasyMock.createMock(UserService.class);
    
    final User user1 = new User();
    user1.setCagridId("user1");
    user1.setFirstName("John");
    user1.setLastName("Chilton");

    final User user2 = new User();
    user2.setCagridId("user2");
    
    final User[] users = new User[]{user1, user2};
    EasyMock.expect(userService.getUsers()).andReturn(users);
    supplier.setUserService(userService);
    supplier.setInstitution("Local");
    
    EasyMock.replay(userService);
    Multimap<String, Person> persons = supplier.get();
    assert persons.keySet().size() == 1;
    assert persons.keySet().contains("Local");

    assert persons.values().size() == 2;
    final Iterator<Person> personIter = persons.values().iterator();
    final Person iterFirstPerson = personIter.next();
    Person p1, p2;
    if(iterFirstPerson.getCagridIdentity().equals("user1")) {
      p1 = iterFirstPerson;
      p2 = personIter.next();
    } else {
      p2 = iterFirstPerson;
      p1 = personIter.next();
    }
    
    assert p1.getCagridIdentity().equals("user1");
    assert p1.getFirstName().equals("John");
    assert p1.getLastName().equals("Chilton");
    
    assert p2.getCagridIdentity().equals("user2");
  }
  
}
