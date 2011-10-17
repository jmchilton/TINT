package edu.umn.msi.tropix.persistence.service.test;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.ProviderService;

public class ProviderServiceTest extends ServiceTest {

  @Inject
  private ProviderService providerService;

  private User adminUser, otherUser;

  @BeforeMethod
  public void setupProviderFolder() {
    adminUser = createTempUser();
    otherUser = createTempUser();

    final Folder folder = new Folder();
    folder.setName("Test Group Folder");
    folder.setCommitted(true);
    providerService.createNewProviderAndFolder(getAdminId(), folder, getOwnerId());
  }

  @Test
  public void testFolderOwnership() {
    assert getTropixObjectDao().getOwner(getFolderId()).getCagridId().equals(getOwnerId());
  }

  @Test
  public void testAddToGroup() {
    final User groupUser = createGroupAndAddToProvider();
    assertHasGroupFolder(groupUser);
  }

  @Test
  public void testAddToGroupPermissions() {
    final User groupUser = createGroupAndAddToProvider();
    assertCanModifyFolder(groupUser);
  }

  @Test
  public void testAddUser() {
    final User user = createUserAndAddToProvider();
    assertHasGroupFolder(user);
  }

  @Test
  public void testAddUserPermissions() {
    final User user = createUserAndAddToProvider();
    assertCanModifyFolder(user);
  }

  private void assertCanModifyFolder(final User user) {
    assert getSecurityProvider().canModify(getFolderId(), user.getCagridId());
  }

  private void assertHasGroupFolder(final User user) {
    assert loadUsersGroupFolder(user.getCagridId()) != null;
  }

  private String getFolderId() {
    return loadFolder().getId();
  }

  private User createUserAndAddToProvider() {
    final User user = createTempUser();
    providerService.addUserToObjectsProvider(getAdminId(), getFolderId(), user.getCagridId());
    return user;

  }

  private User createGroupAndAddToProvider() {
    final User groupUser = createTempUser();
    final Group group = createTempGroup(groupUser);
    providerService.addGroupToObjectsProvider(getAdminId(), getFolderId(), group.getId());
    return groupUser;
  }

  private Folder loadFolder() {
    final String ownerId = getOwnerId();
    return loadUsersGroupFolder(ownerId);
  }

  private Folder loadUsersGroupFolder(final String ownerId) {
    return Iterables.getOnlyElement(getTropixObjectDao().getGroupFolders(ownerId));
  }

  private String getOwnerId() {
    return otherUser.getCagridId();
  }

  private String getAdminId() {
    return adminUser.getCagridId();
  }

}
