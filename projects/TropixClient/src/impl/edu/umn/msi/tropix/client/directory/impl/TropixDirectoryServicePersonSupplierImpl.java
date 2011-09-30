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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import info.minnesotapartnership.tropix.directory.TropixDirectoryService;
import info.minnesotapartnership.tropix.directory.models.Person;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class TropixDirectoryServicePersonSupplierImpl implements Supplier<Multimap<String, Person>> {
  private static final Log LOG = LogFactory.getLog(TropixDirectoryServicePersonSupplierImpl.class);
  private Map<String, String> institutionToServiceAddressMap;
  private GridServiceFactory<TropixDirectoryService> serviceFactory;
  private Supplier<Credential> proxySupplier;

  public Multimap<String, Person> get() {
    final Credential proxy = proxySupplier.get();
    //LOG.trace("Host proxy obtained " + proxy);
    Multimap<String, Person> personMap = HashMultimap.create();
    for(final Entry<String, String> institutionAddressEntry : institutionToServiceAddressMap.entrySet()) {
      final String institution = institutionAddressEntry.getKey();
      final String address = institutionAddressEntry.getValue();
      try {
        LOG.trace("Obtaining person array from directory service " + address);
        final TropixDirectoryService directoryService = serviceFactory.getService(address, proxy);
        final Person[] personArray = directoryService.getUsers();
        if(personArray != null) {
          personMap.putAll(institution, Arrays.asList(personArray));
        } else {
          LOG.info("Directory service " + address + " returned a null Person array.");
        }
      } catch(final RuntimeException e) {
        ExceptionUtils.logQuietly(LOG, e);
        continue;
      }
    }
    LOG.trace("Returning person multimap");
    return personMap;
  }


  public void setInstitutionToServiceAddressMap(final Map<String, String> institutionToServiceAddressMap) {
    this.institutionToServiceAddressMap = institutionToServiceAddressMap;
  }

  public void setServiceFactory(final GridServiceFactory<TropixDirectoryService> serviceFactory) {
    this.serviceFactory = serviceFactory;
  }

  public void setProxySupplier(final Supplier<Credential> proxySupplier) {
    this.proxySupplier = proxySupplier;
  }  

}
