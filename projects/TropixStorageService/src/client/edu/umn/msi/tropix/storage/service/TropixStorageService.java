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

package edu.umn.msi.tropix.storage.service;

import java.rmi.RemoteException;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

public interface TropixStorageService {
  TransferServiceContextReference prepareDownload(String id) throws RemoteException;

  TransferServiceContextReference prepareUpload(String id) throws RemoteException;

  boolean delete(String id) throws RemoteException;

  boolean canDownload(String id) throws RemoteException;

  boolean canUpload(String id) throws RemoteException;

  boolean canDelete(String id) throws RemoteException;

  boolean exists(String id) throws RemoteException;
}
