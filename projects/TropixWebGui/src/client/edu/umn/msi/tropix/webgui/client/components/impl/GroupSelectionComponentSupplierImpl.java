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
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.GroupSelectionComponent;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandSelectionChangedHandlerImpl;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.services.session.GroupService;

public class GroupSelectionComponentSupplierImpl implements Supplier<GroupSelectionComponent> {

  private static class GroupSelectionComponentImpl extends SelectionComponentBaseImpl<Group, ListGrid> implements GroupSelectionComponent {
    
    GroupSelectionComponentImpl() {
      final ClientListGrid grid = new ClientListGrid();
      this.setWidget(grid);
      final ListGridField field = new ListGridField("Name");
      field.setType(ListGridFieldType.TEXT);
      final ListGridField idField = new ListGridField("id");
      idField.setType(ListGridFieldType.TEXT);
      idField.setHidden(true);
      this.get().setFields(field, idField);
      this.get().setAlternateRecordStyles(true);
      this.get().addSelectionChangedHandler(new CommandSelectionChangedHandlerImpl(getOnSelectionChangedCommand()));
      GroupService.Util.getInstance().getGroups(new AsyncCallbackImpl<Set<Group>>() {
        @Override
        public void onSuccess(final Set<Group> groups) {
          for(final Group group : groups) {
            final ListGridRecord groupRecord = new ListGridRecord();
            groupRecord.setAttribute("Name", group.getName());
            groupRecord.setAttribute("id", group.getId());
            groupRecord.setAttribute("group", group);
            grid.getClientDataSource().addData(groupRecord);
          }
        }
      });
    } 

    public Group getSelection() {
      Group selectedGroup = null;
      final ListGridRecord record = this.get().getSelectedRecord();
      if(record != null) {
        selectedGroup = (Group) record.getAttributeAsObject("group");
      }
      return selectedGroup;
    }
  }

  public GroupSelectionComponent get() {
    final GroupSelectionComponentImpl component = new GroupSelectionComponentImpl();
    return component;
  }
}
