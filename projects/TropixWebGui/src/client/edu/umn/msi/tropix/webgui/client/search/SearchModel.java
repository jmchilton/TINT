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

import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.gwt.mvc.Model;

public class SearchModel extends Model {
  enum SearchType {
    LOCAL, GRID, CATALOG
  };

  private static int lastId = 0;
  private Layout layout = null;
  private SearchType searchType;
  private Object results;

  public void setSearchType(final SearchType searchType) {
    this.searchType = searchType;
  }

  public SearchType getSearchType() {
    return this.searchType;
  }

  public void setResults(final Object results) {
    this.results = results;
  }

  public Object getResults() {
    return this.results;
  }

  public SearchModel(final String searchName, final SearchType type) {
    super();
    this.searchType = type;
    this.properties.put("status", "Searching...");
    this.properties.put("name", searchName);
    this.properties.put("id", "search#" + SearchModel.lastId++);
  }

  public Layout getLayout() {
    return this.layout;
  }

  public void complete(final Layout layout) {
    this.properties.put("status", "Complete");
    this.layout = layout;
    this.fireEvent(Model.Update, this);
  }
  
  public boolean isFailed() {
    return this.properties.get("status").equals("Failed");
  }

  public void fail() {
    this.properties.put("status", "Failed");
    this.fireEvent(Model.Update, this);
  }

  public boolean isComplete() {
    return this.properties.get("status").equals("Complete");
  }

  public String getId() {
    return this.getAsString("id");
  }

}
