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

package edu.umn.msi.tropix.webgui.client.components.tree;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.impl.TropixObjectTreeItemExpanderImpl;

public class TropixObjectTreeItemExpanders {
  public static TropixObjectTreeItemExpander forWithFolders(final TropixObjectType tropixObjectType) {
    return get(new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER, tropixObjectType});
  }

  public static TropixObjectTreeItemExpander get(final TropixObjectType[] types) {
    return new TropixObjectTreeItemExpanderImpl(types);
  }

  public static TropixObjectTreeItemExpander get() {
    return new TropixObjectTreeItemExpanderImpl(null);
  }

  public static TropixObjectTreeItemExpander nullExpander() {
    return new TropixObjectTreeItemExpander() {
      public void expand(final TropixObjectTreeItem tropixObjectTreeItem, final AsyncCallback<List<TropixObject>> callback) {
        callback.onSuccess(Arrays.<TropixObject>asList());
      }
    };
  }
}
