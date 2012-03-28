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

package edu.umn.msi.tropix.grid.io.transfer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import com.google.common.annotations.VisibleForTesting;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.TransferContextFactory;
import edu.umn.msi.tropix.grid.io.transfer.TransferResourceContextFactory;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClient;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClients;
import edu.umn.msi.tropix.transfer.types.CaGridTransferResource;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.NamedTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

class TransferResourceContextFactoryImpl implements TransferResourceContextFactory {
  private static final Log LOG = LogFactory.getLog(TransferResourceContextFactoryImpl.class);
  private final TransferContextFactory<Credential> transferContextFactory;
  private final HttpTransferClient httpTransferClient;

  TransferResourceContextFactoryImpl() {
    this(new TransferContextFactoryImpl(), HttpTransferClients.getInstrumentableInstance());
  }
  
  @VisibleForTesting
  TransferResourceContextFactoryImpl(final TransferContextFactory<Credential> transferContextFactory, final HttpTransferClient httpTransferClient) {
    this.transferContextFactory = transferContextFactory;
    this.httpTransferClient = httpTransferClient;
  }

  public InputContext getDownloadContext(final TransferResource resource, final Credential credential) {
    if(resource instanceof CaGridTransferResource) {
      final CaGridTransferResource caGridResource = (CaGridTransferResource) resource;
      final TransferServiceContextReference transferServiceContextReference = caGridResource.getTransferServiceContextReference();
      return transferContextFactory.getDownloadContext(transferServiceContextReference, credential);
    } else if(resource instanceof HttpTransferResource) {
      final HttpTransferResource httpTransferResource = (HttpTransferResource) resource;
      final String url = httpTransferResource.getUrl();
      LOG.trace("Preparing input context for transfer URL " + url);
      return httpTransferClient.getInputContext(url);
    } else if(resource instanceof NamedTransferResource) {
      final TransferResource delegateTransferResource = ((NamedTransferResource) resource).getDelegateTransferResource();
      return getDownloadContext(delegateTransferResource, credential);
    } else {
      throw new IllegalArgumentException("TransferResource's of type " + resource.getClass() + " are not currently supported.");
    }
  }

  public OutputContext getUploadContext(final TransferResource resource, final Credential credential) {
    if(resource instanceof CaGridTransferResource) {
      final CaGridTransferResource caGridResource = (CaGridTransferResource) resource;
      final TransferServiceContextReference transferServiceContextReference = caGridResource.getTransferServiceContextReference();
      return transferContextFactory.getUploadContext(transferServiceContextReference, credential);
    } else if(resource instanceof HttpTransferResource) {
      final HttpTransferResource httpTransferResource = (HttpTransferResource) resource;
      final String url = httpTransferResource.getUrl();
      LOG.trace("Preparing output context for transfer URL " + url);
      return httpTransferClient.getOutputContext(url);
    } else if(resource instanceof NamedTransferResource){
      final TransferResource delegateTransferResource = ((NamedTransferResource) resource).getDelegateTransferResource();
      return getUploadContext(delegateTransferResource, credential);
    } else {
      throw new IllegalArgumentException("TransferResource's of type " + resource.getClass() + " are not currently supported.");
    }
  }

}
