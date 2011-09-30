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
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.FileTypeService;

public class ManageFileTypesWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>>  {
  private Session session;
  
  @Inject
  public ManageFileTypesWindowComponentSupplierImpl(final Session session) {
    this.session = session;
  }
  
  public WindowComponent<Window> get() {
    return new ManageFileTypesWindowComponentImpl(session);
  }

  private static class ManageFileTypesWindowComponentImpl extends WindowComponentImpl<Window> {
    private final ListGrid typesGrid = new ClientListGrid("id");

    private void addRecord(final FileType fileType) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("name", fileType.getShortName());
      record.setAttribute("extension", fileType.getExtension());
      record.setAttribute("id", fileType.getId());
      record.setAttribute("object", fileType);
      typesGrid.addData(record);      
    }
    
    ManageFileTypesWindowComponentImpl(final Session session) {
      final VLayout layout = new VLayout();
      final ListGridField nameField = new ListGridField("name", "Short Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("80%");
      nameField.setCanEdit(true);
      
      final ListGridField extensionField = new ListGridField("extension", "Extension");
      extensionField.setType(ListGridFieldType.TEXT);
      extensionField.setWidth("20%");
      extensionField.setCanEdit(false);
      
      typesGrid.setCanEdit(true);
      typesGrid.setMinHeight(100);
      typesGrid.setAutoFitMaxRecords(5);
      typesGrid.setAutoFitData(Autofit.VERTICAL);
      typesGrid.setFields(extensionField, nameField);
      typesGrid.setEmptyMessage("No options to show");
      typesGrid.setWidth("300px");
      typesGrid.setHeight("200px");

      for(final FileType fileType : session.getFileTypes()) {
        addRecord(fileType);
      }

      final Button addButton = SmartUtils.getButton("New Type", Resources.ADD, new Command() {
        public void execute() {
          SC.askforValue("What extension would you like to use for this new file type?", new ValueCallback() {
            public void execute(final String value) {
              final FileType fileType = new FileType();
              fileType.setExtension(value);
              FileTypeService.Util.getInstance().createFileType(fileType, new AsyncCallbackImpl<FileType>() {
                @Override
                protected void handleSuccess() {
                  addRecord(getResult());
                }
              });
            }            
          });
        }        
      });

      typesGrid.addEditCompleteHandler(new EditCompleteHandler() {
        public void onEditComplete(final EditCompleteEvent event) {
          final int rowNum = event.getRowNum();
          final ListGridRecord record = typesGrid.getRecord(rowNum);
          final FileType fileType = (FileType) record.getAttributeAsObject("object");
          fileType.setShortName(StringUtils.toString(event.getNewValues().get("name")));
          FileTypeService.Util.getInstance().updateFileType(fileType, new AsyncCallbackImpl<Void>());
        }
      });
      layout.addMember(new CanvasWithOpsLayout<ListGrid>(typesGrid, addButton));
      super.setWidget(PopOutWindowBuilder.titled("Manage File Types").autoSized().withContents(layout).get());
    }
    
  }
}
