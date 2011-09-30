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

package edu.umn.msi.tropix.webgui.client.models;

import java.util.Arrays;
import java.util.List;

import edu.umn.msi.tropix.webgui.client.utils.Lists;

public class NewItemFolderImpl extends NewItemModelImpl implements NewItemFolder, NewItemContext {
  private List<NewItemModel> children = Lists.newArrayList();
  
  public NewItemFolderImpl(final String name, final String description, final NewItemModelImpl... itemModels) {
    super(name, description);
    this.children.addAll(Arrays.asList(itemModels));
  }

  public void addModel(final NewItemModel model) {
    this.children.add(model);
  }

  public List<NewItemModel> getChildren() {
    return this.children;
  }

  public NewItemContext getChildContext(final String name) {
    for(NewItemModel model : children) {
      if(model instanceof NewItemFolderImpl && model.getName().equals(name)) {
        return (NewItemFolderImpl) model;
      }
    }
    final NewItemFolderImpl childContext = new NewItemFolderImpl(name, null);
    addModel(childContext);
    return childContext;
  }

}
