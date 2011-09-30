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
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectComponentImpl;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.ItemWrapper;

public class EditGalaxyXmlComponentImpl extends EditObjectComponentImpl<Canvas, String>  {
  private final TextAreaItem editorItem = new TextAreaItem("xml", "Xml");
  
  EditGalaxyXmlComponentImpl(final String inputXml) {
    editorItem.setValue(inputXml);
    editorItem.setShowTitle(false);
    editorItem.setWidth("*");
    editorItem.setHeight("*");
    final ItemWrapper wrapper = new ItemWrapper(editorItem);
    wrapper.setNumCols(1);
    wrapper.setWidth100();
    wrapper.setHeight100();
    setWidget(wrapper);
  }
  
  public String getObject() {
    return StringUtils.toString(editorItem.getValue());
  }

  public boolean isValid() {
    return StringUtils.hasText(editorItem.getValue());
  }

}
