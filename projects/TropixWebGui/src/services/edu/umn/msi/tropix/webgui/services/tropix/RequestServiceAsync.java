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

package edu.umn.msi.tropix.webgui.services.tropix;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Request;

public interface RequestServiceAsync {
  void getActiveRequests(AsyncCallback<Collection<Request>> callback);

  void completeRequest(String requestId, AsyncCallback<Void> callback);

  void setReport(String requestId, String report, AsyncCallback<Void> callback);

  void request(RequestBean requestBean, AsyncCallback<Void> callback);

  void getOutgoingRequests(AsyncCallback<Collection<InternalRequest>> callback);

  void getStatusReport(String requestId, AsyncCallback<String> callback);

  void getLocalStatusReport(String requestId, AsyncCallback<String> callback);

  void getExpandedRequest(String requestId, AsyncCallback<Request> callback);

}
