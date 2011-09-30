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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.client.catalog.beans.Category;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

public class CatalogGlobals {
  private static HashMap<String, Category> categories;

  public static void init() {
    CatalogServiceDefinition.Util.getInstance().getCategories(new AsyncCallback<HashMap<String, Category>>() {
      public void onFailure(final Throwable caught) {
        GWT.log("error in categories " + caught.getMessage(), caught);
      }

      public void onSuccess(final HashMap<String, Category> result) {
        GWT.log("categories populated", null);
        CatalogGlobals.categories = result;
      }
    });
  }

  public static HashMap<String, Category> getCategories() {
    return CatalogGlobals.categories;
  }
}
