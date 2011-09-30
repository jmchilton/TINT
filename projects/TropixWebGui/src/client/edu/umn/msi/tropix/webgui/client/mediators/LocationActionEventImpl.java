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

package edu.umn.msi.tropix.webgui.client.mediators;

import java.util.ArrayList;
import java.util.Collection;

import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;

public final class LocationActionEventImpl implements ActionEvent {
  private final String actionType;
  private final Collection<TreeItem> treeItems;

  private LocationActionEventImpl(final String actionType, final Collection<TreeItem> treeItems) {
    this.actionType = actionType;
    this.treeItems = treeItems;
  }

  public static LocationActionEventImpl forItems(final String actionType, final Collection<TreeItem> treeItems) {
    return new LocationActionEventImpl(actionType, treeItems);
  }

  public String getActionType() {
    return actionType;
  }

  public Collection<TreeItem> getItems() {
    final Collection<TreeItem> items = new ArrayList<TreeItem>();
    if(treeItems != null) {
      items.addAll(treeItems);
    }
    return items;
  }

}
