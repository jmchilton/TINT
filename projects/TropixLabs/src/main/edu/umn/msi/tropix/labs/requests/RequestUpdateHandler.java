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

package edu.umn.msi.tropix.labs.requests;

import javax.annotation.concurrent.Immutable;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import edu.umn.msi.tropix.persistence.service.requestid.RequestId;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public interface RequestUpdateHandler {

  @Immutable
  public static class RequestResult {
    private final RequestId requestId;
    private final String fileName;
    private final String fileDescription;

    public RequestResult(final RequestId requestId, final String fileName, final String fileDescription) {
      this.requestId = requestId;
      this.fileName = fileName;
      this.fileDescription = fileDescription;
    }

    public RequestId getRequestId() {
      return this.requestId;
    }

    public String getFileName() {
      return this.fileName;
    }

    public String getFileDescription() {
      return this.fileDescription;
    }

    public String toString() {
      return "RequestResult[requstId=RequestId[extId=" + requestId.getRequestExternalId() + ",hostId=" + requestId.getRequestHostId() + "],fileName=" + fileName + ",fileDescription=" + fileDescription + "]";
    }
  }

  public enum Status {
    ACTIVE, COMPLETE;
  }

  TransferResource handleResult(RequestResult result);

  void handleStatus(RequestId requestId, Status status);

}
