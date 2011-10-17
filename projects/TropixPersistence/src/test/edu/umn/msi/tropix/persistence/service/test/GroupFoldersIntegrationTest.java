package edu.umn.msi.tropix.persistence.service.test;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.ProviderService;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

public class GroupFoldersIntegrationTest extends ServiceTest {

  @Inject
  private ProviderService providerService;
  
  @Inject
  private FolderService folderService;
  
  @Inject
  private SecurityProvider securityProvider;
    
  @Test
  public void testSecurityBehavior() {
    final User adminUser = createTempUser(), ownerUser = createTempUser(), thirdUser = createTempUser(), fourthUser = createTempUser();
    final Group group = createTempGroup(thirdUser);

    final Folder folder = new Folder();
    folder.setCommitted(true);
    providerService.createNewProviderAndFolder(adminUser.getCagridId(), folder, ownerUser.getCagridId());
    assert !securityProvider.canModify(folder.getId(), thirdUser.getCagridId());
    providerService.addGroupToObjectsProvider(adminUser.getCagridId(), folder.getId(), group.getId());
    assert securityProvider.canModify(folder.getId(), ownerUser.getCagridId());
    assert securityProvider.canModify(folder.getId(), thirdUser.getCagridId());
    assert !securityProvider.canModify(folder.getId(), fourthUser.getCagridId());
    
    Assert.assertEquals(folderService.getGroupFolders(thirdUser.getCagridId()).length, 1);
    Assert.assertEquals(folderService.getGroupFolders(fourthUser.getCagridId()).length, 0);    
  }
  
}
