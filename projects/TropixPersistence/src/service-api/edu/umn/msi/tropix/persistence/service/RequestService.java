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

package edu.umn.msi.tropix.persistence.service;

import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.aop.Modifies;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.Reads;
import edu.umn.msi.tropix.persistence.aop.UserId;
import edu.umn.msi.tropix.persistence.service.request.RequestSubmission;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;

public interface RequestService {
  String getReport(RequestId externalRequestId);

  void setupRequestFile(String requestId, TropixFile tropixFile);

  Request createOrUpdateRequest(RequestId requestId, RequestSubmission requestSubmission);

  InternalRequest loadInternalRequest(RequestId requestId);

  Request loadRequest(String requestId);

  void updateState(RequestId requestId, String string);

  @PersistenceMethod Request[] getActiveRequests(@UserId String gridId);

  boolean isInternalRequest(String requestId);

  @PersistenceMethod InternalRequest setupInternalRequest(@UserId String gridId, InternalRequest request, @Modifies String folderId);

  @PersistenceMethod void setReport(@UserId String gridId, @Modifies String requestId, String report);

  @PersistenceMethod void updateState(@UserId String gridId, @Modifies String requestId, String string);

  @PersistenceMethod String getReport(@UserId String gridId, @Reads String requestId);

  @PersistenceMethod String getContact(@UserId String gridId, @Reads String requestId);

  @PersistenceMethod InternalRequest[] getOutgoingRequests(@UserId String gridId);

  @PersistenceMethod InternalRequest loadInternalRequest(@UserId String gridId, @Reads String internalRequestId);

  @PersistenceMethod String getServiceInfo(@UserId String gridId, @Reads String requestId);

}
