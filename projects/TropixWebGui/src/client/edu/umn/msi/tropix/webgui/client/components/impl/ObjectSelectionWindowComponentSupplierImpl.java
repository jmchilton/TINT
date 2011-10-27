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

import com.google.common.base.Supplier;
import com.google.inject.Inject;

import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.components.tree.impl.TreeComponentFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;

public class ObjectSelectionWindowComponentSupplierImpl extends SelectionWindowComponentSupplierImpl<TreeItem> {

  @Inject
  public ObjectSelectionWindowComponentSupplierImpl(final LocationFactory locationFactory, final Session session) {
    super.setWindowSupplier(PopOutWindowBuilder.titled("Select Objects").modal());
    final TreeComponentFactoryImpl treeFactory = new TreeComponentFactoryImpl();
    treeFactory.setDefaultTreeOptionsSupplier(new Supplier<TreeOptions>() {
      public TreeOptions get() {
        final TreeOptions treeOptions = new TreeOptions();
        treeOptions.setInitialItems(locationFactory.getConcreteTropixObjectRootItems(TropixObjectTreeItemExpanders.get()));
        treeOptions.setSelectionPredicate(TreeItemPredicates.getIsTropixObjectTreeItemPredicate());
        treeOptions.setSelectionType(SelectionType.MULTIPlE);
        return treeOptions;
      }
    });
    super.setSelectionComponentSupplier(treeFactory);
  }

}
