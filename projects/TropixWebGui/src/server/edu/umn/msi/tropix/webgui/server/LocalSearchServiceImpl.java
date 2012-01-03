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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.service.SearchService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.object.LocalSearchService;
import edu.umn.msi.tropix.webgui.services.object.SearchResult;
import edu.umn.msi.tropix.webgui.services.object.SharedFolder;

@ManagedBean
public class LocalSearchServiceImpl implements LocalSearchService {
  private static final Log LOG = LogFactory.getLog(LocalSearchServiceImpl.class);
  private final SearchService searchService;
  private final TropixObjectService tropixObjectService;
  private final UserSession session;
  private final BeanSanitizer beanSanitizer;

  @Inject
  LocalSearchServiceImpl(final SearchService searchService, final TropixObjectService tropixObjectService, final UserSession session, final BeanSanitizer beanSanitizer) {
    this.searchService = searchService;
    this.tropixObjectService = tropixObjectService;
    this.session = session;
    this.beanSanitizer = beanSanitizer;
  }

  @ServiceMethod(readOnly = true)
  private List<SearchResult> getResults(final TropixObject[] resultObjects) {
    final ArrayList<SearchResult> results = new ArrayList<SearchResult>(resultObjects.length);
    for(final TropixObject resultObject : resultObjects) {
      try {
        results.add(new SearchResult(this.beanSanitizer.sanitize(resultObject), null, this.tropixObjectService.getOwnerId(resultObject.getId())));
      } catch(final RuntimeException e) {
        ExceptionUtils.logQuietly(LocalSearchServiceImpl.LOG, e, "Failed to add search result with id " + resultObject.getId());
      }
    }
    return results;
  }

  @ServiceMethod(readOnly = true)
  public List<SearchResult> fullSearch(final String name, final String description, final String ownerId, final TropixObjectType objectType) {
    return this.getResults(this.searchService.searchObjects(this.session.getGridId(), name, description, ownerId, objectType));
  }

  @ServiceMethod(readOnly = true)
  public List<SharedFolder> getSharedFolders() {
    final VirtualFolder[] folders = this.tropixObjectService.getSharedFolders(this.session.getGridId());
    final ArrayList<SharedFolder> sharedFolders = new ArrayList<SharedFolder>(folders.length);
    for(final VirtualFolder folder : folders) {
      try {
        sharedFolders.add(new SharedFolder(this.beanSanitizer.sanitize(folder), this.tropixObjectService.getOwnerId(folder.getId())));
      } catch(final RuntimeException e) {
        ExceptionUtils.logQuietly(LocalSearchServiceImpl.LOG, e, "Failed to add shared folder with id " + folder.getId());
      }
    }
    return sharedFolders;
  }

  @ServiceMethod(readOnly = true)
  public List<SearchResult> quickSearch(final String query) {
    return this.getResults(this.searchService.quickSearchObjects(this.session.getGridId(), query));
  }

}
