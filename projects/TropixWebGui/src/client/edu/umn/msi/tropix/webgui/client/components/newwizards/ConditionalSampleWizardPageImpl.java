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

import javax.annotation.Nullable;

import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ConditionalWidgetSupplier;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

class ConditionalSampleWizardPageImpl extends WizardPageImpl<Layout> {
  private TreeComponent sampleTree;
  private ConditionalWidgetSupplier conditionalSampleComponent;
  
  ConditionalSampleWizardPageImpl(final LocationFactory locationFactory, final TreeComponentFactory treeComponentFactory) {
    this.setTitle("Sample");
    this.setDescription("Select sample");
    final TropixObjectType[] types = new TropixObjectType[] {TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.SAMPLE};
    final TropixObjectTreeItemExpander expander = TropixObjectTreeItemExpanders.get(types);
    final TreeOptions treeOptions = new TreeOptions();
    treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(expander));
    treeOptions.setShowPredicate(TreeItemPredicates.getTropixObjectTreeItemTypePredicate(types, true));
    treeOptions.setSelectionPredicate(TreeItemPredicates.getTropixObjectNotFolderPredicate());
    sampleTree = treeComponentFactory.get(treeOptions);
    conditionalSampleComponent = SmartUtils.getConditionalSelectionWidget("Specify Sample", sampleTree, getValidationListener());
    setCanvas(conditionalSampleComponent.get());
    setValid(true);
  }

  @Nullable
  public String getSampleId() {
    if(conditionalSampleComponent.useSelection()) {
      return sampleTree.getSelection().getId();          
    } else {
      return null;
    }
  }
}
