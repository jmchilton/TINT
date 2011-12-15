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

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.Run;
import edu.umn.msi.tropix.models.Sample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

public class FolderServiceTest extends ServiceTest {
  @Autowired
  private FolderService folderService;

  @Autowired
  private SecurityProvider securityProvider;

  @Test
  public void testGetOrCreateVirtualFolderWithName() {
    final User tempUser = createTempUser();
    final VirtualFolder f1 = createTempRootVirtualFolder();
    getUserDao().addVirtualFolder(tempUser.getCagridId(), f1.getId());
    getTropixObjectDao().setOwner(f1.getId(), tempUser);
    f1.setName("moo");
    getTropixObjectDao().saveOrUpdateTropixObject(f1);
    assert folderService.getOrCreateRootVirtualFolderWithName(tempUser.getCagridId(), "moo").equals(f1);

    final VirtualFolder f2 = folderService.getOrCreateRootVirtualFolderWithName(tempUser.getCagridId(), "cow");
    assert f2.getName().equals("cow");
    assert f2.getCommitted();
    final VirtualFolder f2copy = folderService.getOrCreateRootVirtualFolderWithName(tempUser.getCagridId(), "cow");
    assert f2.equals(f2copy);
  }

  @Test
  public void testGetOrCreateVirtualPath() {
    final User tempUser = createTempUser();
    final VirtualFolder rootVirtualFolder = createTempRootVirtualFolder();
    super.getTropixObjectDao().setOwner(rootVirtualFolder.getId(), tempUser);

    final VirtualFolder lastFolder = folderService.getOrCreateVirtualPath(tempUser.getCagridId(), rootVirtualFolder.getId(), new String[] {"moo",
        "cow"});

    assert lastFolder.getName().equals("cow");
    final VirtualFolder mooFolder = lastFolder.getParentVirtualFolders().iterator().next();
    assert mooFolder.getName().equals("moo");
    assert !lastFolder.getRoot();

    final VirtualFolder lastFolder2 = folderService.getOrCreateVirtualPath(tempUser.getCagridId(), rootVirtualFolder.getId(), new String[] {"moo",
        "cow2"});
    assert lastFolder2.getName().equals("cow2");
    // Make sure moo folder wasn't recreated.
    assert lastFolder2.getParentVirtualFolders().iterator().next().equals(mooFolder);
    assert !lastFolder2.getRoot();

    assert rootVirtualFolder.equals(folderService.getOrCreateVirtualPath(tempUser.getCagridId(), rootVirtualFolder.getId(), new String[0]));
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void createVirtualFolderSameName() {
    final User user = createTempUser();

    VirtualFolder root = new VirtualFolder();
    root.setName("moo");
    folderService.createVirtualFolder(user.getCagridId(), null, root);

    final VirtualFolder root2 = new VirtualFolder();
    root2.setName("moo");
    folderService.createVirtualFolder(user.getCagridId(), null, root2);
  }

  @Test
  public void createGroupSharedFolder() {
    final User user = createTempUser();
    final Group group = createTempGroup(user, true);

    VirtualFolder root = getInputVirtualFolder();
    root = folderService.createGroupVirtualFolder(user.getCagridId(), group.getId(), root);
    assertValidSharedRootFolder(root);
    assert group.getSharedFolders().contains(root);
    assertGroupCanRead(root, group);
    assertGroupCanModify(root, group);
  }

  private VirtualFolder getInputVirtualFolder() {
    final VirtualFolder root = new VirtualFolder();
    root.setName("moo");
    return root;
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void testOnlyGroupMemberCanCreateGroupSharedFolder() {
    final User user = createTempUser();
    final Group group = createTempGroup();

    VirtualFolder root = getInputVirtualFolder();
    folderService.createGroupVirtualFolder(user.getCagridId(), group.getId(), root);
  }

  private void assertGroupCanRead(final TropixObject tropixObject, final Group group) {
    final User newUser = createTempUser(group);
    assert securityProvider.canRead(tropixObject.getId(), newUser.getCagridId());
  }

  private void assertGroupCanModify(final TropixObject tropixObject, final Group group) {
    final User newUser = createTempUser(group);
    assert securityProvider.canModify(tropixObject.getId(), newUser.getCagridId());
  }

  private void assertValidSharedRootFolder(final VirtualFolder root) {
    assert root.getRoot();
    assert root.getCommitted();
    assert root.getName().equals("moo");
  }

  @Test
  public void createVirtualFolders() {
    final User user = createTempUser();

    VirtualFolder root = getInputVirtualFolder();
    root = folderService.createVirtualFolder(user.getCagridId(), null, root);

    assertValidSharedRootFolder(root);
    assert user.getSharedFolders().contains(root);

    VirtualFolder child1 = new VirtualFolder();
    child1.setName("child1");
    child1 = folderService.createVirtualFolder(user.getCagridId(), root.getId(), child1);

    assert !child1.getRoot();
    assert child1.getCommitted();
    assert child1.getName().equals("child1");

    VirtualFolder child2 = new VirtualFolder();
    child2.setName("child2");
    child2 = folderService.createVirtualFolder(user.getCagridId(), child1.getId(), child2);

    assert !child2.getRoot();
    assert child2.getCommitted();
    assert child2.getName().equals("child2");

  }

  @Test
  public void createAndGetContents() {
    final User newUser = createTempUser();
    assert getTropixObjectDao().getOwner(newUser.getHomeFolder().getId()).getCagridId().equals(newUser.getCagridId());
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    final String homeId = newUser.getHomeFolder().getId();
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    assert getTropixObjectDao().getFolderContents(homeId).size() == 0;
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    final Folder newFolder1 = new Folder();
    newFolder1.setName("New Folder 1");
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    assert folderService.getFolderContents(newUser.getCagridId(), newUser.getHomeFolder().getId()).length == 0;
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    folderService.createFolder(newUser.getCagridId(), newUser.getHomeFolder().getId(), newFolder1);
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    assert getTropixObjectDao().getOwner(newFolder1.getId()).getCagridId().equals(newUser.getCagridId());
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    assert securityProvider.canModify(newFolder1.getId(), newUser.getCagridId());
    assert securityProvider.canRead(newUser.getHomeFolder().getId(), newUser.getCagridId());
    final TropixObject[] contents = folderService.getFolderContents(newUser.getCagridId(), newUser.getHomeFolder().getId());

    assert contents.length == 1;

    final String subFolderId = contents[0].getId();
    assert folderService.getFolderContents(newUser.getCagridId(), subFolderId).length == 0;

    final Folder newFolder2 = new Folder();
    newFolder2.setName("New Folder 2");

    folderService.createFolder(newUser.getCagridId(), subFolderId, newFolder2);
    assert folderService.getFolderContents(newUser.getCagridId(), subFolderId).length == 1;

    final Folder newFolder3 = new Folder();
    newFolder3.setName("New Folder 3");
    folderService.createFolder(newUser.getCagridId(), subFolderId, newFolder3);
    assert folderService.getFolderContents(newUser.getCagridId(), subFolderId).length == 2;
  }

  @Test
  public void create() {
    final User newUser = createTempUser();
    final String homeId = newUser.getHomeFolder().getId();
    assert getTropixObjectDao().getFolderContents(homeId).size() == 0;
    final Folder newFolder1 = new Folder();
    newFolder1.setName("New Folder 1");
    folderService.createFolder(newUser.getCagridId(), newUser.getHomeFolder().getId(), newFolder1);
    assert getTropixObjectDao().getOwner(newFolder1.getId()).getCagridId().equals(newUser.getCagridId());
    assert getTropixObjectDao().loadTropixObject(newFolder1.getId()).getName().equals("New Folder 1");
    assert getTropixObjectDao().loadTropixObject(newFolder1.getId()).getCommitted();
    assert getTropixObjectDao().getFolderContents(homeId).size() == 1;

    final Folder newFolder2 = new Folder();
    newFolder2.setName("New Folder 2");
    folderService.createFolder(newUser.getCagridId(), newUser.getHomeFolder().getId(), newFolder2);
    assert getTropixObjectDao().loadTropixObject(newFolder2.getId()).getName().equals("New Folder 2");
    assert getTropixObjectDao().getFolderContents(homeId).size() == 2;
    assert getTropixObjectDao().getFolderContents(newFolder2.getId()).size() == 0;

    final Folder newFolder3 = new Folder();
    newFolder3.setName("New Folder 3");
    folderService.createFolder(newUser.getCagridId(), newFolder2.getId(), newFolder3);

    assert getTropixObjectDao().loadTropixObject(newFolder3.getId()).getName().equals("New Folder 3");
    assert getTropixObjectDao().loadTropixObject(newFolder3.getId()).getPermissionParents().iterator().next().equals(newFolder2);
    assert getTropixObjectDao().getFolderContents(newFolder2.getId()).size() == 1;

  }

  @Test
  public void contents() {
    final User newUser = createTempUser();
    final Folder root = new Folder();
    root.setName("root");
    root.setCommitted(true);
    root.setContents(new HashSet<TropixObject>());

    final Folder bin = new Folder();
    bin.setName("bin");
    bin.setCommitted(true);
    bin.setContents(new HashSet<TropixObject>());

    final Folder var = new Folder();
    var.setName("var");
    var.setCommitted(true);
    var.setContents(new HashSet<TropixObject>());

    final Folder usr = new Folder();
    usr.setName("usr");
    usr.setCommitted(true);
    usr.setContents(new HashSet<TropixObject>());

    final Folder local = new Folder();
    local.setName("local");
    local.setCommitted(true);
    local.setContents(new HashSet<TropixObject>());

    final Sample sample = new Sample();
    sample.setName("Sample1");
    sample.setCommitted(true);

    final Run run1 = new Run();
    run1.setName("Run1");
    run1.setCommitted(true);

    final Run run2 = new Run();
    run2.setName("Run2");
    run2.setCommitted(true);

    final TropixFile file1 = new TropixFile();
    file1.setCommitted(false);

    saveNewTropixObject(root);
    saveNewTropixObject(bin);
    saveNewTropixObject(var);
    saveNewTropixObject(usr);
    saveNewTropixObject(local);

    saveNewTropixObject(sample);
    saveNewTropixObject(run1);
    saveNewTropixObject(run2);
    saveNewTropixObject(file1);

    getTropixObjectDao().setOwner(root.getId(), newUser);
    getTropixObjectDao().setOwner(bin.getId(), newUser);
    getTropixObjectDao().setOwner(var.getId(), newUser);
    getTropixObjectDao().setOwner(usr.getId(), newUser);
    getTropixObjectDao().setOwner(local.getId(), newUser);

    getTropixObjectDao().setOwner(sample.getId(), newUser);
    getTropixObjectDao().setOwner(run1.getId(), newUser);
    getTropixObjectDao().setOwner(run2.getId(), newUser);
    getTropixObjectDao().setOwner(file1.getId(), newUser);

    assert folderService.getFolderContents(newUser.getCagridId(), root.getId()).length == 0;
    assert folderService.getFolderContents(newUser.getCagridId(), root.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER}).length == 0;
    assert folderService.getFolderContents(newUser.getCagridId(), root.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER,
        TropixObjectTypeEnum.SAMPLE}).length == 0;

    getTropixObjectDao().addToFolder(root.getId(), bin.getId());
    getTropixObjectDao().addToFolder(root.getId(), var.getId());
    getTropixObjectDao().addToFolder(root.getId(), usr.getId());
    getTropixObjectDao().addToFolder(usr.getId(), local.getId());
    getTropixObjectDao().addToFolder(root.getId(), file1.getId());

    final int numContents = folderService.getFolderContents(newUser.getCagridId(), root.getId()).length;
    assert numContents == 3 : numContents;
    assert folderService.getFolderContents(newUser.getCagridId(), root.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER}).length == 3;
    assert folderService.getFolderContents(newUser.getCagridId(), root.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER,
        TropixObjectTypeEnum.SAMPLE}).length == 3;

    getTropixObjectDao().addToFolder(root.getId(), sample.getId());

    assert folderService.getFolderContents(newUser.getCagridId(), root.getId()).length == 4;
    assert folderService.getFolderContents(newUser.getCagridId(), root.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER}).length == 3;
    assert folderService.getFolderContents(newUser.getCagridId(), root.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER,
        TropixObjectTypeEnum.SAMPLE}).length == 4;

    getTropixObjectDao().addToFolder(local.getId(), run1.getId());
    getTropixObjectDao().addToFolder(local.getId(), run2.getId());

    assert folderService.getFolderContents(newUser.getCagridId(), usr.getId()).length == 1;
    assert folderService.getFolderContents(newUser.getCagridId(), usr.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER}).length == 1;
    assert folderService.getFolderContents(newUser.getCagridId(), usr.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.RUN}).length == 0;

    assert folderService.getFolderContents(newUser.getCagridId(), local.getId()).length == 2;
    assert folderService.getFolderContents(newUser.getCagridId(), local.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER}).length == 0;
    assert folderService.getFolderContents(newUser.getCagridId(), local.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.RUN}).length == 2;
  }

}
