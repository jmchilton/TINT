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

import info.minnesotapartnership.tropix.directory.TropixDirectoryService;
import info.minnesotapartnership.tropix.directory.models.Person;

import java.util.Map;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class CaGridTropixDirectoryServicePersonSupplierImplTest {

  @Test(groups = "unit")
  public void getPersonMap() {
    final Credential credential = Credentials.getMock();
    CaGridTropixDirectoryServicePersonSupplierImpl iterable = new CaGridTropixDirectoryServicePersonSupplierImpl();
    iterable.setProxySupplier(Suppliers.ofInstance(credential));
    Map<String, String> instMap = Maps.newHashMap();
    instMap.put("mayo", "http://mayo");
    instMap.put("umn1", "http://umn1");
    instMap.put("umn2", "http://umn2");
    instMap.put("umn3", "http://umn3");
    iterable.setInstitutionToServiceAddressMap(instMap);
    @SuppressWarnings("unchecked")
    GridServiceFactory<TropixDirectoryService> serviceFactory = EasyMock.createMock(GridServiceFactory.class);
    TropixDirectoryService umn1Service = EasyMock.createMock(TropixDirectoryService.class);
    TropixDirectoryService umn2Service = EasyMock.createMock(TropixDirectoryService.class);
    TropixDirectoryService umn3Service = EasyMock.createMock(TropixDirectoryService.class);

    EasyMock.expect(serviceFactory.getService("http://mayo", credential)).andThrow(new RuntimeException());
    EasyMock.expect(serviceFactory.getService("http://umn1", credential)).andReturn(umn1Service);
    EasyMock.expect(serviceFactory.getService("http://umn2", credential)).andReturn(umn2Service);
    EasyMock.expect(serviceFactory.getService("http://umn3", credential)).andReturn(umn3Service);

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

    EasyMock.expect(umn1Service.getUsers()).andReturn(new Person[] {umn11, umn12, umn13});
    EasyMock.expect(umn2Service.getUsers()).andReturn(new Person[] {umn21});
    EasyMock.expect(umn3Service.getUsers()).andReturn(null);

    iterable.setServiceFactory(serviceFactory);

    EasyMock.replay(serviceFactory, umn1Service, umn2Service, umn3Service);

    Multimap<String, Person> persons = iterable.get();
    assert persons.keys().containsAll(Lists.newArrayList("umn1", "umn2"));
    assert persons.containsEntry("umn1", umn11);
    assert persons.containsEntry("umn1", umn12);
    assert persons.containsEntry("umn2", umn21);

    EasyMock.verify(serviceFactory, umn1Service, umn2Service, umn3Service);
  }

}
