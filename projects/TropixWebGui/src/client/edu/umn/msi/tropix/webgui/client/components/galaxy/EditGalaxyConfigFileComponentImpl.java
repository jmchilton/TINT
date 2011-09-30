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

import java.util.LinkedHashMap;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFileType;
import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectComponentImpl;
import edu.umn.msi.tropix.webgui.client.utils.Maps;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Form;

public class EditGalaxyConfigFileComponentImpl extends EditObjectComponentImpl<Canvas, ConfigFile> {
    private static final LinkedHashMap<String, String> TYPE_MAP = Maps.newLinkedHashMap();
    static {
      TYPE_MAP.put(ConfigFileType.LITERAL.name(), "Literal");
      TYPE_MAP.put(ConfigFileType.TEMPLATE.name(), "Template");
    }
    private final TextItem nameItem = new TextItem("name", "Name");
    private final SelectItem selectItem = new SelectItem("type", "Type");
    private final TextAreaItem contentsItem = new TextAreaItem("contents", "Contents");

    public EditGalaxyConfigFileComponentImpl(final ConfigFile configFile) {
      nameItem.setValue(configFile.getName());
      selectItem.setValueMap(TYPE_MAP);
      selectItem.setValue(configFile.getType().name());
      contentsItem.setValue(configFile.getValue());
      contentsItem.setWidth("*");
      contentsItem.setHeight("*");
      final Form form = new Form(nameItem, selectItem, contentsItem);
      form.setWidth100();
      form.setHeight("*");
      form.addItemChangedHandler(getItemChangedHandler());
      final VLayout layout = new VLayout();
      layout.addMember(form);
      setWidget(layout);      
    }

    public ConfigFile getObject() {
      final ConfigFile configFile = new ConfigFile();
      final String typeStr = StringUtils.toString(selectItem.getValue());
      System.out.println("Type is " + typeStr);
      configFile.setType(ConfigFileType.valueOf(typeStr));
      System.out.println("Type is " + typeStr);
      configFile.setName(StringUtils.toString(nameItem.getValue()));
      configFile.setValue(StringUtils.toString(contentsItem.getValue()));
      return configFile;
    }

    public boolean isValid() {
      return StringUtils.hasText(nameItem.getValue());
    }
 
}
