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

import java.util.Arrays;

import info.minnesotapartnership.tropix.directory.models.Person;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.UserService;

public class LocalPersonSupplierImpl implements Supplier<Multimap<String, Person>> {
  private UserService userService;
  private String institution;
  
  private static final Function<User, Person> USER_TO_PERSON_FUNCTION = new Function<User, Person>() {
    
    public Person apply(final User user) {
      Person person = new Person();
      person.setCagridIdentity(user.getCagridId());
      person.setFirstName(user.getFirstName());
      person.setLastName(user.getLastName());
      person.setEmailAddress(user.getEmail());
      person.setPhoneNumber(user.getPhone());
      return person;
    }
    
  };
  
  public Multimap<String, Person> get() {
    Iterable<Person> persons = Lists.transform(Arrays.asList(userService.getUsers()), USER_TO_PERSON_FUNCTION);
    final Multimap<String, Person> personMap = HashMultimap.create();
    personMap.putAll(institution, persons);
    return personMap;
  }

  public void setInstitution(final String institution) {
    this.institution = institution;
  }

  public void setUserService(final UserService userService) {
    this.userService = userService;
  }
  
}
