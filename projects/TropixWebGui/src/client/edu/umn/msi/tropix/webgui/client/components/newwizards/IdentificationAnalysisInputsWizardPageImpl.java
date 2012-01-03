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
import java.util.List;

import javax.annotation.Nullable;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;
import edu.umn.msi.tropix.webgui.client.components.newwizards.LocationSelectionComponentImpl.InputType;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

public class IdentificationAnalysisInputsWizardPageImpl extends WizardPageImpl<VLayout> {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private Database database = null;
  private RunTreeComponentImpl runComponent;

  IdentificationAnalysisInputsWizardPageImpl(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory,
      final Collection<TreeItem> treeItems, @Nullable final InputType inputType, final boolean optionalRun) {
    this.setCanvas(new VLayout());
    this.setTitle(CONSTANTS.idWizardInputsTitle());
    this.setDescription(CONSTANTS.idWizardInputsDescription());
    final ValidationListener validationListener = new ValidationListener() {
      public void onValidation(final boolean isValid) {
        checkValid(isValid);
      }
    };
    if(inputType == null) {
      runComponent = new RunTreeComponentImpl(treeComponentFactory, locationFactory, treeItems, optionalRun, validationListener);
    } else {
      runComponent = new RunTreeComponentImpl(treeComponentFactory, locationFactory, treeItems, optionalRun,
          Lists.<InputType>newArrayList(inputType), validationListener);
    }
    final Layout inputTreeLayout = runComponent.get();
    inputTreeLayout.setWidth("100%");
    inputTreeLayout.setHeight("40%");

    final TreeComponent databaseTree = Utils.getDatabaseTreeComponent(locationFactory, treeComponentFactory, treeItems);
    // TODO: Clean this up, the selection predicate should negate the need for these checks
    databaseTree.addSelectionListener(new Listener<TreeItem>() {
      public void onEvent(final TreeItem treeItem) {
        database = null;
        if(treeItem instanceof TropixObjectTreeItem) {
          final TropixObject object = ((TropixObjectLocation) treeItem).getObject();
          database = (Database) ((treeItem == null || !(object instanceof Database)) ? null : object);
        }
        checkValid();
      }
    });
    final TreeGrid databaseTreeGrid = databaseTree.get();
    databaseTreeGrid.setWidth("100%");
    databaseTreeGrid.setHeight("40%");

    this.getCanvas().addMember(inputTreeLayout);
    final Label databaseLabel = SmartUtils.smartParagraph(CONSTANTS.idWizardInputsSelectDatabase());
    databaseLabel.setHeight(20);
    this.getCanvas().addMember(databaseLabel);
    this.getCanvas().addMember(databaseTreeGrid);

  }

  private void checkValid() {
    checkValid(runComponent.isValid());
  }

  private void checkValid(final boolean runIsValid) {
    setValid(runIsValid && database != null);
  }

  public InputType getInputType() {
    return runComponent.getInputType();
  }

  public List<String> getRunIds() {
    return runComponent.getIds();
  }

  public String getRunId() {
    return getRunIds().isEmpty() ? null : getRunIds().iterator().next();
  }

  public Database getDatabase() {
    return database;
  }

  public String getDatabaseId() {
    return database.getId();
  }
}
