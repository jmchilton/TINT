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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.client.directory.GridUser;

// TODO: Clean up the hackish nature of this class
public class UserIterableImpl implements Iterable<GridUser> {
  private ConcurrentHashMap<String, GridUser> gridUserMap;
  private Supplier<Multimap<String, Person>> personSupplier;

  private static class PersonFunction implements Function<Person, GridUser> {
    private final String institution;

    PersonFunction(final String institution) {
      this.institution = institution;
    }

    public GridUser apply(final Person person) {
      final GridUser user = new GridUser();
      user.setInstitution(institution);
      user.setFirstName(StringEscapeUtils.escapeHtml(person.getFirstName()));
      user.setLastName(StringEscapeUtils.escapeHtml(person.getLastName()));
      user.setEmailAddress(StringEscapeUtils.escapeHtml(person.getEmailAddress()));
      final String gridId = person.getCagridIdentity();
      if(gridId.equals(StringEscapeUtils.escapeHtml(gridId))) {
        user.setGridId(gridId);
      } else {
        return null;
      }
      return user;
    }
  }

  public Iterator<GridUser> iterator() {
    final Collection<GridUser> users = new LinkedList<GridUser>();
    final Multimap<String, Person> persons = personSupplier.get();
    for(String institution : persons.keySet()) {
      Iterables.addAll(users, Iterables.filter(Collections2.transform(persons.get(institution), new PersonFunction(institution)), Predicates.not(Predicates.isNull())));
    }
    for(final GridUser user : users) {
      gridUserMap.put(user.getGridId(), user);
    }
    return users.iterator();
  }

  public void setPersonSupplier(final Supplier<Multimap<String, Person>> personSupplier) {
    this.personSupplier = personSupplier;
  }

  public void setGridUserMap(final ConcurrentHashMap<String, GridUser> gridUserMap) {
    this.gridUserMap = gridUserMap;
  }

}
