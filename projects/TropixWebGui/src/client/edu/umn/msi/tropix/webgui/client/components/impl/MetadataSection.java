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

import java.util.Date;

import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.SelectionStyle;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.PropertyListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PageFrameSupplierImpl.Section;

public class MetadataSection extends Section {
  private final PropertyListGrid listGrid;

  public MetadataSection(final TropixObject tropixObject) {
    super();
    this.listGrid = new PropertyListGrid();
    this.listGrid.setMargin(10);
    this.listGrid.setSelectionType(SelectionStyle.NONE);
    this.listGrid.setMinHeight(10);
    this.listGrid.setHeight(10);
    this.listGrid.setAutoFitData(Autofit.VERTICAL);

    this.listGrid.set("Name", StringUtils.sanitize(tropixObject.getName()));
    this.listGrid.set("Description", StringUtils.sanitize(tropixObject.getDescription()));

    if(tropixObject.getCreationTime() != null) {
      final Date creationDate = new Date(Long.parseLong(tropixObject.getCreationTime()));
      this.listGrid.set("Creation Date", creationDate);
    }

    super.setTitle("Metadata");
    super.setResizeable(true);
    super.setItem(this.listGrid);
    super.setExpanded(true);
  }

  public void set(final String name, final String value) {
    this.listGrid.set(name, StringUtils.sanitize(value));
  }
}
