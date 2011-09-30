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

import com.google.common.base.Supplier;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;

public interface CatalogServiceController extends Supplier<Layout> {

  void createNewProvider();

  void populateServiceDetails(ServiceBean serviceBean, int resultListSize, int posInList);

  void edit(ServiceBean serviceBean, boolean inResultList);

  void showSearchResults(boolean affectHistory);

  void showPrevService();

  void showNextService();

  void cancelEditService();

  void showUpdatedService(ServiceBean serviceBean, boolean doUpdateServiceInResultList);

  void reset(String message);

  void populateSearchResults(List<ServiceBean> serviceBeans);

  void createNewService();

  void displayService(int position);

  void showSearchOnly();

  void updateProviderList();

  void showProviderDetails(Provider provider);

  Canvas getShowCatalogServiceCanvas(ServiceBean serviceBean);

  void request(ServiceBean currentService);

  void showServiceDetails(ServiceBean serviceBean, List<ServiceBean> serviceBeans, int posInList);

}