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
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixObjectServiceGetPathTest extends ServiceTest {
  private static final String TEST_GROUP_FOLDER_NAME = "grouptest";

  @Autowired
  private TropixObjectService tropixObjectService;

  private User owner;

  private Folder rootGroupFolder;

  @BeforeMethod
  public void init() {
    owner = createTempUser();
    // Create second root folder to make sure, not just go with only one
    createTempUser();
    rootGroupFolder = createTempGroupFolder(owner, TEST_GROUP_FOLDER_NAME);
  }

  @Test
  public void getForPathHome() {
    assertPathLeadsTo(owner.getHomeFolder(), Locations.MY_HOME);
  }

  @Test
  public void getForPathGroupFolderRoot() {
    assertPathLeadsTo(rootGroupFolder, Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME);
  }

  @Test
  public void getForPath() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", owner.getHomeFolder(), owner);
    assertPathLeadsTo(file, Locations.MY_HOME, "file");
  }

  @Test
  public void getForPathGroupFolder() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", rootGroupFolder, owner);
    assertPathLeadsTo(file, Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "file");
  }

  @Test
  public void getForPathNonFolderParent() {
    final Database database = saveWithNameToParent(new Database(), "database", owner.getHomeFolder(), owner);
    final TropixFile file = saveWithName(new TropixFile(), "file", owner);
    database.setDatabaseFile(file);
    getTropixObjectDao().addPermissionParent(file.getId(), database.getId());
    assertPathLeadsTo(file, Locations.MY_HOME, "database", "file");
  }

  @Test
  public void getForPathGroupFolderNonFolderParent() {
    final Database database = saveWithNameToParent(new Database(), "database", rootGroupFolder, owner);
    final TropixFile file = saveWithName(new TropixFile(), "file", owner);
    database.setDatabaseFile(file);
    getTropixObjectDao().addPermissionParent(file.getId(), database.getId());
    assertPathLeadsTo(file, Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "database", "file");
  }

  @Test
  public void getMultipleNames() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", rootGroupFolder, owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    // DUP File
    saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    assertPathLeadsTo(file, Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "subFolder1", String.format("file [id:%s]", file.getId()));
  }

  @Test
  public void getForPathSubFolder() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    assertPathLeadsTo(file, Locations.MY_HOME, "subFolder1", "file");
  }

  @Test
  public void getForPathMultipleSubFolder() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final Folder subFolder2 = saveWithNameToParent(newFolder(), "subFolder2", subFolder1, owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder2, owner);
    assertPathLeadsTo(file, Locations.MY_HOME, "subFolder1", "subFolder2", "file");
  }

  @Test
  public void getForPathMultipleGroupSubFolder() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", rootGroupFolder, owner);
    final Folder subFolder2 = saveWithNameToParent(newFolder(), "subFolder2", subFolder1, owner);
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", subFolder2, owner);
    assertPathLeadsTo(file, Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "subFolder1", "subFolder2", "file");
  }

  @Test
  public void getRootPath() {
    // Create some files to muddle the picture
    saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    final Folder subFolder2 = saveWithNameToParent(newFolder(), "subFolder2", owner.getHomeFolder(), owner);
    saveWithNameToParent(new TropixFile(), "file", subFolder2, owner);
    assertPathLeadsTo(owner.getHomeFolder(), Locations.MY_HOME);
  }

  @Test
  public void testDeletedFilesHidden() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", owner.getHomeFolder(), owner);
    assertPathNotEmpty(Locations.MY_HOME, "file");
    tropixObjectService.delete(owner.getCagridId(), file.getId());
    assertPathIsEmpty(Locations.MY_HOME, "file");
  }

  @Test
  public void testGroupDeletedFilesHidden() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", rootGroupFolder, owner);
    assertPathNotEmpty(Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "file");
    tropixObjectService.delete(owner.getCagridId(), file.getId());
    assertPathIsEmpty(Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "file");
  }

  @Test
  public void testDeletedSubpathsHidden() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    tropixObjectService.delete(owner.getCagridId(), subFolder1.getId());
    assertPathIsEmpty(Locations.MY_HOME, "subFolder1", "file");
  }

  @Test
  public void testUncommittedFilesHidden() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", owner.getHomeFolder(), owner);
    assertPathNotEmpty(Locations.MY_HOME, "file");
    uncommit(file);
    assertPathIsEmpty(Locations.MY_HOME, "file");
  }

  @Test
  public void testGroupUncommittedFilesHidden() {
    final TropixFile file = saveWithNameToParent(new TropixFile(), "file", rootGroupFolder, owner);
    assertPathNotEmpty(Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "file");
    uncommit(file);
    assertPathIsEmpty(Locations.MY_GROUP_FOLDERS, TEST_GROUP_FOLDER_NAME, "file");
  }

  @Test
  public void testUncommittedSubpathsHidden() {
    final Folder subFolder1 = saveWithNameToParent(newFolder(), "subFolder1", owner.getHomeFolder(), owner);
    saveWithNameToParent(new TropixFile(), "file", subFolder1, owner);
    subFolder1.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(subFolder1);
    assertPathIsEmpty(Locations.MY_HOME, "subFolder1", "file");
  }

  private void assertPathNotEmpty(final String... pathParts) {
    Assert.assertNotNull(tropixObjectService.getPath(owner.getCagridId(), pathParts));
  }

  private void assertPathIsEmpty(final String... pathParts) {
    Assert.assertNull(tropixObjectService.getPath(owner.getCagridId(), pathParts));
  }

  private void assertPathLeadsTo(final TropixObject object, final String... pathParts) {
    Assert.assertEquals(tropixObjectService.getPath(owner.getCagridId(), pathParts).getId(), object.getId());
  }

  private void uncommit(final TropixFile file) {
    file.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(file);
  }

}
