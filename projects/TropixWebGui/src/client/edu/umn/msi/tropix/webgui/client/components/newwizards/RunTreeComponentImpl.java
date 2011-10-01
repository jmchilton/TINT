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

import com.google.common.base.Predicate;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationJob;

public class RunTreeComponentImpl extends LocationSelectionComponentImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private static final Predicate<TreeItem> PROTEOMICS_RUN_PREDICATE = TreeItemPredicates.getTropixObjectTreeItemTypePredicate(
      TropixObjectTypeEnum.PROTEOMICS_RUN, false);

  public static class RunInputTypeEnum {
    public static InputType RUN = new InputTypeImpl("RUN", TropixObjectTypeEnum.PROTEOMICS_RUN, false, false, PROTEOMICS_RUN_PREDICATE,
        CONSTANTS.runWizardSelectByRun());
    public static InputType BATCH_RUNS = new InputTypeImpl("BATCH_RUNS", TropixObjectTypeEnum.PROTEOMICS_RUN, true, true, PROTEOMICS_RUN_PREDICATE,
        CONSTANTS.runWizardSelectByRuns());
    public static InputType BATCH_FOLDER = new InputTypeImpl("BATCH_FOLDER", TropixObjectTypeEnum.FOLDER, false, true,
        TreeItemPredicates.getFolderPredicate(), CONSTANTS.runWizardSelectByFolder());
  }

  public boolean mayHaveMultipleRuns() {
    final InputType inputType = getInputType();
    return inputType == RunInputTypeEnum.BATCH_FOLDER || Iterables.size(getIds()) > 1;
  }

  public RunTreeComponentImpl(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory,
      final Collection<TreeItem> treeItems,
      final boolean optionalRun, final ValidationListener validationListener) {
    this(treeComponentFactory, locationFactory, treeItems, optionalRun, Lists.<InputType>newArrayList(RunInputTypeEnum.BATCH_RUNS,
        RunInputTypeEnum.BATCH_FOLDER), validationListener);
  }

  RunTreeComponentImpl(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory, final Collection<TreeItem> treeItems,
      final boolean optionalRun, final List<InputType> validInputTypes, final ValidationListener validationListener) {
    super(treeComponentFactory, locationFactory, treeItems, optionalRun, validInputTypes, validationListener);
  }

  public void getRuns(final AsyncCallback<Collection<ProteomicsRun>> asyncCallback) {
    IdentificationJob.Util.getInstance().getRuns(getIds(), asyncCallback);
  }

}
