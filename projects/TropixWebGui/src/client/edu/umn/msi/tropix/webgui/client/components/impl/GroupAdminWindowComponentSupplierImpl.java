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

import java.util.Set;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.GroupSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.SelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.session.GroupService;

public class GroupAdminWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userSelectionWindowComponentSupplier;
  
  @Inject
  public void setUserSelectionWindowComponentSupplier(final Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userSelectionWindowComponentSupplier) {
    this.userSelectionWindowComponentSupplier = userSelectionWindowComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new GroupAdminWindowComponentImpl();
  }

  class GroupAdminWindowComponentImpl extends WindowComponentImpl<Window> {

    GroupAdminWindowComponentImpl() {
      this.setWidget(PopOutWindowBuilder.titled("Group Admin").sized(600, 500).withContents(getContents()).get());
    }

    private Canvas getContents() {
      final SectionStack sectionStack = new SectionStack();
      sectionStack.addSection(this.getAddGroupSection());
      sectionStack.addSection(this.getEditGroupSection());
      return sectionStack;
    }

    private SectionStackSection getEditGroupSection() {
      final SectionStackSection section = new SectionStackSection("Edit Groups");
      final GroupSelectionComponent groupSelectionComponent = groupSelectionComponentSupplier.get();
      final ClientListGrid membersGrid = new ClientListGrid();
      membersGrid.setWidth100();
      membersGrid.setHeight("45%");
      membersGrid.setFields(new ListGridField("id"));
      groupSelectionComponent.addSelectionListener(new Listener<Group>() {
        public void onEvent(final Group group) {
          GroupService.Util.getInstance().getUsers(group.getId(), new AsyncCallbackImpl<Set<User>>() {
            @Override
            public void onSuccess(final Set<User> users) {
              for(final User user : users) {
                final ListGridRecord userRecord = new ListGridRecord();
                userRecord.setAttribute("id", user.getCagridId());
                membersGrid.getClientDataSource().addData(userRecord);
              }
            }
          });
        }
      });
      final VLayout vLayout = new VLayout();
      vLayout.addMember(groupSelectionComponent.get());
      vLayout.addMember(membersGrid);
      final Button addButton = SmartUtils.getButton("Add User", Resources.PERSON_ABSOLUTE);
      addButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          try {
          SelectionWindowComponent<GridUser, ? extends Window> selectionComponent = userSelectionWindowComponentSupplier.get();
          selectionComponent.setSelectionCallback(new Listener<GridUser>() {
            public void onEvent(final GridUser gridUser) {
              if(gridUser != null) {
                final String groupId = groupSelectionComponent.getSelection().getId();
                final String userGridId = gridUser.getGridId();
                System.out.println("Adding user " + userGridId + " to group " + groupId);
                GroupService.Util.getInstance().addUserToGroup(userGridId, groupId, new AsyncCallbackImpl<Void>());                
              }
            }
          });
          selectionComponent.execute();
          } catch(RuntimeException e) {
            e.printStackTrace();
          }
        }
      });
      final Button removeButton = SmartUtils.getButton("Remove Users", Resources.CROSS);
      removeButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final ListGridRecord[] selectedUserRecords = membersGrid.getSelection();
          final String groupId = groupSelectionComponent.getSelection().getId();
          for(final ListGridRecord selectedUserRecord : selectedUserRecords) {
            final String userId = selectedUserRecord.getAttribute("id");
            GroupService.Util.getInstance().removeUserFromGroup(userId, groupId, new AsyncCallbackImpl<Void>());
          }
        }
      });
      final HLayout bars = new HLayout();
      bars.addMember(removeButton);
      bars.addMember(addButton);
      vLayout.addMember(bars);
      section.addItem(vLayout);
      return section;
    }

    private SectionStackSection getAddGroupSection() {
      final SectionStackSection section = new SectionStackSection("Create Group");
      final DynamicForm form = new DynamicForm();
      final TextItem textItem = new TextItem("Name");
      form.setFields(textItem);
      final Button button = SmartUtils.getButton("Create", Resources.ADD);
      button.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          GroupService.Util.getInstance().createGroup(form.getValueAsString("Name"), new AsyncCallbackImpl<Group>());
        }
      });
      final VLayout vLayout = new VLayout();
      vLayout.addMember(form);
      vLayout.addMember(button);
      section.addItem(vLayout);
      return section;
    }
  }

  private Supplier<GroupSelectionComponent> groupSelectionComponentSupplier;

  @Inject
  public void setGroupSelectionComponentSupplier(final Supplier<GroupSelectionComponent> groupSelectionComponentSupplier) {
    this.groupSelectionComponentSupplier = groupSelectionComponentSupplier;
  }
  
}
