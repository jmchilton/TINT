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

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.webgui.client.components.LocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.impl.WindowComponentImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;

public abstract class WizardCommandComponentFactoryImpl implements LocationCommandComponentFactory<Command> {
  private TreeComponentFactory treeComponentFactory;
  private LocationFactory locationFactory;
  private MetadataWizardPageFactory metadataWizardPageFactory;

  public boolean acceptsLocations(final Collection<TreeItem> treeItem) {
    return true;
  }

  @Inject
  public void setTreeComponentFactory(final TreeComponentFactory treeComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
  }

  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  @Inject
  public void setMetadataWizardPageFactory(final MetadataWizardPageFactory metadataWizardPageFactory) {
    this.metadataWizardPageFactory = metadataWizardPageFactory;
  }

  public abstract Command get(final Collection<TreeItem> treeItems);

  protected TreeComponentFactory getTreeComponentFactory() {
    return treeComponentFactory;
  }

  protected LocationFactory getLocationFactory() {
    return locationFactory;
  }

  protected MetadataWizardPageFactory getMetadataWizardPageFactory() {
    return metadataWizardPageFactory;
  }

  static class WizardCommand extends WindowComponentImpl<Window> {
    private final Collection<TreeItem> locations;

    WizardCommand(final Collection<TreeItem> locations) {
      this.locations = locations;
    }

    protected Collection<TreeItem> getLocations() {
      return locations;
    }

  }

}
