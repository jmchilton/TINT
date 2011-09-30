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

package edu.umn.msi.tropix.webgui.server;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.client.search.TropixSearchClient;
import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.tropix.GridSearchService;

@ManagedBean
public class GridSearchServiceImpl implements GridSearchService {
  private final TropixSearchClient tropixSearchClient;
  private final UserSession userSession;

  @Inject
  GridSearchServiceImpl(final TropixSearchClient tropixSearchClient, final UserSession userSession) {
    this.tropixSearchClient = tropixSearchClient;
    this.userSession = userSession;
  }

  @ServiceMethod(readOnly = true)
  public List<GridData> getChildItems(final String serviceUrl, final String dataId) {
    return this.tropixSearchClient.getChildItems(serviceUrl, this.userSession.getProxy(), dataId);
  }

  @ServiceMethod(readOnly = true)
  public List<GridData> getTopLevelItems(final String serviceUrl, final String ownerId) {
    return this.tropixSearchClient.getTopLevelItems(serviceUrl, this.userSession.getProxy(), ownerId);
  }

}
