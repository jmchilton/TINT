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



import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class CaGridTropixDirectoryServicePersonSupplierImpl extends BaseTropixDirectoryServicePersonSupplierImpl implements Supplier<Multimap<String, Person>> {
  private GridServiceFactory<TropixDirectoryService> serviceFactory;
  private Supplier<Credential> proxySupplier;

  public void setServiceFactory(final GridServiceFactory<TropixDirectoryService> serviceFactory) {
    this.serviceFactory = serviceFactory;
  }

  public void setProxySupplier(final Supplier<Credential> proxySupplier) {
    this.proxySupplier = proxySupplier;
  }

  @Override
  protected TropixDirectoryService getService(final String address) {
    final Credential proxy = proxySupplier.get();
    final TropixDirectoryService directoryService = serviceFactory.getService(address, proxy);
    return directoryService;
  }

}
