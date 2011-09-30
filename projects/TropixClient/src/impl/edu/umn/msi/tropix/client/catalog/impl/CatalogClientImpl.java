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
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogSearchAPI;
import edu.mayo.bmi.bic.bobcat.api.Query;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.umn.msi.tropix.client.catalog.CatalogClient;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;

public class CatalogClientImpl implements CatalogClient {
  private static final Log LOG = LogFactory.getLog(CatalogClientImpl.class);
  private Iterable<CatalogInstance> catalogInstanceIterable;

  private interface SearchProvider {
    SearchHit[] getHits(final CatalogInstance catalogInstance) throws RemoteException;
  }
  
  
  public Multimap<String, SearchHit> customEntrySearch(final Query query) {
    return getResults(new SearchProvider() {
      public SearchHit[] getHits(final CatalogInstance catalogInstance) throws RemoteException {
        return catalogInstance.getCatalogSearchAPI().customEntrySearch(query);
      }
    });
  }

  public Multimap<String, SearchHit> entrySearch(final String query) {
    return getResults(new SearchProvider() {
      public SearchHit[] getHits(final CatalogInstance catalogInstance) throws RemoteException {
        return catalogInstance.getCatalogSearchAPI().entrySearch(query);
      }
    });
  }

  public Iterable<String> getCatalogIds() {
    return Iterables.transform(catalogInstanceIterable, new Function<CatalogInstance, String>() {
      public String apply(final CatalogInstance instance) {
        return instance.getId();
      }
    });
  }

  public CatalogEntryAPI getEntryAPI(final String id) {
    return get(id).getCatalogEntryAPI();
  }

  public CatalogOntAPI getOntologAPI(final String id) {
    return get(id).getCatalogOntAPI();
  }

  public CatalogSearchAPI getSearchAPI(final String id) {
    return get(id).getCatalogSearchAPI();
  }

  private Multimap<String, SearchHit> getResults(final SearchProvider hitFunction) {
    final HashMultimap<String, SearchHit> searchHits = HashMultimap.create();
    for(final CatalogInstance instance : catalogInstanceIterable) {
      try {
        SearchHit[] hits = hitFunction.getHits(instance);
        if(hits == null) {
          hits = new SearchHit[0];
        }
        searchHits.putAll(instance.getId(), Arrays.asList(hits));
      } catch(final Exception e) {
        ExceptionUtils.logQuietly(LOG, e, "Failed to execute catalog search on service " + instance.getId());
      }
    }
    return searchHits;
  }

  private CatalogInstance get(final String id) {
    CatalogInstance instance = null;
    for(final CatalogInstance queryInstance : catalogInstanceIterable) {
      if(queryInstance.getId().equals(id)) {
        instance = queryInstance;
        break;
      }
    }
    return instance;
  }

  public void setCatalogInstanceIterable(final Iterable<CatalogInstance> catalogInstanceIterable) {
    this.catalogInstanceIterable = catalogInstanceIterable;
  }

}
