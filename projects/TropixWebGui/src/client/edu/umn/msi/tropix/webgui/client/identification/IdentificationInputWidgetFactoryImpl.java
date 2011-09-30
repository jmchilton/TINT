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

package edu.umn.msi.tropix.webgui.client.identification;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Function;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.umn.msi.tropix.webgui.client.forms.InputWidgetFactory;
import edu.umn.msi.tropix.webgui.client.forms.ValueHook;
import edu.umn.msi.tropix.webgui.client.forms.ValueHookRegistry;
import edu.umn.msi.tropix.webgui.client.utils.JArray;
import edu.umn.msi.tropix.webgui.client.utils.JObject;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

public class IdentificationInputWidgetFactoryImpl implements InputWidgetFactory {
  private final ParameterSetMap parametersSpecificationMap;
  private final ValidationProvider validator;
  private final Map<String, ParametersWidgetCreator> parametersWidgetFactoryMap;
  private final Function<String, String> defaultValueMapper;
  private boolean readOnly = false;

  public IdentificationInputWidgetFactoryImpl(final ParameterSetMap parametersSpecificationMap, final Function<String, String> defaultValueMapper, final ValidationProvider validator, final boolean readOnly) {
    this.parametersSpecificationMap = parametersSpecificationMap;
    this.validator = validator;
    this.defaultValueMapper = defaultValueMapper;
    this.readOnly = readOnly;
    this.parametersWidgetFactoryMap = new HashMap<String, ParametersWidgetCreator>();
    this.register(new SelectionParameterWidgetCreator());
    this.register(new TextBoxParameterWidgetCreator());
    this.register(new BooleanCheckBoxParameterWidgetCreator());
  }

  public Widget get(final JObject parameterDisplayWidget, final ValueHookRegistry tracker, final ValidationProvider provider) {
    final Object creatorObject = this.parametersWidgetFactoryMap.get(parameterDisplayWidget.getStringOrNull("widget"));
    if(creatorObject == null) {
      return null;
    } else {
      return ((ParametersWidgetCreator) creatorObject).create(parameterDisplayWidget, tracker, this.readOnly);
    }
  }

  public void register(final ParametersWidgetCreator creator) {
    this.parametersWidgetFactoryMap.put(creator.getWidgetName(), creator);
  }

  abstract class ParametersWidgetCreator {
    public abstract Widget create(JObject parameterDisplayWidget, ValueHookRegistry tracker, boolean readOnly);

    public abstract String getWidgetName();
  }

  private void register(final JObject inputObject, final ValueHookRegistry registery, final ValueHook valueHook) {
    final String parameterName = inputObject.getString("parameter");
    registery.registerHook(parameterName, valueHook);
    valueHook.set(this.defaultValueMapper.apply(parameterName));
  }

  class SelectionParameterWidgetCreator extends ParametersWidgetCreator {
    public String getWidgetName() {
      return "selection";
    }

    public Widget create(final JObject parameterDisplayWidget, final ValueHookRegistry tracker, final boolean readOnly) {
      final SelectionParameterWidget widget = new SelectionParameterWidget(parameterDisplayWidget, IdentificationInputWidgetFactoryImpl.this.parametersSpecificationMap, readOnly);
      IdentificationInputWidgetFactoryImpl.this.register(parameterDisplayWidget, tracker, widget);
      return widget;
    }
  }

  class TextBoxParameterWidgetCreator extends ParametersWidgetCreator {
    public String getWidgetName() {
      return "textbox";
    }

    public Widget create(final JObject parameterDisplayWidget, final ValueHookRegistry tracker, final boolean readOnly) {
      final TextBoxParameterWidget widget = new TextBoxParameterWidget(parameterDisplayWidget, IdentificationInputWidgetFactoryImpl.this.parametersSpecificationMap, IdentificationInputWidgetFactoryImpl.this.validator, readOnly);
      IdentificationInputWidgetFactoryImpl.this.register(parameterDisplayWidget, tracker, widget);
      return widget;
    }
  }

  class BooleanCheckBoxParameterWidgetCreator extends ParametersWidgetCreator {
    public String getWidgetName() {
      return "checkbox";
    }

    public Widget create(final JObject parameterDisplayWidget, final ValueHookRegistry tracker, final boolean readOnly) {
      final BooleanCheckBoxParameterWidget widget = new BooleanCheckBoxParameterWidget(parameterDisplayWidget, readOnly);
      IdentificationInputWidgetFactoryImpl.this.register(parameterDisplayWidget, tracker, widget);
      return widget;
    }
  }

}

class SelectionParameterWidget extends ListBox implements ValueHook {
  private static final int DEFAULT_VISIBLE_ITEM_COUNT = 7;
  private static final boolean DEFAULT_SORTED = false;
  private final boolean allowMultiple;
  private final String parameterName;
  private String lastValue = "";

  public String get() {
    if(this.allowMultiple) {
      final StringBuffer valueBuffer = new StringBuffer();
      boolean first = true;
      for(int i = 0; i < this.getItemCount(); i++) {
        if(this.isItemSelected(i)) {
          if(!first) {
            valueBuffer.append(", ");
          } else {
            first = false;
          }
          valueBuffer.append(this.getValue(i));
        }
      }
      return valueBuffer.toString();
    } else {
      return this.getValue(this.getSelectedIndex());
    }
  }

  private boolean allowMultipleSelections(final JObject cellObject) {
    final String allowMultiple = cellObject.getStringOrNull("allowMultiple");
    return "true".equals(allowMultiple);
  }

  private int getVisibleItemCount(final JObject cellObject) {
    if(cellObject.containsKey("visibleItems")) {
      return cellObject.getInt("visibleItems");
    } else {
      return SelectionParameterWidget.DEFAULT_VISIBLE_ITEM_COUNT;
    }
  }

