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

package edu.umn.msi.tropix.storage.service.impl;

import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;

public interface TransferServiceContextReferenceFactory {
  TransferServiceContextReference createTransferContext(final DataDescriptor dd, final DataStagedCallback callback) throws RemoteException;

  TransferServiceContextReference createTransferContext(final InputStream inputStream, final DataDescriptor dd) throws RemoteException;
  
  TransferServiceContextReference createTransferContext(final File file, final DataDescriptor dd, boolean deleteOnCompletion) throws RemoteException;
}
