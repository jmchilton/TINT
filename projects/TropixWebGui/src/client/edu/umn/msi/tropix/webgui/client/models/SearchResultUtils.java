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

package edu.umn.msi.tropix.webgui.client.models;

import java.util.Date;

import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectWithContext;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.services.object.SearchResult;

public class SearchResultUtils {
  
  @SuppressWarnings("deprecation")
  public static ListGridRecord toRecord(final SearchResult searchResult) {
    final ListGridRecord record = new ListGridRecord();
    record.setAttribute("id", searchResult.getTropixObject().getId());
    record.setAttribute("name", StringUtils.sanitize(searchResult.getTropixObject().getName()));
    record.setAttribute("description", StringUtils.sanitize(searchResult.getTropixObject().getDescription()));
    record.setAttribute("icon", ModelFunctions.getIconFunction16().apply(searchResult.getTropixObject()));
    if(searchResult.getTropixObject().getCreationTime() != null) {      
      final Date creationDate = new Date(Long.parseLong(searchResult.getTropixObject().getCreationTime()));
      // Deprecated methods needed because GWT lacks java.util.Calandar class...
      creationDate.setHours(0);
      creationDate.setMinutes(0);
      creationDate.setSeconds(0);
      record.setAttribute("creationDate", creationDate);
    }
    final String ownerDisplay = searchResult.getOwner().substring(searchResult.getOwner().lastIndexOf('=') + 1);
    record.setAttribute("owner", ownerDisplay);
    record.setAttribute("object", new TropixObjectWithContext<TropixObject>(searchResult.getTropixObjectContext(), searchResult.getTropixObject()));
    return record;
  }
}
