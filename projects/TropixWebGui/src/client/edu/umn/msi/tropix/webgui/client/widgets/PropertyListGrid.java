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

package edu.umn.msi.tropix.webgui.client.widgets;

import java.util.Date;
import java.util.HashMap;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.webgui.client.utils.StringUtils;

public class PropertyListGrid extends ListGrid {
  private final HashMap<String, ListGridRecord> recordMap = new HashMap<String, ListGridRecord>();
  private final DataSource dataSource;

  public PropertyListGrid() {
    super();
    this.dataSource = new DataSource();
    final DataSourceField dNameItem = new DataSourceTextField("name", "Property");
    dNameItem.setPrimaryKey(true);
    final DataSourceField dValueItem = new DataSourceTextField("value", "Value");
    this.dataSource.setFields(dNameItem, dValueItem);
    this.dataSource.setClientOnly(true);
    this.setAutoFetchData(true);
    this.setDataSource(this.dataSource);
    this.setAlternateRecordStyles(false);
    final ListGridField nameField = new ListGridField("name", "Property");
    nameField.setType(ListGridFieldType.TEXT);
    nameField.setWidth("25%");
    nameField.setHoverCustomizer(new HoverCustomizer() {
      public String hoverHTML(final Object value, final ListGridRecord record, final int rowNum, final int colNum) {
        String hover = record.getAttribute("nameHover");
        if(hover == null) {
          hover = record.getAttribute("name");
        }
        return hover;
      }
    });
    final ListGridField valueField = new ListGridField("value", "Value");
    valueField.setType(ListGridFieldType.TEXT);
    valueField.setWidth("*");
    valueField.setShowHover(true);
    this.setFields(nameField, valueField);
  }

  public void set(final String name, final String nameHover, final Object value) {
    ListGridRecord record;
    if(this.recordMap.containsKey(name)) {
      record = this.recordMap.get(name);
      if(value instanceof Date) {
        record.setAttribute("value", (Date) value);
      } else {
        record.setAttribute("value", StringUtils.toString(value));
      }
      this.dataSource.updateData(record);
    } else {
      record = new ListGridRecord();
      record.setAttribute("name", name);
      if(nameHover != null) {
        record.setAttribute("nameHover", nameHover);
      }
      if(value instanceof Date) {
        record.setAttribute("value", (Date) value);
      } else {
        record.setAttribute("value", StringUtils.toString(value));
      }
      this.recordMap.put(name, record);
      this.addData(record);
    }
  }

  public void set(final String name, final Object value) {
    this.set(name, null, value);
  }

  public void clearProperties() {
    for(final ListGridRecord record : this.recordMap.values()) {
      this.removeData(record);
    }
    this.recordMap.clear();
    this.redraw();
  }
}
