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

import java.util.Collection;

import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.identification.ParametersPanel;
import edu.umn.msi.tropix.webgui.client.identification.ParametersPanelFactory;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

class ParametersWizardPage extends WizardPageImpl<VLayout> {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private final Collection<TreeItem> locations;
  private final ParametersPanelFactory parametersPanelFactory;
  private String currentParametersType = null;
  private ParametersPanel currentPanel = null;

  public String getCurrentParametersType() {
    return currentParametersType;
  }

  public ParametersPanel getCurrentPanel() {
    return currentPanel;
  }

  ParametersWizardPage(final Collection<TreeItem> locations, final ParametersPanelFactory parametersPanelFactory) {
    this.locations = locations;
    this.parametersPanelFactory = parametersPanelFactory;
    this.setCanvas(new VLayout());
    this.setTitle(CONSTANTS.idWizardParametersTitle());
    this.setDescription(CONSTANTS.idWizardParametersDescription());
  }

  void setParametersType(final String type) {
    if(type.equals(this.currentParametersType)) {
      return;
    }
    final TreeItem parametersItem = Iterables.find(locations, TreeItemPredicates.getIdentificationParametersOfTypePredicate(type, false));
    final String parametersId = parametersItem == null ? null : parametersItem.getId();
    final ParametersPanel parametersPanel = parametersPanelFactory.createParametersPanel(type, new AsyncCallbackImpl<ParametersPanel>(), false,
        parametersId);
    parametersPanel.addValidationListener(new ValidationListener() {
      public void onValidation(final boolean isValid) {
        setValid(isValid);
      }
    });
    parametersPanel.setSize("100%", "100%");
    if(this.currentPanel != null) {
      this.getCanvas().removeMember(this.currentPanel);
    }
    this.getCanvas().addMember(parametersPanel);
    this.currentPanel = parametersPanel;
    this.currentParametersType = type;
  }
}