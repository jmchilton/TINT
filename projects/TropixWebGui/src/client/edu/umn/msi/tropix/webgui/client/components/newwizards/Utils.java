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

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Predicate;

import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;

class Utils {
  private static final Predicate<TreeItem> DATABASE_PREDICATE = TreeItemPredicates.getTropixObjectTreeItemTypePredicate(TropixObjectTypeEnum.DATABASE, false);

  static TreeComponent getDatabaseTreeComponent(final LocationFactory locationFactory, final TreeComponentFactory treeComponentFactory, final Collection<TreeItem> locations) {
    final TropixObjectType[] dbTypes = new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.DATABASE};
    final TreeOptions databaseTreeOptions = new TreeOptions();
    databaseTreeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(TropixObjectTreeItemExpanders.get(dbTypes)));
    databaseTreeOptions.setShowPredicate(TreeItemPredicates.showDatabaseTypePredicate("FASTA"));
    databaseTreeOptions.setSelectionPredicate(TreeItemPredicates.selectDatabaseTypePredicate("FASTA"));
    databaseTreeOptions.expandAndSelectValidItem(locations);

    final TreeComponent databaseTree = treeComponentFactory.get(databaseTreeOptions);    
    return databaseTree;
  }
    
  private static boolean endsWithExtension(final String fileName, final String extension) {
    return fileName != null && fileName.toLowerCase().endsWith(extension.toLowerCase());
  }
  
  static String stripExtension(final String fileName, final String extension) {
    String strippedFileName = fileName;
    if(endsWithExtension(fileName, extension)) {      
      strippedFileName = fileName.substring(0, fileName.length() - extension.length());
    }
    return strippedFileName;
  }
  
  static String stripRawExtension(final String fileName) {
    return stripExtension(fileName, ".raw");
  }

  static String stripMzxmlExtension(final String fileName) {
    return stripExtension(fileName, ".mzxml");
  }

}