  private boolean isSorted(final JObject cellObject) {
    if(cellObject.containsKey("sort")) {
      return "true".equals(cellObject.getStringOrNull("sort"));
    } else {
      return SelectionParameterWidget.DEFAULT_SORTED;
    }
  }

  private void reset() {
    this.set(this.lastValue);
  }

  @Override
  public void onBrowserEvent(final Event event) {
    if((event.getTypeInt() == Event.ONCHANGE)) {
      this.reset();
    }
    super.onBrowserEvent(event);
  }

  SelectionParameterWidget(final JObject cellObject, final ParameterSetMap parametersSpecificationMap, final boolean readOnly) {
    if(readOnly) {
      this.sinkEvents(Event.ONCHANGE);
    }
    this.allowMultiple = this.allowMultipleSelections(cellObject);
    this.setMultipleSelect(this.allowMultiple);
    if(this.allowMultiple) {
      this.setVisibleItemCount(this.getVisibleItemCount(cellObject));
    }
    this.parameterName = cellObject.getString("parameter");
    final JObject parameter = parametersSpecificationMap.getParameter(this.parameterName);

    final DomainElementIterator iterator = new DomainElementIterator(parameter);
    if(!this.isSorted(cellObject)) {
      while(iterator.hasNext()) {
        final DomainElement domainElement = iterator.next();
        this.addItem(domainElement.getDisplay(), domainElement.getValue());
      }
    } else {
      final TreeMap<String, String> treeMap = new TreeMap<String, String>();
      while(iterator.hasNext()) {
        final DomainElement domainElement = iterator.next();
        treeMap.put(domainElement.getDisplay(), domainElement.getValue());
      }
      for(final String display : treeMap.keySet()) {
        final String value = treeMap.get(display);
        this.addItem(display, value);
      }
    }
  }

  public void set(final String value) {
    this.lastValue = value;
    Collection<String> values = Arrays.asList(value);
    if(this.allowMultiple) {
      values = Arrays.asList(value.split(", "));
    }
    for(int i = 0; i < this.getItemCount(); i++) {
      this.setItemSelected(i, values.contains(this.getValue(i)));
    }
  }
}

class DomainElement {
  private final String value;
  private final String display;

  public DomainElement(final String value, final String display) {
    this.value = value;
    this.display = display;
  }

  public String getValue() {
    return this.value;
  }

  public String getDisplay() {
    return this.display;
  }
}

class DomainElementIterator implements Iterator<DomainElement> {
  private final Iterator<JObject> domainElementIterator;

  DomainElementIterator(final JObject parameterObject) throws JSONException {
    final JObject domainObject = parameterObject.getJObject("domain");
    final JArray domainElements = domainObject.getJArray("domainElement");
    this.domainElementIterator = domainElements.jObjectIterator();
  }

  public boolean hasNext() {
    return this.domainElementIterator.hasNext();
  }

  public DomainElement next() throws JSONException {
    final JObject domainElementJObject = this.domainElementIterator.next();
    final String value = domainElementJObject.getString("value");
    final String display = domainElementJObject.getStringOrNull("display");
    DomainElement domainElement;
    if(display == null) {
      domainElement = new DomainElement(value, value);
    } else {
      domainElement = new DomainElement(value, display);
    }
    return domainElement;
  }

  public void remove() {
    this.domainElementIterator.remove();
  }
}

class TextBoxParameterWidget extends TextBox implements FocusListener, ValueHook {
  private final String parameterName;
  private final ValidationProvider validator;

  public String getParameterName() {
    return this.parameterName;
  }

  public String get() {
    return this.getText();
  }

  public void onFocus(final Widget w) {
  }

  public void onLostFocus(final Widget w) {
    this.validate();
  }

  private void validate() {
    final String value = this.getText();
    boolean valid = true;
    if(this.validator != null) {
      valid = this.validator.validate(this.parameterName, value);
    }
    if(!valid) {
      this.addStyleDependentName("invalid");
    } else {
      this.removeStyleDependentName("invalid");
    }
  }

  TextBoxParameterWidget(final JObject cellObject, final ParameterSetMap parametersSpecificationMap, final ValidationProvider validator, final boolean readOnly) throws JSONException {
    this.setReadOnly(readOnly);
    this.parameterName = cellObject.getString("parameter");
    if(cellObject.containsKey("width")) {
      this.setWidth(cellObject.getString("width"));
    }
    this.setStylePrimaryName("tropixForms-TextBox");
    this.addKeyboardListener(new KeyboardListener() {
      public void onKeyDown(final Widget sender, final char keyCode, final int modifiers) {
      }

      public void onKeyPress(final Widget sender, final char keyCode, final int modifiers) {
      }

      public void onKeyUp(final Widget sender, final char keyCode, final int modifiers) {
        TextBoxParameterWidget.this.validate();
      }
    });
    this.addChangeListener(new ChangeListener() {
      public void onChange(final Widget sender) {
        TextBoxParameterWidget.this.validate();
      }
    });
    this.validator = validator;
  }

  public void set(final String value) {
    this.setText(value);
    this.validate();
  }

  public void onChange() {
    this.validate();
  }
}

class BooleanCheckBoxParameterWidget extends CheckBox implements ValueHook {
  private final String parameterName;

  public String getParameterName() {
    return this.parameterName;
  }

  public String get() {
    if(this.isChecked()) {
      return "true";
    } else {
      return "false";
    }
  }

  BooleanCheckBoxParameterWidget(final JObject cellObject, final boolean readOnly) throws JSONException {
    this.parameterName = cellObject.getString("parameter");
    this.setEnabled(!readOnly);
  }

  public void set(final String value) {
    this.setChecked("true".equals(value));
  }
}
