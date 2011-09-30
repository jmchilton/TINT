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

import java.util.Iterator;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.CustomValidator;

import edu.umn.msi.tropix.webgui.client.utils.JObject;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

public class DefaultInputWidgetFactoryImpl implements InputWidgetFactory {

  public Widget get(final JObject cellObject, final ValueHookRegistry registry, final ValidationProvider validationProvider) {
    final String widgetType = cellObject.getString("widget");
    final String parameterName = cellObject.getString("parameter");
    Widget widget;
    if(widgetType.equals("textbox")) {
      widget = new TextBoxParameterWidget(cellObject, validationProvider);
    } else if(widgetType.equals("password")) {
      widget = new PasswordTextBoxParameterWidget(cellObject);
    } else if(widgetType.equals("checkbox")) {
      widget = new CheckBoxParameterWidget();
    } else if(widgetType.equals("selection")) {
      widget = new SelectionParameterWidget(cellObject);
    } else {
      throw new IllegalStateException("Unknown widget type encountered " + widgetType);
    }
    final ValueHook hook = (ValueHook) widget;
    registry.registerHook(parameterName, (ValueHook) widget);

    // Attempt to set the default value...
    final String defaultValue = cellObject.getStringOrNull("default");
    if(defaultValue != null) {
      hook.set(defaultValue);
    }
    return widget;

  }

  static class SelectionParameterWidget extends ListBox implements ValueHook {
    private static final int DEFAULT_VISIBLE_ITEM_COUNT = 1;
    private final boolean allowMultiple;

    public SelectionParameterWidget(final JObject cellObject) {
      this.allowMultiple = this.allowMultipleSelections(cellObject);
      this.setVisibleItemCount(this.getVisibleItemCount(cellObject));

      final Iterator<JObject> entries = cellObject.getJObject("entries").getJArray("entry").jObjectIterator();
      while(entries.hasNext()) {
        final JObject entry = entries.next();
        final String value = entry.getString("value");
        final String text = entry.containsKey("text") ? entry.getString("text") : value;
        this.addItem(text, value);
      }
    }

    private int getVisibleItemCount(final JObject cellObject) {
      if(cellObject.containsKey("visibleItems")) {
        return cellObject.getInt("visibleItems");
      } else {
        return SelectionParameterWidget.DEFAULT_VISIBLE_ITEM_COUNT;
      }
    }

    private void setSelectedByValue(final String value) {
      for(int i = 0; i < this.getItemCount(); i++) {
        if(this.getValue(i).equals(value)) {
          this.setItemSelected(i, true);
          return;
        }
      }
    }

    public void set(final String value) {
      if(this.allowMultiple) {
        final String[] values = value.split(", ");
        for(final String value2 : values) {
          this.setSelectedByValue(value2);
        }
      } else {
        this.setSelectedByValue(value);
      }
    }

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
      return allowMultiple != null && "true".equals(allowMultiple);
    }

  }

  static class CheckBoxParameterWidget extends CheckBox implements ValueHook {

    public void set(final String value) {
      // this.setChecked(value.equals("true") || value.equals("1"));
      setValue(value.equals("true") || value.equals("1"));
    }

    public String get() {
      return this.getValue() ? "true" : "false";
    }

  }

  /*
   * static class CheckBoxParameterWidget extends EasyFormItemWidget implements ValueHook { private String parameterName; private CheckboxItem
   * checkboxItem;
   * 
   * public String getParameterName() { return parameterName; } public void set(String value) { checkboxItem.setValue(value.equals("true") ||
   * value.equals("1")); }
   * 
   * public String get() { return checkboxItem.getValue().toString(); } CheckBoxParameterWidget(JObject cellObject, ValidationProvider validator)
   * throws JSONException { this.parameterName = cellObject.getString("parameter"); this.checkboxItem = new CheckboxItem();
   * checkboxItem.setShowLabel(false); init(checkboxItem); } }
   */

  static class PasswordTextBoxParameterWidget extends PasswordTextBox implements ValueHook {

    PasswordTextBoxParameterWidget(final JObject cellObject) throws JSONException {
      if(cellObject.containsKey("width")) {
        this.setWidth(cellObject.getString("width"));
      }
      this.setStylePrimaryName("tropixForms-TextBox");
    }

    public void set(final String value) {
      this.setText(value);
    }

    public String get() {
      return this.getText();
    }
  }

  abstract static class EasyFormItemWidget extends DynamicForm implements ValueHook {
    protected static final String FIELD_NAME = "theField";

    void setValidator(final FormItem item, final String key, final ValidationProvider validationProvider) {
      item.setValidateOnChange(false);
      item.setShowErrorStyle(true);
      item.setShowErrorIcon(true);
      item.setShowErrorText(false);
      item.setValidators(new CustomValidator() {
        protected boolean condition(final Object value) {
          return validationProvider.validate(key, value == null ? "" : value.toString());
        }
      });
      item.setRedrawOnChange(true);
    }

    protected void init(final FormItem item) {
      item.setName(EasyFormItemWidget.FIELD_NAME);
      item.setShowTitle(false);
      this.setFields(item);
      this.setNumCols(1);
    }

    public void set(final String value) {
      this.setValue(EasyFormItemWidget.FIELD_NAME, value);
    }

    public String get() {
      final String value = this.getValueAsString(EasyFormItemWidget.FIELD_NAME);
      return value == null ? "" : value;
    }

  }

  static class TextBoxParameterWidget extends EasyFormItemWidget {
    private final String parameterName;
    private final TextItem textItem;

    public String getParameterName() {
      return this.parameterName;
    }

    TextBoxParameterWidget(final JObject cellObject, final ValidationProvider validator) throws JSONException {
      this.parameterName = cellObject.getString("parameter");
      this.textItem = new TextItem();

      if(cellObject.containsKey("width")) {
        this.setWidth(cellObject.getString("width"));
        this.textItem.setWidth(cellObject.getString("width"));
      }
      this.textItem.addChangedHandler(new ChangedHandler() {
        public void onChanged(final ChangedEvent event) {
          event.getForm().validate();
        }
      });
      this.init(this.textItem);
      if(validator != null) {
        this.setValidator(this.textItem, this.parameterName, validator);
      }
    }
  }

}
