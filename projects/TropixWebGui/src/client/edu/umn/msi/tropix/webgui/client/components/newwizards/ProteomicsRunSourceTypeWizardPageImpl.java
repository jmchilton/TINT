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

class ProteomicsRunSourceTypeWizardPageImpl extends WizardPageImpl<Canvas> {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private Property<ProteomicsRunSource> proteomicsRunSourceProperty = Properties.newProperty(ProteomicsRunSource.THERMO);
  private Property<Boolean> batchProperty = Properties.newProperty(true);
  private boolean allowExistingRuns;

  enum ProteomicsRunSource {
    THERMO("Upload Thermo Finnigan .RAW file(s)"),
    MZXML("Upload MzXML file(s)"),
    MGF("Upload Mascot Generic Format (.mgf) file(s) [Beta]"),
    EXISTING("Select existing peak list(s)");

    private final String description;

    private ProteomicsRunSource(final String description) {
      this.description = description;
    }

  }

  Property<ProteomicsRunSource> getProteomicsRunSourceProperty() {
    return proteomicsRunSourceProperty;
  }

  Property<Boolean> getBatchProperty() {
    return batchProperty;
  }

  ProteomicsRunSourceTypeWizardPageImpl(final boolean allowExistingRuns) {
    this.allowExistingRuns = allowExistingRuns;
    setTitle(CONSTANTS.runWizardSourceTypeTitle());
    setDescription(CONSTANTS.runWizardSourceTypeDescription());
    setValid(true);
    final RadioGroupItem sourceRadioGroupItem = getSourceItem();
    final CheckboxItem batchItem = getBatchItem();

    final Form form = new Form(sourceRadioGroupItem, batchItem);
    form.setNumCols(2);
    final Layout layout = SmartUtils.getFullVLayout();
    layout.addMember(form);
    this.setCanvas(layout);
  }

  private CheckboxItem getBatchItem() {
    final CheckboxItem batchItem = new CheckboxItem("batch", CONSTANTS.runWizardBatch());
    batchItem.setValue((boolean) batchProperty.get());
    SmartUtils.bindProperty(batchProperty, batchItem);
    return batchItem;
  }

  private RadioGroupItem getSourceItem() {
    final RadioGroupItem sourceRadioGroupItem = new RadioGroupItem("source");
    sourceRadioGroupItem.setShowTitle(false);
    sourceRadioGroupItem.setColSpan(2);
    sourceRadioGroupItem.setValueMap(getRadioOptions());
    sourceRadioGroupItem.setValue(ProteomicsRunSource.THERMO.toString());
    SmartUtils.addListener(sourceRadioGroupItem, getSourceListener());
    return sourceRadioGroupItem;
  }

  private Listener<String> getSourceListener() {
    return new Listener<String>() {
      public void onEvent(final String rawValue) {
        final String value = (!StringUtils.hasText(rawValue)) ? null : rawValue;
        if(value != null) {
          proteomicsRunSourceProperty.set(ProteomicsRunSource.valueOf(value));
          setValid(true);
        } else {
          setValid(false);
        }
      }
    };
  }

  private LinkedHashMap<String, String> getRadioOptions() {
    final LinkedHashMap<String, String> options = Maps.newLinkedHashMap();
    for(ProteomicsRunSource type : ProteomicsRunSource.values()) {
      if(!allowExistingRuns && type == ProteomicsRunSource.EXISTING) {
        continue;
      }
      options.put(type.toString(), type.description);
    }
    return options;
  }

}
