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

import java.util.Collection;

import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.DescribableLocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;

public class RenameCommandComponentFactoryImpl implements DescribableLocationCommandComponentFactory<Command> {

  public boolean acceptsLocations(final Collection<TreeItem> treeItems) {
    // Condition is there is there is one object and it is a tropix object tree item with a parent (no renaming root).
    if(treeItems == null || treeItems.size() != 1) {
      return false;
    }
    final TreeItem item = treeItems.iterator().next();
    return item instanceof TropixObjectTreeItem && item.getParent() != null;
  }


  public Command get(final Collection<TreeItem> input) {
    return new WindowComponent((TropixObjectTreeItem) Iterables.getOnlyElement(input));
  }

  static class WindowComponent extends WindowComponentImpl<Window> {

    WindowComponent(final TropixObjectTreeItem treeItem) {
      final TextItem textItem = new TextItem("name", "New Name");
      textItem.setValue(treeItem.getObject().getName());
      final Form form = new Form();
      form.setItems(textItem);
      final Button okButton = SmartUtils.getButton("Rename", Resources.OK, new Command() {
        public void execute() {
          ObjectService.Util.getInstance().rename(treeItem.getId(), textItem.getValue().toString(), new AsyncCallbackImpl<Void>() {
            @Override
            public void onSuccess(final Void result) {
              LocationUpdateMediator.getInstance().onEvent(new UpdateEvent(treeItem.getId(), null));
            }
          });
          get().destroy();
        }
      });
      final CanvasWithOpsLayout<DynamicForm> layout = new CanvasWithOpsLayout<DynamicForm>(form, okButton);
      setWidget(PopOutWindowBuilder.titled("Rename").withIcon(Resources.EDIT_CLEAR).autoSized().withContents(layout).get());
    }
    
  }

  public String getDescription() {
    return "Rename";
  }
  
}
