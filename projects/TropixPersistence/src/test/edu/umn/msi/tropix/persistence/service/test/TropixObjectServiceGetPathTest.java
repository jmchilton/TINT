package edu.umn.msi.tropix.persistence.service.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixObjectServiceGetPathTest extends ServiceTest {
  @Autowired
  private TropixObjectService tropixObjectService;

  private User owner;
  
  @BeforeMethod
  public void init() {
    owner = createTempUser();
    // Create second root folder to make sure, not just go with only one
    createTempUser();
  }
  
  @Test
  public void getForPathRoot() {
    assertPathLeadsTo(owner.getHomeFolder());
  }

  @Test
  public void getForPath() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", owner.getHomeFolder(), owner);
    assertPathLeadsTo(file, "file");
  }
  
  @Test
  public void getForPathNonFolderParent() {
    final Database database = saveWithNameToParent(new Database(), "database", owner.getHomeFolder(), owner);
    final TropixFile file = saveWithName(new TropixFile(), "file", owner);
    database.setDatabaseFile(file);
    getTropixObjectDao().addPermissionParent(file.getId(), database.getId());
    assertPathLeadsTo(file, "database", "file");
  }

  @Test
  public void getMultipleNames() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    final TropixFile fileDup = saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    assertPathLeadsTo(file, "subFolder1", String.format("file [id:%s]", file.getId()));
  }
  
  

  @Test
  public void getForPathSubFolder() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    assertPathLeadsTo(file, "subFolder1", "file");
  }

  @Test
  public void getForPathMultipleSubFolder() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final Folder subFolder2 = saveWithNameToParent(newFolder(), "subFolder2", subFolder1, owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder2, owner);
    assertPathLeadsTo(file, "subFolder1", "subFolder2", "file");
  }
  
  @Test
  public void getRootPath() {
    // Create some files to muddle the picture
    saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final Folder subFolder2 = saveWithNameToParent(newFolder(), "subFolder2", owner.getHomeFolder(), owner);
    saveWithNameToParent(new TropixFile(), "file", subFolder2, owner);
    assertPathLeadsTo(owner.getHomeFolder());
  }
    
  private void assertPathIsEmpty(final String... pathParts) {
    Assert.assertNull(tropixObjectService.getPath(owner.getCagridId(), pathParts));
  }
  
  private void assertPathLeadsTo(final TropixObject object, final String... pathParts) {
    Assert.assertEquals(tropixObjectService.getPath(owner.getCagridId(), pathParts).getId(), object.getId());
  }
  
  @Test
  public void testDeletedFilesHidden() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", owner.getHomeFolder(), owner);
    tropixObjectService.delete(owner.getCagridId(), file.getId());
    assertPathIsEmpty("file");
  }

  @Test
  public void testDeletedSubpathsHidden() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    tropixObjectService.delete(owner.getCagridId(), subFolder1.getId());
    assertPathIsEmpty("subFolder1", "file");
  }

  @Test
  public void testUncommittedFilesHidden() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", owner.getHomeFolder(), owner);
    file.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(file);
    assertPathIsEmpty("file");
  }
  
  @Test
  public void testUncommittedSubpathsHidden() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    subFolder1.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(subFolder1);
    assertPathIsEmpty("subFolder1", "file");
  }

}
