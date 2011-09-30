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

package edu.umn.msi.tropix.webgui.client.forms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import edu.umn.msi.tropix.webgui.client.utils.JArray;
import edu.umn.msi.tropix.webgui.client.utils.JObject;

public class FormWidgetFactoryImpl implements FormWidgetFactory {
  private Map<String, Function<JObject, Widget>> cellWidgetFunctions = new HashMap<String, Function<JObject, Widget>>();
  private InputWidgetFactory inputWidgetFactory;
  private ValueHookRegistry valueHookRegistry;
  private ValidationTracker validationTracker;
  {
    this.addCellWidgetFunction("window", new WindowCellWidgetProvider());
    this.addCellWidgetFunction("input", new InputCellWidgetProvider());
    this.addCellWidgetFunction("label", new LabelCellWidgetProvider());
    this.addCellWidgetFunction("table", new GridWidgetProvider());
    this.addCellWidgetFunction("tabs", new TabWidgetProvider());
    this.addCellWidgetFunction("flowPanel", new FlowPanelWidgetProvider());
    this.addCellWidgetFunction("horizontalPanel", new CellPanelWidgetProvider("horizontal"));
    this.addCellWidgetFunction("verticalPanel", new CellPanelWidgetProvider("vertical"));
  }

  protected Widget getChildWidget(final JObject parentObject) throws JSONException {
    final Iterator<String> keyIterator = parentObject.keySet().iterator();
    while(keyIterator.hasNext()) {
      final String key = keyIterator.next();
      if(this.cellWidgetFunctions.containsKey(key)) {
        return this.cellWidgetFunctions.get(key).apply(parentObject.getJObject(key));
      }
    }
    throw new IllegalArgumentException("Found no valid child element in parent object" + parentObject.toString());
  }

  // TODO: Replace
  public Widget apply(final JObject configurationObject) {
    final Widget widget = getChildWidget(configurationObject);
    widget.setStyleName("tropixForms");
    return widget;
  }

  protected class WindowCellWidgetProvider implements Function<JObject, Widget> {
    public Widget apply(final JObject windowConfigurationObject) {
      final Window window = new Window();
      if(windowConfigurationObject.containsKey("modal")) {
        window.setIsModal(windowConfigurationObject.getBoolean("modal"));
      }
      final JArray entriesArray = windowConfigurationObject.getJArray("entry");
      for(int i = 0; i < entriesArray.size(); i++) {
        final JObject entryObject = entriesArray.getJObject(i);
        final Widget childWidget = FormWidgetFactoryImpl.this.getChildWidget(entryObject);
        window.addChild(childWidget);
      }
      return window;
    }
  }

  /*
   * private static void setCanvasProperties(final JObject config, final Canvas canvas) {
   * FormWidgetFactoryImpl.setBaseWidgetProperties(config, canvas);
   * if(config.containsKey("accessKey")) {
   * canvas.setAccessKey(config.getString("accessKey"));
   * }
   * if(config.containsKey("alignment")) {
   * canvas.setAlign(Alignment.valueOf(config.getString("alignment")));
   * }
   * if(config.containsKey("border")) {
   * canvas.setBorder(config.getString("border"));
   * }
   * if(config.containsKey("minHeight")) {
   * canvas.setMinHeight(config.getInt("minHeight"));
   * }
   * if(config.containsKey("minWidth")) {
   * canvas.setMinWidth(config.getInt("minWidth"));
   * }
   * if(config.containsKey("maxHeight")) {
   * canvas.setMaxHeight(config.getInt("maxHeight"));
   * }
   * if(config.containsKey("maxWidth")) {
   * canvas.setMaxWidth(config.getInt("maxWidth"));
   * }
   * }
   * 
   * private static void setLayoutProperties(final JObject config, final Layout layout) {
   * FormWidgetFactoryImpl.setCanvasProperties(config, layout);
   * if(config.containsKey("resizable")) {
   * layout.setCanDragResize(Boolean.parseBoolean(config.getString("resizable")));
   * }
   * }
   * 
   * private static void setBaseWidgetProperties(final JObject config, final BaseWidget baseWidget) {
   * if(config.containsKey("width")) {
   * baseWidget.setWidth(config.getString("width"));
   * }
   * if(config.containsKey("height")) {
   * baseWidget.setHeight(config.getString("height"));
   * }
   * if(config.containsKey("title")) {
   * baseWidget.setTitle(config.getString("title"));
   * }
   * if(config.containsKey("visible")) {
   * baseWidget.setVisible(config.getBoolean("visible"));
   * }
   * }
   */

  /**
   * Widget provider for input cells, the most interesting ones.
   */
  protected class InputCellWidgetProvider implements Function<JObject, Widget> {
    public Widget apply(final JObject cellObject) {
      return inputWidgetFactory.get(cellObject, valueHookRegistry, validationTracker);
    }
  }

