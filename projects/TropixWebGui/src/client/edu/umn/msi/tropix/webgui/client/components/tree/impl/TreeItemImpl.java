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

import java.util.Date;

import javax.annotation.Nullable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;

public abstract class TreeItemImpl implements TreeItem {
  private final TreeItem parent;
  private TreeItem root;
  private String type;
  private String icon;
  private String name;
  @Nullable
  private String sort;
  private String id;
  @Nullable
  private Date creationDate;
  private boolean valid = true;
  private boolean folder;

  protected TreeItemImpl(@Nullable final TreeItem parent) {
    this.parent = parent;
    if(parent == null) {
      this.root = this;
    } else {
      this.root = parent.getRoot();
    }
  }

  public void refresh(final AsyncCallback<Void> callback) {
  }

  public TreeItem getParent() {
    return this.parent;
  }

  public TreeItem getRoot() {
    return this.root;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getSort() {
    return sort == null ? name : sort;
  }

  public String getIcon() {
    return this.icon;
  }

  public String getType() {
    return this.type;
  }

  public Date getCreationDate() {
    return this.creationDate;
  }

  public boolean isFolder() {
    return this.folder;
  }

  public boolean isValid() {
    return this.valid;
  }

  protected void setId(final String id) {
    this.id = id;
  }

  protected void setName(final String name) {
    this.name = name;
  }

  @Nullable
  protected void setSort(final String sort) {
    this.sort = sort;
  }

  protected void setIcon(final String icon) {
    this.icon = icon;
  }

  protected void setType(final String type) {
    this.type = type;
  }

  @Nullable
  protected void setCreationDate(final Date creationDate) {
    this.creationDate = creationDate;
  }

  protected void setFolder(final boolean folder) {
    this.folder = folder;
  }

  protected void setValid(final boolean valid) {
    this.valid = valid;
  }

}