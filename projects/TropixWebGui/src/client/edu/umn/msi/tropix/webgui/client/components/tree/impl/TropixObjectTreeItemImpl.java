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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.ModelUtils;
import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.client.models.ModelFunctions;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;

public class TropixObjectTreeItemImpl extends TreeItemImpl implements TropixObjectTreeItem {
  private static final Function<TropixObject, String> ICON_FUNCTION = ModelFunctions.getIconFunction16();
  private static final Function<TropixObject, String> TYPE_FUNCTION = ModelFunctions.getTypeFunction();
  private TropixObjectTreeItem tropixObjectTreeItemRoot;
  private TropixObjectContext tropixObjectContext;
  private TropixObject object;
  private final TropixObjectTreeItemExpander tropixObjectTreeItemExpander;

  // This method should only be called during construction
  private void init(final TropixObject object) {
    this.object = object;
    this.init(object, object.getId());
  }

  private void init(@Nullable final TropixObject object, final String id) {
    this.object = object;
    this.setId(id);
    if(object == null) {
      this.setValid(false);
    } else {
      this.setFolder(ModelUtils.hasChildren(object));
      this.setIcon(TropixObjectTreeItemImpl.ICON_FUNCTION.apply(object));
      this.setType(TropixObjectTreeItemImpl.TYPE_FUNCTION.apply(object));
      this.setName(StringUtils.sanitize(object.getName()));
      if(StringUtils.hasText(this.getType())) {
        this.setName(this.getName() + "&nbsp;<span style=\"color: #AAA; font-style: italic;\">(" + this.getType() + ")</span>");
      }
      if(object.getCreationTime() != null) {
        this.setCreationDate(new Date(Long.parseLong(object.getCreationTime())));
      }
    }
  }

  public TropixObjectTreeItemImpl(@Nullable final TreeItem parent,
                                  @Nullable final TropixObjectContext context,
                                  final TropixObject object,
                                  final TropixObjectTreeItemExpander tropixObjectTreeItemExpander) {
    super(parent);
    this.tropixObjectContext = context;
    this.init(object);
    if(parent != null && parent instanceof TropixObjectTreeItem) {
      this.tropixObjectTreeItemRoot = (TropixObjectTreeItem) parent;
    } else {
      this.tropixObjectTreeItemRoot = this;
    }
    this.tropixObjectTreeItemExpander = tropixObjectTreeItemExpander;
  }

  public TropixObjectTreeItemImpl(final TropixObjectTreeItemImpl parent, final TropixObject object) {
    super(parent);
    this.tropixObjectTreeItemRoot = parent.getTropixObjectTreeItemRoot();
    this.init(object);
    this.tropixObjectTreeItemExpander = parent.tropixObjectTreeItemExpander;
    this.tropixObjectContext = parent.getContext();
  }

  public TropixObject getObject() {
    return this.object;
  }

  public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
    this.tropixObjectTreeItemExpander.expand(this, new AsyncCallback<List<TropixObject>>() {
      public void onFailure(final Throwable throwable) {
        callback.onFailure(throwable);
      }

      public void onSuccess(final List<TropixObject> objects) {
        final ArrayList<TreeItem> items = new ArrayList<TreeItem>(objects.size());
        for(final TropixObject object : objects) {
          items.add(new TropixObjectTreeItemImpl(TropixObjectTreeItemImpl.this, object));
        }
        callback.onSuccess(items);
      }
    });
  }

  public String getId() {
    return this.object.getId();
  }

  public TropixObjectTreeItem getTropixObjectTreeItemRoot() {
    return this.tropixObjectTreeItemRoot;
  }

  public void refresh(final AsyncCallback<Void> callback) {
    if(this.isValid()) {
      System.out.println("Refreshing id " + object.getId() + " object " + object);
    }
    ObjectService.Util.getInstance().load(this.getId(), new AsyncCallback<TropixObject>() {
      public void onFailure(final Throwable throwable) {
        callback.onFailure(throwable);
      }

      public void onSuccess(final TropixObject object) {
        init(object, TropixObjectTreeItemImpl.this.getId());
        callback.onSuccess(null);
      }
    });
  }

  public TropixObjectContext getContext() {
    return tropixObjectContext;
  }
}
