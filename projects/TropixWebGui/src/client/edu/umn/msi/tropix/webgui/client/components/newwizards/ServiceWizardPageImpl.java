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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.ArrayList;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

public class ServiceWizardPageImpl<T extends GridService> extends WizardPageImpl<VLayout> implements ServiceWizardPage<T> {
  private T gridService;
  private final ArrayList<Listener<T>> listeners = new ArrayList<Listener<T>>();

  public ServiceWizardPageImpl(final ServiceSelectionComponent<T> serviceSelectionComponent, final String title, final String description) {
    this.setTitle(title);
    this.setDescription(description);
    serviceSelectionComponent.addSelectionListener(new Listener<T>() {
      public void onEvent(final T event) {
        ServiceWizardPageImpl.this.gridService = event;
        final boolean serviceSelected = ServiceWizardPageImpl.this.gridService != null;
        ServiceWizardPageImpl.this.setValid(serviceSelected);
        if(serviceSelected) {
          for(final Listener<T> listener : ServiceWizardPageImpl.this.listeners) {
            listener.onEvent(ServiceWizardPageImpl.this.gridService);
          }
        }
      }
    });
    serviceSelectionComponent.fetchServices();
    this.setCanvas(new VLayout());

    final ListGrid listGrid = serviceSelectionComponent.get();
    listGrid.setWidth100();
    this.getCanvas().addMember(listGrid);
  }

  public ServiceWizardPageImpl(final ServiceSelectionComponent<T> serviceSelectionComponent) {
    this(serviceSelectionComponent, "Service", "Select a service for this action");
  }

  @Override
  public boolean isValid() {
    final boolean isValid = super.isValid();
    return isValid;
  }

  public void addGridServiceChangedListener(final Listener<T> listener) {
    this.listeners.add(listener);
  }

  public T getGridService() {
    return this.gridService;
  }
}
