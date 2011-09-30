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

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGrid;

/**
 * This is an extension of the SmartGWT that is automatically configured with an client only data source at construction time.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
public class ClientListGrid extends ListGrid {

  public static DataSourceTextField getIdField() {
    return getIdField("id");
  }

  public static DataSourceTextField getIdField(final String primaryKey) {
    final DataSourceTextField idField = new DataSourceTextField(primaryKey);
    idField.setPrimaryKey(true);
    return idField;
  }

  private DataSource clientOnlyDataSource;

  public ClientListGrid() {
    init(getIdField());
  }

  public ClientListGrid(final DataSourceField... fields) {
    init(fields);
  }

  public ClientListGrid(final String primaryKey) {
    init(getIdField(primaryKey));
  }

  public ClientListGrid(final DataSource dataSource) {
    init(dataSource);
  }

  private void init(final DataSourceField... fields) {
    final DataSource dataSource = new DataSource();
    dataSource.setFields(fields);

    init(dataSource);
  }

  private void init(final DataSource dataSource) {
    clientOnlyDataSource = dataSource;
    clientOnlyDataSource.setCacheAllData(false);
    setAlternateRecordStyles(true);
    clientOnlyDataSource.setClientOnly(true);
    setDataSource(clientOnlyDataSource);
    setAutoFetchData(true);
  }

  public DataSource getClientDataSource() {
    return this.clientOnlyDataSource;
  }
}
