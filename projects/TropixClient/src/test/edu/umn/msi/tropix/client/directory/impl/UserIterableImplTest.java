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

import info.minnesotapartnership.tropix.directory.models.Person;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.client.directory.GridUser;

public class UserIterableImplTest {
  
  @Test(groups = "unit")
  public void userIterator() {
    final UserIterableImpl iterable = new UserIterableImpl();
    final ConcurrentHashMap<String, GridUser> gridUserMap = new ConcurrentHashMap<String, GridUser>();
    iterable.setGridUserMap(gridUserMap);
    
    Person umn11 = new Person();
    umn11.setCagridIdentity("umn11");
    umn11.setFirstName("<html");
    umn11.setLastName("Last < name");
    umn11.setEmailAddress("moo@<cow.net");

    Person umn12 = new Person();
    umn12.setCagridIdentity("umn12");
    
    Person umn13 = new Person();
    umn13.setCagridIdentity("<umn13");
    
    Person umn21 = new Person();
    umn21.setCagridIdentity("umn21");

    Multimap<String, Person> persons = LinkedHashMultimap.create();
    persons.putAll("umn1", Lists.newArrayList(umn11, umn12, umn13));
    persons.put("umn2", umn21);
    iterable.setPersonSupplier(Suppliers.ofInstance(persons));
        
    List<GridUser> users = Lists.newArrayList(); 
    Iterators.addAll(users, iterable.iterator());
    
    GridUser user1 = users.get(0), user2 = users.get(1), user3 = users.get(2);
    assert user1.getGridId().equals("umn11");
    assert !user1.getFirstName().contains("<");
    assert !user1.getLastName().contains("<");
    assert !user1.getEmailAddress().contains("<");
    assert user1.getInstitution().equals("umn1");
    
    assert user2.getGridId().equals("umn12");
    assert user2.getInstitution().equals("umn1");
    
    assert user3.getGridId().equals("umn21") : user3.getGridId();
    assert user3.getInstitution().equals("umn2");
    
  }
  
}
