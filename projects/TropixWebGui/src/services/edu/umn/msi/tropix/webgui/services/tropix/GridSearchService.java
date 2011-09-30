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

import javax.annotation.Nullable;

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.umn.msi.tropix.client.search.models.GridData;

@RemoteServiceRelativePath("GridSearchService.rpc")
@GWTRequestMapping("/webgui/GridSearchService.rpc")
public interface GridSearchService extends RemoteService {
  public static class Util {
    public static GridSearchServiceAsync getInstance() {
      return (GridSearchServiceAsync) GWT.create(GridSearchService.class);
    }
  }

  List<GridData> getTopLevelItems(String serviceUrl, @Nullable String ownerId);

  List<GridData> getChildItems(String serviceUrl, String dataId);

}
