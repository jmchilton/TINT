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

package edu.umn.msi.tropix.webgui.client.components.galaxy;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent;
import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectComponentImpl;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Form;

public class EditGalaxyOutputComponentImpl extends EditObjectComponentImpl<Canvas, Data> {
  private final TextItem nameItem = new TextItem("name", "Name");
  private final TextItem labelItem = new TextItem("label", "Label");
  private final FormItem formatItem;
  private final FileTypeFormItemComponent fileTypeFormItemComponent;

  public EditGalaxyOutputComponentImpl(final Data data, final FileTypeFormItemComponent fileTypeFormItemComponent) {
    nameItem.setValue(data.getName());
    labelItem.setValue(data.getLabel());
    formatItem = fileTypeFormItemComponent.get();
    this.fileTypeFormItemComponent = fileTypeFormItemComponent;
    if(data.getFormat() != null) {
      fileTypeFormItemComponent.setSelection(GalaxyFormatUtils.formatToExtension(data.getFormat()));
    }
    final Form form = new Form(nameItem, labelItem, formatItem);
    form.setWrapItemTitles(false);
    form.addItemChangedHandler(getItemChangedHandler());
    final VLayout layout = new VLayout();
    layout.addMember(form);
    setWidget(layout);
  }
    
  public Data getObject() {
    final Data data = new Data();
    data.setFormat(GalaxyFormatUtils.extensionToFormat(StringUtils.toString(fileTypeFormItemComponent.getSelection().getExtension())));
    data.setLabel(StringUtils.toString(labelItem.getValue()));
    data.setName(StringUtils.toString(nameItem.getValue()));
    return data;
  }

  public boolean isValid() {
    return StringUtils.hasText(nameItem.getValue()) 
           && StringUtils.hasText(labelItem.getValue())
           && StringUtils.hasText(formatItem.getValue());
  }
  
}
