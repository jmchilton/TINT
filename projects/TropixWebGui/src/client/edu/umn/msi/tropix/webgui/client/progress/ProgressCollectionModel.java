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

package edu.umn.msi.tropix.webgui.client.progress;

import java.util.ArrayList;

import edu.umn.msi.gwt.mvc.Model;

public class ProgressCollectionModel extends Model {
  private static final long serialVersionUID = 1L;

  public void addProgressModel(final ProgressModel model) {
    this.add(model);
  }

  public void clear() {
    this.removeAll();
  }

  public ProgressModel getProgressModel(final String id) {
    ProgressModel model = null;
    final int numJobs = this.getChildCount();
    for(int i = 0; i < numJobs; i++) {
      final ProgressModel progressModel = (ProgressModel) this.getChild(i);
      if(progressModel.getAsString("id").equals(id)) {
        model = progressModel;
        break;
      }
    }
    return model;
  }

  public void removeAllComplete() {
    final ArrayList<Model> children = new ArrayList<Model>(this.getChildren());
    for(final Model model : children) {
      final ProgressModel progressModel = (ProgressModel) model;
      if(progressModel.isCompleted()) {
        this.remove(progressModel);
      }
    }

  }
}
