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

package edu.umn.msi.tropix.persistence.service;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.aop.Admin;
import edu.umn.msi.tropix.persistence.aop.AutoUser;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface GroupService {

  @PersistenceMethod Group createGroup(@UserId @Admin String adminId, String groupName);

  @PersistenceMethod void addUserToGroup(@UserId @Admin String adminId, @AutoUser String userId, String groupId);

  @PersistenceMethod void removeUserFromGroup(@UserId @Admin String adminId, String userId, String groupId);

  @PersistenceMethod Group[] getAllGroups(@UserId @Admin String adminId);

  @PersistenceMethod Group[] getGroups(@UserId String userId);

  @PersistenceMethod User[] getUsers(@UserId @Admin String adminId, String groupId);
}
