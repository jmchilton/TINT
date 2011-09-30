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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.HashSet;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;

public class Utils {
  static void initObject(final TropixObject object) {
    //Preconditions.checkState(object.getId() == null, "Object with nonnull id sent to initAndSaveObject id is " + object.getId());
    Preconditions.checkState(object.getCreationTime() == null, "Object with creation time already sent to initAndSaveObject id is " + object.getId());
    setCreationTime(object);
    object.setPermissionParents(new HashSet<TropixObject>());
    object.setPermissionChildren(new HashSet<TropixObject>());
    object.setParentVirtualFolders(new HashSet<VirtualFolder>());      
  }
  
  static void setCreationTime(final TropixObject object) {
    object.setCreationTime("" + System.currentTimeMillis());
  }
}
