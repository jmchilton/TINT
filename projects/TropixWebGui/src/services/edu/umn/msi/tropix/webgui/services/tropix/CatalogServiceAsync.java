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

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.services.object.Permission;

public interface CatalogServiceAsync {
  void getPermissions(String objectId, AsyncCallback<List<Permission>> callback);

  void addProviderPermissionForUser(String objectId, String userId, AsyncCallback<Void> callback);

  void removeProviderPermissionForUser(String objectId, String userId, AsyncCallback<Void> callback);

  void addProviderPermissionForGroup(String objectId, String groupId, AsyncCallback<Void> callback);

  void removeProviderPermissionForGroup(String objectId, String groupId, AsyncCallback<Void> callback);

  void canModify(String providerCatalogId, AsyncCallback<Boolean> callback);

  void getMyProviders(AsyncCallback<List<Provider>> callback);

  void createProvider(Provider provider, AsyncCallback<Void> callback);

  void addFreeTextField(final String name, AsyncCallback<Void> callback);

  void addEnumField(String name, List<String> values, AsyncCallback<Void> callback);

  void addCategory(String name, String description, AsyncCallback<Void> callback);
}
