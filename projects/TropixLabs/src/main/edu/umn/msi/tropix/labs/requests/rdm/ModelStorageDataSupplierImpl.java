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

package edu.umn.msi.tropix.labs.requests.rdm;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;

public class ModelStorageDataSupplierImpl implements Supplier<ModelStorageData> {
  private ModelStorageDataFactory modelStorageDataFactory = null;
  private String storageServiceUrl = null;
  private Supplier<Credential> proxySupplier = null;

  public ModelStorageData get() {
    return modelStorageDataFactory.getStorageData(storageServiceUrl, proxySupplier.get());
  }

  public void setModelStorageDataFactory(final ModelStorageDataFactory modelStorageDataFactory) {
    this.modelStorageDataFactory = modelStorageDataFactory;
  }

  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

  public void setProxySupplier(final Supplier<Credential> proxySupplier) {
    this.proxySupplier = proxySupplier;
  }

}
