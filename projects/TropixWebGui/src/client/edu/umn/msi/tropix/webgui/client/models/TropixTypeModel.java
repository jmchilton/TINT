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

import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

import edu.umn.msi.tropix.webgui.client.Resources;

public enum TropixTypeModel {
  TROPIX_OBJECT("Any Object", null, Resources.OBJECT_16), SAMPLE("Sample", TROPIX_OBJECT, Resources.SAMPLE_16), TISSUE_SAMPLE("Tissue Sample", SAMPLE, null), FOLDER("Folder", TROPIX_OBJECT, Resources.FOLDER_16), FILE("File", TROPIX_OBJECT, Resources.FILE_16), DATABASE(
      "Database", TROPIX_OBJECT, Resources.DATABASE_16), ANALYSIS("Analysis", TROPIX_OBJECT, Resources.ANALYSIS_16), PROTEIN_IDENTIFICATION_ANALYSIS("Identification Analysis", ANALYSIS, null), SCAFFOLD_ANALYSIS("Scaffold Analysis", ANALYSIS, null), PARAMETERS("Parameters",
      TROPIX_OBJECT, Resources.PARAMETERS_16), IDENTIFICATION_PARAMETERS("Identification Parameters", PARAMETERS, null), RUN("Run", TROPIX_OBJECT, Resources.RUN_16), PROTEOMICS_RUN("Proteomics Run", RUN, null), THERMOFINNIGAN_RUN("Thermofinnigan Run", PROTEOMICS_RUN, null), NOTE(
      "Wiki Note", TROPIX_OBJECT, Resources.NOTE_16);

  private final String displayName, icon;
  private final TropixTypeModel parent;

  public static Tree getTree() {
    final Tree tree = new Tree();
    final TropixTypeModel[] models = TropixTypeModel.values();
    final TreeNode[] nodes = new TreeNode[models.length];
    for(int i = 0; i < models.length; i++) {
      nodes[i] = models[i].getTreeNode();
    }
    tree.setData(nodes);
    tree.setRootValue("root");
    tree.setShowRoot(true);
    tree.setIdField("type");
    tree.setParentIdField("parentType");
    tree.setTitleProperty("name");
    tree.setModelType(TreeModelType.PARENT);
    return tree;
  }

  public TreeNode getTreeNode() {
    final TreeNode node = new TreeNode();
    node.setAttribute("name", this.displayName);
    node.setAttribute("type", this.toString());
    if(this.parent != null) {
      node.setAttribute("parentType", this.parent.toString());
    } else {
      node.setAttribute("parentType", "root");
    }
    node.setAttribute("icon", this.getIcon());
    return node;
  }

  private String getIcon() {
    TropixTypeModel model = this;
    while(model.icon == null) {
      model = model.parent;
    }
    return model.icon;
  }

  private TropixTypeModel(final String displayName, final TropixTypeModel parent, final String icon) {
    this.displayName = displayName;
    this.parent = parent;
    this.icon = icon;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public TropixTypeModel getParent() {
    return this.parent;
  }
}
