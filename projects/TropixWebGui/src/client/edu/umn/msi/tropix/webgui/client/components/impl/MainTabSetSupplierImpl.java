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

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.client.widgets.ContextAwareTab;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class MainTabSetSupplierImpl extends WidgetSupplierImpl<TabSet> {
  private Supplier<Tab> jobsTabSupplier, searchResultsTabSupplier;
  private final ModuleManager moduleManager;

  @Inject
  public MainTabSetSupplierImpl(final ModuleManager moduleManager) {
    this.moduleManager = moduleManager;
  }

  @Inject
  public void setJobsTabSupplier(@Named("jobs") final Supplier<Tab> jobsTabSupplier) {
    this.jobsTabSupplier = jobsTabSupplier;
  }

  @Inject
  public void setSearchResultsTabSupplier(@Named("searchResults") final Supplier<Tab> searchResultsTabSupplier) {
    this.searchResultsTabSupplier = searchResultsTabSupplier;
  }

  @Override
  protected void initWidget() {
    final TabSet tabSet = new TabSet();
    if(moduleManager.apply(jobsTabSupplier)) {
      ContextAwareTab.addTabToTabSet(jobsTabSupplier.get(), tabSet);
    }
    if(moduleManager.apply(searchResultsTabSupplier)) {
      ContextAwareTab.addTabToTabSet(searchResultsTabSupplier.get(), tabSet);
    }    
    setWidget(tabSet);
  }

}
