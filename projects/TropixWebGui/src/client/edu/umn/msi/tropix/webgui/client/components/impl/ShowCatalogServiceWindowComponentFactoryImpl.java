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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;

public class ShowCatalogServiceWindowComponentFactoryImpl extends CompositeWindowComponentFactoryImpl<ServiceBean, Window> {

  @Inject
  public ShowCatalogServiceWindowComponentFactoryImpl(@Named("showCatalogService") final ComponentFactory<ServiceBean, ? extends CanvasComponent<? extends Canvas>> canvasComponentFactory) {
    super.setWindowSupplier(PopOutWindowBuilder.titled("Catalog Service Properties"));
    super.setBaseComponentFactory(canvasComponentFactory);
  }
}
