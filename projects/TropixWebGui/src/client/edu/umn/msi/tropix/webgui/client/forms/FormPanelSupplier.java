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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.user.client.ui.Widget;

import edu.umn.msi.tropix.webgui.client.utils.JObject;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

class ParameterTracker implements ValueHookRegistry, Function<String, String> {
  private final Map<String, ValueHook> valueHookMap = new HashMap<String, ValueHook>();

  public void registerHook(final String name, final ValueHook hook) {
    this.valueHookMap.put(name, hook);
  }

  public String apply(final String name) {
    final ValueHook hook = this.valueHookMap.get(name);
    return (hook == null) ? null : hook.get();
  }

  public void setValue(final String name, final String value) {
    final ValueHook hook = this.valueHookMap.get(name);
    if(hook != null) {
      hook.set(value);
    }
  }

  public void setValues(final Function<String, String> valueFunction) {
    for(final String name : this.valueHookMap.keySet()) {
      this.setValue(name, valueFunction.apply(name));
    }
  }

  public Set<String> keySet() {
    return this.valueHookMap.keySet();
  }

}

public class FormPanelSupplier { // implements Supplier<Widget>
  private ParameterTracker tracker = new ParameterTracker();
  private ValidationMediator mediator = new ValidationMediator();
  private FormWidgetFactory widgetFactory;
  private Function<String, String> defaultValueMapper = null;
  private JObject formConfiguration;
  private ValidationTracker validationTracker;

  public void addValidationListener(final ValidationListener listener) {
    this.mediator.addValidationListener(listener);
    if(this.validationTracker != null) {
      this.validationTracker.fireValidationEvent();
    }
  }

  public Map<String, String> getParametersMap(final Collection<String> parameterNames) {
    final Map<String, String> parameterValues = new HashMap<String, String>();
    final Iterator<String> namesIterator = parameterNames.iterator();

    // Iterate though names, if widget exists get its value
    // otherwise use the default value
    while(namesIterator.hasNext()) {
      final String name = namesIterator.next();
      String value = this.tracker.apply(name);
      if(value == null && this.defaultValueMapper != null) {
        value = this.defaultValueMapper.apply(name);
      }
      parameterValues.put(name, value);
    }
    return parameterValues;
  }

  public Map<String, String> getParametersMap() {
    return this.getParametersMap(this.tracker.keySet());
  }

  /**
   * Builds a widget containing the specified form. Be sure setFormConfiguration is called before calling this method. Also if you wish to supply a validation provider be sure that is set prior to calling this method as well.
   * 
   * @return
   * @throws JSONException
   */
  public Widget get() throws JSONException {
    final JObject formConfigurationObject = this.formConfiguration.getJObject("formConfiguration");
    this.widgetFactory.setValidationTracker(this.validationTracker);
    this.widgetFactory.setValueHookRegistry(this.tracker);
    return this.widgetFactory.apply(formConfigurationObject);
  }

  public void setDefaultValueMapper(final Function<String, String> defaultValueMapper) {
    this.defaultValueMapper = defaultValueMapper;
    this.tracker.setValues(defaultValueMapper);
  }

  public void setValidationProvider(final ValidationProvider validationProvider) {
    this.validationTracker = new ValidationTracker(validationProvider, this.mediator);
  }

  public void setFormConfiguration(final String formConfigurationString) {
    this.setFormConfiguration(JObject.Factory.create(formConfigurationString));
  }

  public void setFormConfiguration(final JObject formConfiguration) {
    this.formConfiguration = formConfiguration;
  }

  public void setFormWidgetFactory(final FormWidgetFactoryImpl formWidgetFactory) {
    this.widgetFactory = formWidgetFactory;
  }
}
