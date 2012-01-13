package edu.umn.msi.tropix.persistence.service.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixObjectServiceMoveGroupFoldersTest extends ServiceTest {
  @Autowired
  private TropixObjectService tropixObjectService;
  private User user1;
  private User user2;
  private User user3;
  private Folder groupFolder;

  @BeforeMethod
  public void init() {
    user1 = createTempUser();
    user2 = createTempUser();
    user3 = createTempUser();
    groupFolder = super.createTempGroupFolder(user1, user2, "Root");

  }

  @Test
  public void moveToGroupFolder() {
    final TropixObject object = saveToParent(new TropixObject(), user1.getHomeFolder(), user1);
    final TropixObject childObject = saveToParentObject(new TropixObject(), object, user1);

    getTropixObjectDao().addRole(object.getId(), "read", user3);

    assert object.getPermissionParents().contains(user1.getHomeFolder());
    assert !object.getPermissionParents().contains(groupFolder);
    assert getSecurityProvider().canRead(object.getId(), user1.getCagridId());
    assert !getSecurityProvider().canRead(object.getId(), user2.getCagridId());
    assert getSecurityProvider().canRead(object.getId(), user3.getCagridId());

    tropixObjectService.move(user1.getCagridId(), object.getId(), groupFolder.getId());

    assert getTropixObjectDao().getOwner(childObject.getId()).equals(user2);
    assert !object.getPermissionParents().contains(user1.getHomeFolder());
    assert object.getPermissionParents().contains(groupFolder);

    assert getSecurityProvider().canRead(object.getId(), user1.getCagridId());
    assert getSecurityProvider().canRead(object.getId(), user2.getCagridId());
    assert getSecurityProvider().canRead(object.getId(), user3.getCagridId());
    assert object.getParentFolder().equals(groupFolder);

  }

  @Test(expectedExceptions = RuntimeException.class)
  public void cannotMoveGroupToHome() {
    final TropixObject object = saveToParent(new TropixObject(), groupFolder, user1);
    tropixObjectService.move(user1.getCagridId(), object.getId(), user1.getHomeFolder().getId());
  }

}
