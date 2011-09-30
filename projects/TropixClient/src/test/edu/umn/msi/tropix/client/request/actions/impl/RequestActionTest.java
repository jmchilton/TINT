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

package edu.umn.msi.tropix.client.request.actions.impl;

import info.minnesotapartnership.tropix.request.models.RequestStatus;

import org.testng.annotations.Test;

public class RequestActionTest {

  @Test(groups = "unit")
  public void updateRequestAction() {
    final UpdateRequestAction a = new UpdateRequestAction(123L, "moo", "cow", "COMPLETE");
    assert a.getActionId() == 123L;
    assert a.getRequestService().equals("moo");
    assert a.getRequestId().equals("cow");
    assert a.getRequestStatus().equals(RequestStatus.COMPLETE);
  }

  @Test(groups = "unit")
  public void uploadRequestAction() {
    final UploadRequestAction a = new UploadRequestAction(123L, "moo", "cow", "fileid", "storage", "xml");
    assert a.getActionId() == 123L;
    assert a.getRequestService().equals("moo");
    assert a.getRequestId().equals("cow");
    assert a.getFileId().equals("fileid");
    assert a.getFileStorageService().equals("storage");
    assert a.getServiceResultXml().equals("xml");
  }

}
