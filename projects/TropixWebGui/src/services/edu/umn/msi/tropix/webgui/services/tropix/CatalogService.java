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

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.services.object.Permission;

@RemoteServiceRelativePath("CatalogService.rpc")
@GWTRequestMapping("/webgui/CatalogService.rpc")
public interface CatalogService extends RemoteService {
  public static class Util {
    public static CatalogServiceAsync getInstance() {
      return (CatalogServiceAsync) GWT.create(CatalogService.class);
    }
  }

  List<Provider> getMyProviders();

  void createProvider(Provider provider);

  List<Permission> getPermissions(String providerCatalogId);

  void addProviderPermissionForUser(String providerCatalogId, String userId);

  void removeProviderPermissionForUser(String providerCatalogId, String userId);

  void addProviderPermissionForGroup(String providerCatalogId, String groupId);

  void removeProviderPermissionForGroup(String providerCatalogId, String groupId);

  boolean canModify(String providerCatalogId);

  void addFreeTextField(final String name);

  void addEnumField(String name, List<String> values);

  void addCategory(String name, String description);

}
