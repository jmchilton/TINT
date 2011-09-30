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

import java.io.InputStream;

import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.Status;
import org.globus.gsi.GlobusCredential;

import edu.umn.msi.tropix.grid.io.transfer.TransferUtils;

public class TransferUtilsGridImpl implements TransferUtils {

  public InputStream get(final TransferServiceContextReference reference, final GlobusCredential proxy) throws Exception {
    TransferServiceContextClient tClient = null;
    InputStream inputStream = null;
    if(proxy == null) {
      tClient = new TransferServiceContextClient(reference.getEndpointReference());
      inputStream = TransferClientHelper.getData(tClient.getDataTransferDescriptor());
    } else {
      tClient = new TransferServiceContextClient(reference.getEndpointReference(), proxy);
      inputStream = TransferClientHelper.getData(tClient.getDataTransferDescriptor(), proxy);
    }
    return inputStream;
  }

  public void put(final InputStream inputStream, final TransferServiceContextReference reference, final GlobusCredential proxy) throws Exception {
    TransferServiceContextClient tClient = null;
    if(proxy == null) {
      tClient = new TransferServiceContextClient(reference.getEndpointReference());
      TransferClientHelper.putData(inputStream, inputStream.available(), tClient.getDataTransferDescriptor());
    } else {
      tClient = new TransferServiceContextClient(reference.getEndpointReference(), proxy);
      TransferClientHelper.putData(inputStream, inputStream.available(), tClient.getDataTransferDescriptor(), proxy);
    }
    inputStream.close();
    tClient.setStatus(Status.Staged);
  }

  /*
  public void get(final OutputStream outputStream, final TransferServiceContextReference reference, final GlobusCredential proxy) {
    final InputStream inputStream = get(reference, proxy);
    try {
      IO_UTLS.copyLarge(inputStream, outputStream);
    } finally {
      IO_UTLS.closeQuietly(inputStream);
    }
  }
   */

}
