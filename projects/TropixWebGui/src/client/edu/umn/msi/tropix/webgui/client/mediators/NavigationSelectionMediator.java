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

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.models.utils.TropixObjectUserAuthorities;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;

public class NavigationSelectionMediator implements Listener<NavigationSelectionMediator.NavigationSelection> {
  private final ListenerList<NavigationSelection> contextListeners = ListenerLists.getInstance();
  private NavigationSelection lastItem;
  private final LocationFactory locationFactory;

  @Inject
  public NavigationSelectionMediator(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  public static class NavigationSelection {
    private final Collection<TreeItem> locations;
    private final Object source;

    public NavigationSelection(final Collection<TreeItem> locations) {
      this(locations, null);
    }

    public NavigationSelection(final TreeItem location) {
      this(location, null);
    }

    public NavigationSelection(final Collection<TreeItem> locations, final Object source) {
      this.locations = locations;
      this.source = source;
    }

    public NavigationSelection(final TreeItem location, final Object source) {
      this(Arrays.asList(location), source);
    }

    public Collection<TreeItem> getLocations() {
      return this.locations;
    }

    public Object getSource() {
      return this.source;
    }

  }

  public void addNavigationSelectionChangedListener(final Listener<NavigationSelection> selectionChangedListener) {
    this.contextListeners.add(selectionChangedListener);
  }

  public void go(final TropixObjectContext<? extends TropixObject> tropixObjectWithContext) {
    final TropixObject tropixObject = tropixObjectWithContext.getTropixObject();
    final TropixObjectUserAuthorities authorities = tropixObjectWithContext.getTropixObjectContext();
    Preconditions.checkNotNull(tropixObject);
    final TropixObjectTreeItem treeItem = locationFactory.getTropixObjectTreeItem(null, authorities, tropixObject, null);
    go(treeItem);
  }

  public void go(final TropixObjectTreeItem tropixObjectLocation) {
    this.onEvent(new NavigationSelection(tropixObjectLocation));
  }

  public void onEvent(final NavigationSelection treeItem) {
    if(this.lastItem == treeItem) {
      return;
    }
    this.lastItem = treeItem;
    this.contextListeners.onEvent(treeItem);
  }

}
