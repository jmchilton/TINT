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

package edu.umn.msi.tropix.webgui.client.components.tree;

import com.google.common.base.Predicate;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;

public class TropixObjectTreeItemPredicate implements Predicate<TreeItem> {
  /**
   * The default value to return when this predicate is applied to items which are not TropixObjectTreeItem. For instance, in selection predicates this should usually be false, but for expansion predicates this should usually be true. So that items like My Recent Searches appear,
   * but cannot be selected.
   */
  private final boolean notTropixObjectDefault;

  public TropixObjectTreeItemPredicate() {
    this(false);
  }

  public TropixObjectTreeItemPredicate(final boolean notTropixObjectDefault) {
    this.notTropixObjectDefault = notTropixObjectDefault;
  }

  protected boolean apply(final TropixObject tropixObject) {
    return true;
  }

  public boolean apply(final TreeItem treeItem) {
    if(treeItem == null || !(treeItem instanceof TropixObjectTreeItem)) {
      return this.notTropixObjectDefault;
    } else {
      return this.apply(((TropixObjectLocation) treeItem).getObject());
    }
  }

}
