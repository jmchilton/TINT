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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.Arrays;

import com.google.common.base.Supplier;
import com.google.inject.Inject;

import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.components.tree.impl.TreeComponentFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;

public class VirtualFolderSelectionWindowComponentSupplierImpl extends SelectionWindowComponentSupplierImpl<TreeItem> {

  @Inject
  public VirtualFolderSelectionWindowComponentSupplierImpl(final LocationFactory locationFactory) {
    super.setWindowSupplier(PopOutWindowBuilder.titled("Select Shared Folder").withIcon(Resources.SHARED_FOLDER_16).modal());
    final TreeComponentFactoryImpl treeFactory = new TreeComponentFactoryImpl();

    treeFactory.setDefaultTreeOptionsSupplier(new Supplier<TreeOptions>() {
      public TreeOptions get() {
        final TreeOptions treeOptions = new TreeOptions();
        treeOptions.setInitialItems(Arrays.<TreeItem>asList(locationFactory.getMySharedFoldersItem(TropixObjectTreeItemExpanders.get(new TropixObjectType[] {TropixObjectTypeEnum.VIRTUAL_FOLDER}))));
        treeOptions.setSelectionPredicate(LocationPredicates.getIsTropixObjectTreeItemPredicate());
        treeOptions.setSelectionType(SelectionType.SINGLE);
        return treeOptions;
      }
    });

    super.setSelectionComponentSupplier(treeFactory);
  }
}
