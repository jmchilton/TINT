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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.LinkedHashMap;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Maps;
import edu.umn.msi.tropix.webgui.client.utils.Properties;
import edu.umn.msi.tropix.webgui.client.utils.Property;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

public class ScaffoldSampleTypeWizardPageImpl extends WizardPageImpl<Canvas> {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private Property<ScaffoldSampleType> scaffoldSampleTypeProperty =
      Properties.newProperty(ScaffoldSampleType.MANY_ANALYSIS);
  private boolean analyzeAsMudpit;
  final RadioGroupItem sampleRadioGroupItem = new RadioGroupItem("option");
  final CheckboxItem analyzeAsMudpitItem = new CheckboxItem("analyzeAsMudpit", "Analyze as mudpit");

  public enum ScaffoldSampleType {
    MANY_ANALYSIS("One scaffold analysis per identification analysis"),
    MANY_SAMPLE("Identification analyses as independent replicates"),
    ONE_SAMPLE("All identification analyses as one scaffold sample"),
    CUSTOM("Manually describe scaffold samples");

    private final String description;

    private ScaffoldSampleType(final String description) {
      this.description = description;
    }
  }

  ScaffoldSampleType getScaffoldType() {
    return scaffoldSampleTypeProperty.get();
  }

  boolean getAnalyzeAsMudpit() {
    return analyzeAsMudpit;
  }

  void registerScaffoldSampleTypeChangeListener(final Listener<ScaffoldSampleType> scaffoldSampleType) {
    scaffoldSampleTypeProperty.addListener(scaffoldSampleType);
  }

  ScaffoldSampleTypeWizardPageImpl(final boolean allowCustomScaffoldSamples) {
    setTitle(CONSTANTS.scaffoldWizardOptionsTitle());
    setDescription(CONSTANTS.scaffoldWizardOptionsDescription());
    setValid(true);

    final Layout layout = SmartUtils.getFullVLayout();

    analyzeAsMudpitItem.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        analyzeAsMudpit = (Boolean) analyzeAsMudpitItem.getValue();
      }
    });

    sampleRadioGroupItem.setColSpan(2);
    final LinkedHashMap<String, String> options = Maps.newLinkedHashMap();
    for(ScaffoldSampleType type : ScaffoldSampleType.values()) {
      if(!allowCustomScaffoldSamples && type == ScaffoldSampleType.CUSTOM) {
        continue;
      }
      options.put(type.toString(), type.description);
    }
    sampleRadioGroupItem.setShowTitle(false);
    sampleRadioGroupItem.setValueMap(options);
    sampleRadioGroupItem.setValue(scaffoldSampleTypeProperty.toString());
    sampleRadioGroupItem.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        checkValid();
      }

    });
    final Form form = new Form(sampleRadioGroupItem, analyzeAsMudpitItem);
    form.setNumCols(2);
    layout.addMember(form);
    this.setCanvas(layout);
  }

  private void checkValid() {
    final Object valueObject = sampleRadioGroupItem.getValue();
    final String value = (!StringUtils.hasText(valueObject)) ? null : valueObject.toString();
    if(value != null) {
      final ScaffoldSampleType scaffoldSampleType = ScaffoldSampleType.valueOf(value);
      scaffoldSampleTypeProperty.set(scaffoldSampleType);
      setValid(true);
      analyzeAsMudpitItem.setDisabled(scaffoldSampleType == ScaffoldSampleType.CUSTOM);
    } else {
      setValid(sampleRadioGroupItem.isDisabled());
    }
  }

  void setShowSampleType(final boolean show) {
    this.sampleRadioGroupItem.setDisabled(!show);
    this.checkValid();
  }
}
