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

package edu.umn.msi.tropix.webgui.client.search;

import java.util.Collection;
import java.util.LinkedList;

import edu.umn.msi.gwt.mvc.Model;

public class SearchCollectionModel extends Model {

  public SearchModel getSearchModel(final String id) {
    SearchModel model = null;
    final int numJobs = this.getChildCount();
    for(int i = 0; i < numJobs; i++) {
      final SearchModel searchModel = (SearchModel) this.getChild(i);
      if(searchModel.getAsString("id").equals(id)) {
        model = searchModel;
        break;
      }
    }
    return model;
  }

  public Iterable<SearchModel> getSearchModels(final SearchModel.SearchType searchType) {
    final Collection<SearchModel> searchModels = new LinkedList<SearchModel>();
    for(final Model model : this.children) {
      final SearchModel searchModel = (SearchModel) model;
      if(searchModel.getSearchType().equals(searchType)) {
        searchModels.add(searchModel);
      }
    }
    return searchModels;
  }

}
