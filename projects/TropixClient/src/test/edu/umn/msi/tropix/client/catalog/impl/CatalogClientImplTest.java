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

package edu.umn.msi.tropix.client.catalog.impl;

import java.rmi.RemoteException;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogSearchAPI;
import edu.mayo.bmi.bic.bobcat.api.Query;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;

public class CatalogClientImplTest {

  @Test(groups = "unit")
  public void getAPIs() {
    final CatalogClientImpl client = new CatalogClientImpl();
    final List<CatalogInstance> instances = getInstances("id1", "id2", "id3");
    CatalogEntryAPI entryAPI = EasyMock.createMock(CatalogEntryAPI.class);
    CatalogOntAPI ontAPI = EasyMock.createMock(CatalogOntAPI.class);
    CatalogSearchAPI searchAPI = EasyMock.createMock(CatalogSearchAPI.class);

    EasyMock.expect(instances.get(0).getCatalogEntryAPI()).andReturn(entryAPI);
    EasyMock.expect(instances.get(1).getCatalogOntAPI()).andReturn(ontAPI);
    EasyMock.expect(instances.get(2).getCatalogSearchAPI()).andReturn(searchAPI);
    EasyMockUtils.replayAll(instances);
    client.setCatalogInstanceIterable(instances);
    assert Iterables.elementsEqual(client.getCatalogIds(), Lists.newArrayList("id1", "id2", "id3"));
    assert client.getEntryAPI("id1") == entryAPI;
    assert client.getOntologAPI("id2") == ontAPI;
    assert client.getSearchAPI("id3") == searchAPI;
  }

  @Test(groups = "unit")
  public void search() throws RemoteException {
    for(boolean simpleSearch : new boolean[]{true, false}) {
      final CatalogClientImpl client = new CatalogClientImpl();
      final List<CatalogInstance> instances = getInstances("id1", "id2", "id3", "id4");
      client.setCatalogInstanceIterable(instances);
      final List<CatalogSearchAPI> searchAPIs = getSearchAPIs(instances);

      SearchHit hit11 = new SearchHit();
      hit11.setEntryID("hit11");

      SearchHit hit12 = new SearchHit();
      hit12.setEntryID("hit12");

      SearchHit hit13 = new SearchHit();
      hit13.setEntryID("hit13");

      SearchHit hit21 = new SearchHit();
      hit21.setEntryID("hit21");

      final Query query = new Query();      
      if(simpleSearch) {
        EasyMock.expect(searchAPIs.get(0).entrySearch("moo")).andReturn(new SearchHit[]{hit11, hit12, hit13});
        EasyMock.expect(searchAPIs.get(1).entrySearch("moo")).andReturn(new SearchHit[]{hit21});
        EasyMock.expect(searchAPIs.get(2).entrySearch("moo")).andReturn(null);
        EasyMock.expect(searchAPIs.get(3).entrySearch("moo")).andThrow(new RemoteException());
      } else {
        EasyMock.expect(searchAPIs.get(0).customEntrySearch(query)).andReturn(new SearchHit[]{hit11, hit12, hit13});
        EasyMock.expect(searchAPIs.get(1).customEntrySearch(query)).andReturn(new SearchHit[]{hit21});
        EasyMock.expect(searchAPIs.get(2).customEntrySearch(query)).andReturn(null);
        EasyMock.expect(searchAPIs.get(3).customEntrySearch(query)).andThrow(new RemoteException());
      }

      EasyMockUtils.replayAll(instances, searchAPIs);

      Multimap<String, SearchHit> hits;
      if(simpleSearch) {
        hits = client.entrySearch("moo");
      } else {
        hits = client.customEntrySearch(query);
      }
      assert hits.get("id1").size() == 3;
      assert hits.get("id2").size() == 1;
      assert !hits.containsKey("id3");
      assert !hits.containsKey("id4");

      EasyMockUtils.verifyAndResetAll(instances, searchAPIs);
    }
  }

  private List<CatalogSearchAPI> getSearchAPIs(final List<CatalogInstance> instances) {
    List<CatalogSearchAPI> searchAPIs = Lists.newArrayListWithCapacity(instances.size());
    for(CatalogInstance instance : instances) {
      CatalogSearchAPI searchAPI = EasyMock.createMock(CatalogSearchAPI.class);
      searchAPIs.add(searchAPI);
      EasyMock.expect(instance.getCatalogSearchAPI()).andReturn(searchAPI);
    }
    return searchAPIs;
  }

  private List<CatalogInstance> getInstances(final String... ids) {
    List<CatalogInstance> instances = Lists.newArrayListWithCapacity(ids.length);
    for(String id : ids) {
      CatalogInstance instance = EasyMock.createMock(CatalogInstance.class);
      EasyMock.expect(instance.getId()).andStubReturn(id);
      instances.add(instance);
    }
    return instances;
  }


}