  /**
   * Widget provider for plain text cells.
   */
  protected static class LabelCellWidgetProvider implements Function<JObject, Widget> {
    public Widget apply(final JObject cellObject) {
      final Label label = new Label();
      label.setText(cellObject.getString("text"));
      return label;
    }
  }

  protected class GridWidgetProvider implements Function<JObject, Widget> {

    public Widget apply(final JObject gridConfiguration) {
      final JArray rows = gridConfiguration.getJArray("row");
      final int numRows = rows.size();
      final FlexTable grid = new FlexTable();
      grid.setCellPadding(5);
      grid.setCellSpacing(5);
      for(int i = 0; i < numRows; i++) {
        final JObject row = rows.getJObject(i);
        final Widget[] rowWidgets = this.getRowWidgets(row);
        for(int j = 0; j < rowWidgets.length; j++) {
          grid.setWidget(i, j, rowWidgets[j]);
        }
      }
      VerticalPanel panel = new VerticalPanel();
      panel.setWidth("100%");
      panel.add(grid);
      VerticalPanel panel2 = new VerticalPanel();

      panel2.setHeight("100%");
      panel.add(panel2);
      return panel;
    }

    /**
     * Builds one row of a grid from the supplied row configuration object.
     * 
     * @param row
     *          JSON configuration for a row
     * @return
     * @throws JSONException
     */
    protected Widget[] getRowWidgets(final JObject rowConfiguration) throws JSONException {
      final JArray parameterDisplay = rowConfiguration.getJArray("cell");
      final int numCols = parameterDisplay.size();
      final Widget[] widgets = new Widget[numCols];
      for(int i = 0; i < numCols; i++) {
        final JObject cellObject = parameterDisplay.getJObject(i);
        final Widget widget = FormWidgetFactoryImpl.this.getChildWidget(cellObject);
        widgets[i] = widget;
      }
      return widgets;
    }

  }

  protected class TabWidgetProvider implements Function<JObject, Widget> {
    public Widget apply(final JObject tabConfigurationObject) {
      final TabSet tabSet = new TabSet();
      final JArray tabsArray = tabConfigurationObject.getJArray("tab");
      for(int i = 0; i < tabsArray.size(); i++) {
        final JObject tabObject = tabsArray.getJObject(i);
        final Tab item = new Tab();
        item.setTitle(tabObject.getString("name"));
        final Widget childWidget = FormWidgetFactoryImpl.this.getChildWidget(tabObject);
        final VLayout layout = new VLayout();
        layout.setHeight100();
        layout.setWidth100();
        layout.addMember(childWidget);
        item.setPane(layout);
        // TODO: Implement tooltip again...
        // if(tabObject.containsKey("tooltip")) {
        // }
        tabSet.addTab(item);
        tabSet.setWidth100();
        tabSet.setHeight100();
      }
      // return tabFolder;
      return tabSet;
    }
  }

  protected abstract class PanelWidgetProvider implements Function<JObject, Widget> {

    abstract Panel getNewPanel(JObject panelConfigurationObject);

    public Widget apply(final JObject panelConfigurationObject) {
      final Panel panel = this.getNewPanel(panelConfigurationObject);
      final JArray entriesArray = panelConfigurationObject.getJArray("entry");
      for(int i = 0; i < entriesArray.size(); i++) {
        final JObject entryObject = entriesArray.getJObject(i);
        final Widget childWidget = FormWidgetFactoryImpl.this.getChildWidget(entryObject);
        panel.add(childWidget);
      }
      return panel;
    }

  }

  protected class FlowPanelWidgetProvider extends PanelWidgetProvider {
    Panel getNewPanel(final JObject panelConfigurationObject) {
      return new FlowPanel();
    }
  }

  protected class CellPanelWidgetProvider extends PanelWidgetProvider {
    private String type;

    public CellPanelWidgetProvider(final String type) {
      this.type = type;
    }

    protected Panel getNewPanel(final JObject panelConfigurationObject) {
      CellPanel panel;
      if(this.type.equals("horizontal")) {
        panel = new HorizontalPanel();
      } else if(this.type.equals("vertical")) {
        panel = new VerticalPanel();
      } else {
        throw new IllegalStateException("Panel type " + this.type + " is unknown.");
      }
      if(panelConfigurationObject.containsKey("spacing")) {
        panel.setSpacing(panelConfigurationObject.getInt("spacing"));
      }
      return panel;
    }

  }

  public void addCellWidgetFunction(final String cellType, final Function<JObject, Widget> provider) {
    this.cellWidgetFunctions.put(cellType, provider);
  }

  public void setValidationTracker(final ValidationTracker tracker) {
    this.validationTracker = tracker;
  }

  public void setValueHookRegistry(final ValueHookRegistry valueHookRegistry) {
    this.valueHookRegistry = valueHookRegistry;
  }

  public void setInputWidgetFactory(final InputWidgetFactory inputWidgetFactory) {
    this.inputWidgetFactory = inputWidgetFactory;
  }

}
