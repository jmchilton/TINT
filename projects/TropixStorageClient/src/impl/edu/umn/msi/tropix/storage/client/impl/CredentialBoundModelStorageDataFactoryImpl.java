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

package edu.umn.msi.tropix.storage.client.impl;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.CredentialBoundModelStorageDataFactory;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;

public class CredentialBoundModelStorageDataFactoryImpl implements CredentialBoundModelStorageDataFactory {
  private ModelStorageDataFactory modelStorageDataFactory;
  private Supplier<Credential> credentialSupplier;

  public ModelStorageData getStorageData(final TropixFile file) {
    return modelStorageDataFactory.getStorageData(file, credentialSupplier.get());
  }

  public ModelStorageData getStorageData(final String dataIdentifier, final String serviceUrl) {
    return modelStorageDataFactory.getStorageData(dataIdentifier, serviceUrl, credentialSupplier.get());
  }

  public ModelStorageData getStorageData(final String serviceUrl) {
    return modelStorageDataFactory.getStorageData(serviceUrl, credentialSupplier.get());
  }

  public void setModelStorageDataFactory(final ModelStorageDataFactory modelStorageDataFactory) {
    this.modelStorageDataFactory = modelStorageDataFactory;
  }

  public void setCredentialSupplier(final Supplier<Credential> credentialSupplier) {
    this.credentialSupplier = credentialSupplier;
  }

}
