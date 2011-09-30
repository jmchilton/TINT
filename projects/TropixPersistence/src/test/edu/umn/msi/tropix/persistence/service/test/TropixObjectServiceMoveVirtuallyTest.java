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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixObjectServiceMoveVirtuallyTest extends ServiceTest {
  @Autowired
  private TropixObjectService tropixObjectService;

  @Test
  public void moveVirtually() {
    final User user1 = createTempUser();
    final VirtualFolder root = createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);

    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());

    assert root.getContents().size() == 1;
    assert sub1.getContents().size() == 1;
    tropixObjectService.moveVirtually(user1.getCagridId(), sub1.getId(), sub2.getId(), root.getId());
    assert root.getContents().size() == 2;
    assert sub1.getContents().size() == 0;

  }

  @Test(expectedExceptions = RuntimeException.class)
  public void moveVirtuallyAncestor() {
    final User user1 = createTempUser();
    final VirtualFolder root = createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder(), sub3 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub3.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    sub3.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    saveNewTropixObject(sub3);

    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());
    getTropixObjectDao().addToVirtualFolder(sub2.getId(), sub3.getId());
    getTropixObjectDao().copyVirtualPermissions(sub2.getId(), sub3.getId());

    tropixObjectService.moveVirtually(user1.getCagridId(), sub2.getId(), sub1.getId(), sub3.getId());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void moveVirtuallyRootAncestor() {
    final User user1 = createTempUser();
    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder(), sub3 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub3.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    sub3.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    saveNewTropixObject(sub3);

    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());
    getTropixObjectDao().addToVirtualFolder(sub2.getId(), sub3.getId());
    getTropixObjectDao().copyVirtualPermissions(sub2.getId(), sub3.getId());

    tropixObjectService.moveVirtually(user1.getCagridId(), sub2.getId(), root.getId(), sub3.getId());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void moveVirtuallyWrongRoot() {
    final User user1 = createTempUser();
    final VirtualFolder root1 = super.createTempRootVirtualFolder(), root2 = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root1.getId(), "write", user1.getCagridId());
    getTropixObjectDao().addVirtualPermissionUser(root2.getId(), "write", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);

    getTropixObjectDao().addToVirtualFolder(root1.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root1.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(root2.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(root2.getId(), sub2.getId());

    tropixObjectService.moveVirtually(user1.getCagridId(), root2.getId(), sub2.getId(), sub1.getId());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void moveVirtuallyExisting() {
    final User user1 = createTempUser();
    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);

    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());

    final TropixObject object1 = new TropixObject();
    saveNewTropixObject(object1);

    getTropixObjectDao().addToVirtualFolder(sub1.getId(), object1.getId());
    getTropixObjectDao().addToVirtualFolder(sub2.getId(), object1.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), object1.getId());

    tropixObjectService.moveVirtually(user1.getCagridId(), sub2.getId(), object1.getId(), sub1.getId());
  }

}
