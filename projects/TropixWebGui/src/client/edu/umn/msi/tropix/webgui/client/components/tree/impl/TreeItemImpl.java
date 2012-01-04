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

package edu.umn.msi.tropix.webgui.client.components.tree.impl;

import javax.annotation.Nullable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.models.locations.Location;
import edu.umn.msi.tropix.models.locations.LocationImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;

public abstract class TreeItemImpl extends LocationImpl implements TreeItem {
  private String icon;
  private boolean valid = true;

  protected TreeItemImpl(@Nullable final Location parent) {
    super(parent);
  }

  public void refresh(final AsyncCallback<Void> callback) {
  }

  public String getIcon() {
    return this.icon;
  }

  public boolean isValid() {
    return this.valid;
  }

  protected void setIcon(final String icon) {
    this.icon = icon;
  }

  protected void setValid(final boolean valid) {
    this.valid = valid;
  }

  
}