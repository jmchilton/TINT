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

package edu.umn.msi.tropix.webgui.server;

import com.google.common.base.Function;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionSourceType;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;
import edu.umn.msi.tropix.webgui.services.object.Permission;

public class PermissionFunction implements Function<PermissionReport, Permission> {
  private Function<String, GridUser> gridUserFunction;

  public void setGridUserFunction(final Function<String, GridUser> gridUserFunction) {
    this.gridUserFunction = gridUserFunction;
  }

  public Permission apply(final PermissionReport report) {
    final Permission permission = new Permission();
    permission.setId(report.getId());
    permission.setName(report.getName());
    permission.setImmutable(PermissionType.Owner.equals(report.getPermission()));

    if(PermissionType.Read.equals(report.getPermission())) {
      permission.setType(Permission.Type.READ);
    } else if(report.getPermission() != null) {
      permission.setType(Permission.Type.WRITE);
    } else {
      permission.setType(Permission.Type.FOLDER);
    }
    if(PermissionSourceType.User.equals(report.getPermissionSource())) {
      final GridUser user = gridUserFunction.apply(report.getId());
      if(user != null) {
        permission.setName(user.toString());
      }
      permission.setSource(Permission.Source.USER);
    } else if(PermissionSourceType.Group.equals(report.getPermissionSource())) {
      permission.setSource(Permission.Source.GROUP);
    } else {
      permission.setSource(Permission.Source.SHARED_FOLDER);
    }
    return permission;
  }

}
