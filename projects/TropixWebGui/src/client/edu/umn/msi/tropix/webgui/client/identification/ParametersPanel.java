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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelSupplier;
import edu.umn.msi.tropix.webgui.client.forms.FormWidgetFactoryImpl;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.forms.ValidationMediator;
import edu.umn.msi.tropix.webgui.client.forms.ValidationTracker;
import edu.umn.msi.tropix.webgui.client.forms.ValueHook;
import edu.umn.msi.tropix.webgui.client.forms.ValueHookRegistry;
import edu.umn.msi.tropix.webgui.client.utils.JObject;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

class DefaultValueMapperImpl implements Function<String, String> {
  private final ParameterSetMap setMap;
  private final Map<String, String> templateMap;

  DefaultValueMapperImpl(final ParameterSetMap setMap, final Map<String, String> templateMap) {
    this.setMap = setMap;
    this.templateMap = templateMap;
  }

  public String apply(final String parameterName) {
    String defaultValue;
    if(templateMap == null || !templateMap.containsKey(parameterName)) {
      final JObject parameterObject = setMap.getParameter(parameterName);
      Preconditions.checkState(parameterObject != null, "No parameter object found corresponding to name " + parameterName);
      defaultValue = parameterObject.getStringOrNull("default");
      Preconditions.checkNotNull(defaultValue, "No default found for parameter with name " + parameterName);
    } else {
      defaultValue = templateMap.get(parameterName);
    }
    return defaultValue;
  }

}

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

  public void setValues(final Function<String, String> valueMapper) {
    final Iterator<String> iterator = this.valueHookMap.keySet().iterator();
    while(iterator.hasNext()) {
      final String name = iterator.next();
      this.setValue(name, valueMapper.apply(name));
    }
  }
}

class ParametersValidationProvider extends ParametersValidator implements ValidationProvider {
  public ParametersValidationProvider(final ParameterSetMap parametersSpecificationMap) {
    super(parametersSpecificationMap);
  }
}

public class ParametersPanel extends VLayout {
  private TreeComponentFactory treeComponentFactory;
  private ValidationMediator mediator = new ValidationMediator();
  private JObject parametersSpecification;
  private JObject parametersDisplay;
  private ParameterSetMap parametersSetMap;
  private Function<String, String> defaultValueMapper;
  private ValidationTracker validationTracker;
  private String parametersType;
  private FormPanelSupplier formPanelSupplier;
  private LocationFactory locationFactory;
  private MetadataInputComponentFactory metadataInputComponentFactory;

  public ParametersPanel(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory,
      final MetadataInputComponentFactory metadataInputComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
    this.metadataInputComponentFactory = metadataInputComponentFactory;
  }

  public void addValidationListener(final ValidationListener listener) {
    this.mediator.addValidationListener(listener);
    if(this.validationTracker != null) {
      this.validationTracker.fireValidationEvent();
    }
  }

  public void setTemplateMap(final Map<String, String> templateMap) {
    this.defaultValueMapper = new DefaultValueMapperImpl(this.parametersSetMap, templateMap);
    this.formPanelSupplier.setDefaultValueMapper(this.defaultValueMapper);
  }

  public Map<String, String> getParametersMap() {
    final Map<String, String> parameterValues = new HashMap<String, String>();
    final Map<String, String> inputValues = this.formPanelSupplier.getParametersMap();
    final Iterator<String> namesIterator = this.parametersSetMap.getParameterNames();
    while(namesIterator.hasNext()) {
      final String name = namesIterator.next();
      String value = inputValues.get(name);
      if(value == null) {
        value = this.defaultValueMapper.apply(name);
      }
      parameterValues.put(name, value);
    }
    return parameterValues;
  }

  public void init(final String parametersType, final String parametersSpecification, final String parametersDisplay,
      final Map<String, String> parameterTemplateMap, final boolean readOnly) {
    this.parametersType = parametersType;
    this.parametersSpecification = JObject.Factory.create(parametersSpecification);
    this.parametersDisplay = JObject.Factory.create(parametersDisplay);
    this.parametersSetMap = new ParameterSetMap(parametersSpecification);
    this.defaultValueMapper = new DefaultValueMapperImpl(this.parametersSetMap, null);
    final ValidationProvider baseValidationProvider = new ParametersValidationProvider(this.parametersSetMap);
    this.validationTracker = new ValidationTracker(baseValidationProvider, this.mediator);

    this.formPanelSupplier = new FormPanelSupplier();
    this.formPanelSupplier.setFormConfiguration(parametersDisplay);
    this.formPanelSupplier.setValidationProvider(this.validationTracker);
    final FormWidgetFactoryImpl factory = new FormWidgetFactoryImpl();
    factory.addCellWidgetFunction("info", new Function<JObject, Widget>() {
      public Widget apply(final JObject config) {
        final Image image = new Image(Resources.HELP_ICON);
        image.setSize("16px", "16px");
        final String parameterName = config.getString("parameter");
        final JObject jObject = ParametersPanel.this.parametersSetMap.getParameter(parameterName);
        final String shortDescription = jObject.getString("shortDescription");
        image.setTitle(shortDescription);
        return image;
      }
    });
    factory.setInputWidgetFactory(new IdentificationInputWidgetFactoryImpl(this.parametersSetMap, this.defaultValueMapper, this.validationTracker,
        readOnly));
    this.formPanelSupplier.setFormWidgetFactory(factory);
    this.formPanelSupplier.setDefaultValueMapper(this.defaultValueMapper);
    this.setTemplateMap(parameterTemplateMap);
    this.validationTracker.fireValidationEvent();
    if(!readOnly) {
      final ToolStrip toolStrip = new ToolStrip();
      final Button saveButton = SmartUtils.getButton("Save...", Resources.SAVE, new SaveCommand(metadataInputComponentFactory, ParametersPanel.this,
          parametersType));
      final Button loadButton = SmartUtils.getButton("Load...", Resources.DOWNLOAD, new LoadCommand(this.treeComponentFactory, locationFactory,
          ParametersPanel.this, parametersType));
      toolStrip.setMembers(loadButton, saveButton);
      toolStrip.setMargin(3);
      toolStrip.setWidth100();
      this.addMember(toolStrip);
    }
    this.addMember((Canvas) this.formPanelSupplier.get());
  }

  public void init(final String parametersType, final String parametersSpecification, final String parametersDisplay, final boolean readOnly) {
    this.init(parametersType, parametersSpecification, parametersDisplay, null, readOnly);
  }

}