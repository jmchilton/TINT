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
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.webgui.client.components.GridUserSelectionComponent;

public class GridUserSelectionComponentSupplierImpl implements Supplier<GridUserSelectionComponent> {

  static class GridUserSelectionComponentImpl extends SelectionComponentBaseImpl<GridUser, ListGrid> implements GridUserSelectionComponent {
    private final RestDataSource dataSource = new RestDataSource();
    
    GridUserSelectionComponentImpl() {
      final OperationBinding fetch = new OperationBinding();  
      fetch.setOperationType(DSOperationType.FETCH);  
      fetch.setDataProtocol(DSProtocol.POSTMESSAGE);
      dataSource.setOperationBindings(fetch);
      dataSource.setFetchDataURL("xml/data.xml?id=gridUser");  
      dataSource.setDataFormat(DSDataFormat.JSON);
      
      // Using short field names to reduce data transferred
      final DataSourceTextField firstNameField = new DataSourceTextField("f", "First Name");
      final DataSourceTextField lastNameField = new DataSourceTextField("l", "Last Name");
      final DataSourceTextField gridIdField = new DataSourceTextField("g", "Grid Identity");
      final DataSourceTextField instField = new DataSourceTextField("i", "Institution");
      gridIdField.setPrimaryKey(true);
      
      this.dataSource.setFields(lastNameField, firstNameField, instField, gridIdField);
      final ListGrid grid = new ListGrid();
      
      grid.setFilterOnKeypress(true);
      grid.setShowAllRecords(false);
      grid.setUseAllDataSourceFields(true);
      grid.setShowFilterEditor(true);
      grid.setAlternateRecordStyles(true);
      grid.setEmptyMessage("Loading user list...");
      grid.setShowRollOver(false);
      grid.setDrawAheadRatio(1.1f);
      grid.setAutoFetchData(true);
      grid.setDataSource(this.dataSource);
      setWidget(grid);
    }

    public GridUser getSelection() {
      final ListGridRecord record = this.get().getSelectedRecord();
      GridUser user = null;
      if(record != null) {
        user = new GridUser();
        user.setFirstName(record.getAttribute("f"));
        user.setLastName(record.getAttribute("l"));
        user.setInstitution(record.getAttribute("i"));
        user.setGridId(record.getAttribute("g"));
      }
      return user;
    }
  }

  public GridUserSelectionComponentImpl get() {
    final GridUserSelectionComponentImpl component = new GridUserSelectionComponentImpl();
    return component;
  }
}