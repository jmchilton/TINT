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
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.layout.HLayout;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.webgui.client.components.GridUserItemComponent;
import edu.umn.msi.tropix.webgui.client.components.SelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Listener;

public class GridUserItemComponentSupplierImpl implements Supplier<GridUserItemComponent> {
  private Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userWindowComponentSupplier;

  public GridUserItemComponent get() {
    return new GridUserItemComponentImpl();
  }

  class GridUserItemComponentImpl implements GridUserItemComponent {
    private final Label ownerLabel;
    private String selectedUserId = null;
    private final CanvasItem ownerItem;

    GridUserItemComponentImpl() {
      this.ownerItem = new CanvasItem();
      this.ownerItem.setTitle("Owner");
      //ownerItem.setHeight(20);
      final HLayout ownerLayout = new HLayout();
      //ownerLayout.setHeight(20);
      ownerLayout.setMargin(0);
      ownerLayout.setAutoHeight();
      ownerLayout.setAutoWidth();
      //ownerLayout.setWidth("*");
      this.ownerLabel = new Label(ConstantsInstances.COMPONENT_INSTANCE.gridUserAnyone());
      //this.ownerLabel.setHeight(20);
      this.ownerLabel.setWrap(false);
      final Button ownerSelect = new Button(ConstantsInstances.COMPONENT_INSTANCE.gridUserChange());
      ownerSelect.setAutoFit(true);
      ownerLayout.setMembers(ownerSelect, this.ownerLabel);
      this.ownerItem.setCanvas(ownerLayout);
      ownerSelect.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          System.out.println("Clicked");
          final SelectionWindowComponent<GridUser, ? extends Window> userSelectionComponent = GridUserItemComponentSupplierImpl.this.userWindowComponentSupplier.get();
          userSelectionComponent.setSelectionCallback(new Listener<GridUser>() {
            public void onEvent(final GridUser selectedUser) {
              try {
                if(selectedUser != null) {
                  GridUserItemComponentImpl.this.selectedUserId = selectedUser.getGridId();
                  GridUserItemComponentImpl.this.ownerLabel.setContents(selectedUser.toString());
                } else {
                  GridUserItemComponentImpl.this.selectedUserId = null;
                  GridUserItemComponentImpl.this.ownerLabel.setContents(ConstantsInstances.COMPONENT_INSTANCE.gridUserAnyone());
                }
              } catch(final Exception e) {
                e.printStackTrace();
              }
            }
          });
          System.out.println("executing...");
          userSelectionComponent.execute();
          System.out.println("Clicked done");
        }
      });
    }

    public CanvasItem get() {
      return this.ownerItem;
    }

    public String getSelectedUserId() {
      return this.selectedUserId;
    }

    public String getSelectedUserLabel() {
      return this.ownerLabel.getContents();
    }
  }

  @Inject
  public void setUserWindowComponentSupplier(final Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userWindowComponentSupplier) {
    this.userWindowComponentSupplier = userWindowComponentSupplier;
  }
}

