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

package edu.umn.msi.tropix.webgui.client.catalog;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.client.catalog.beans.CustomQueryBean;
import edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogSearch;

public class CatalogUtils {
  public static void getEntriesFromProvider(final String providerId, final String catalogId, final AsyncCallback<List<ServiceBean>> callback) {
    final CustomQueryBean queryBean = new CustomQueryBean();
    queryBean.setCatalogId(catalogId);
    queryBean.setProivderID(providerId);
    CatalogSearch.Util.getInstance().advancedSearch(queryBean, callback);
  }

  public static String getValuesString(@Nullable final Iterable<FieldValue> values) {
    final StringBuilder valuesBuilder = new StringBuilder();
    if(values != null) {
      for(final FieldValue value : values) {
        if(valuesBuilder.length() > 0) {
          valuesBuilder.append(", ");
        }
        valuesBuilder.append(value.getValue());
      }
    }
    return valuesBuilder.toString();
  }
  
  public static String getValuesString(@Nullable final FieldValue[] values) {
    final StringBuilder valuesBuilder = new StringBuilder();
    if(values != null) {
      for(final FieldValue value : values) {
        if(valuesBuilder.length() > 0) {
          valuesBuilder.append(", ");
        }
        valuesBuilder.append(value.getValue());
      }
    }
    return valuesBuilder.toString();
  }
}
