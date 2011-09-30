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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.List;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.webgui.client.catalog.CatalogServiceController;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.ResultComponent;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;

public class CatalogResultWindowComponentSupplierImpl implements Supplier<ResultComponent<List<ServiceBean>, Window>> {
  private Supplier<CatalogServiceController> catalogServiceControllerSupplier;

  @Inject
  public void setCatalogServiceControllerSupplier(final Supplier<CatalogServiceController> catalogServiceControllerSupplier) {
    this.catalogServiceControllerSupplier = catalogServiceControllerSupplier;
  }

  public ResultComponent<List<ServiceBean>, Window> get() {
    return new CatalogResultWindowComponentImpl(this.catalogServiceControllerSupplier.get());
  }

  static class CatalogResultWindowComponentImpl extends WindowComponentImpl<Window> implements ResultComponent<List<ServiceBean>, Window> {
    private final CatalogServiceController controller;

    CatalogResultWindowComponentImpl(final CatalogServiceController controller) {
      this.controller = controller;

      this.setWidget(PopOutWindowBuilder.titled("Lab Service Search Results").sized(600, 600).hideOnClose().get());
      this.get().addItem(this.controller.get());
    }

    public void setResults(final List<ServiceBean> results) {
      this.controller.populateSearchResults(results);
    }
  }

}
