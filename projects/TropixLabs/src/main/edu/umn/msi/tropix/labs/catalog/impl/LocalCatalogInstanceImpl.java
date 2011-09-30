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

package edu.umn.msi.tropix.labs.catalog.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import edu.mayo.bmi.bic.bobcat.api.CatalogAdminAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogAdminAPI_ServiceLocator;
import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI_ServiceLocator;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI_ServiceLocator;
import edu.mayo.bmi.bic.bobcat.api.CatalogSearchAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogSearchAPI_ServiceLocator;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;

@ManagedResource
public class LocalCatalogInstanceImpl implements CatalogInstance {
  private String host;

  public static final String URL_ONTOLOGY = "CatalogAPI/services/CatalogOntAPISOAP";
  public static final String URL_SEARCH = "CatalogAPI/services/CatalogSearchAPISOAP";
  public static final String URL_ENTRIES = "CatalogAPI/services/CatalogEntryAPISOAP";
  public static final String URL_ADMIN = "CatalogAPI/services/CatalogAdminAPISOAP";

  private CatalogSearchAPI catalogSearchApi = null;
  private CatalogOntAPI catalogOntologyApi = null;
  private CatalogEntryAPI catalogEntryApi = null;
  private CatalogAdminAPI catalogAdminApi = null;
  private boolean forceUpdate = true;

  @ManagedOperation
  public synchronized void reload() {
    initCatalog(true);
  }

  @ManagedOperation
  public synchronized void setHost(final String host) {
    this.host = host;
    this.forceUpdate = true;
  }

  private synchronized void initCatalog(final boolean isForceUpdate) {
    try {
      // Search API
      if(catalogSearchApi == null || isForceUpdate) {
        catalogSearchApi = new CatalogSearchAPI_ServiceLocator().getCatalogSearchAPISOAP(new URL(host + URL_SEARCH));
      }

      // Ontology API
      if(catalogOntologyApi == null || isForceUpdate) {
        catalogOntologyApi = new CatalogOntAPI_ServiceLocator().getCatalogOntAPISOAP(new URL(host + URL_ONTOLOGY));
      }

      // Entries API
      if(catalogEntryApi == null || isForceUpdate) {
        catalogEntryApi = new CatalogEntryAPI_ServiceLocator().getCatalogEntryAPISOAP(new URL(host + URL_ENTRIES));
      }

      if(catalogAdminApi == null || isForceUpdate) {
        catalogAdminApi = new CatalogAdminAPI_ServiceLocator().getCatalogAdminAPISOAP(new URL(host + URL_ADMIN));
      }

      this.forceUpdate = false;
    } catch(final MalformedURLException e) {
      throw new IllegalArgumentException(e);
    } catch(final ServiceException e) {
      throw new IllegalStateException(e);
    }
  }

  public CatalogEntryAPI getCatalogEntryAPI() {
    initCatalog(forceUpdate);
    return catalogEntryApi;
  }

  public CatalogOntAPI getCatalogOntAPI() {
    initCatalog(forceUpdate);
    return catalogOntologyApi;
  }

  public CatalogSearchAPI getCatalogSearchAPI() {
    initCatalog(forceUpdate);
    return catalogSearchApi;
  }

  public CatalogAdminAPI getCatalogAdminAPI() {
    initCatalog(forceUpdate);
    return catalogAdminApi;
  }

  public String getId() {
    return host;
  }

}
