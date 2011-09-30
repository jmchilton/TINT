package edu.umn.msi.tropix.ssh;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import javax.inject.Inject;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.DatabaseService;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.UserService;
import edu.umn.msi.tropix.storage.core.StorageManager;

public class TestDataCreator {
  public static final String TO_MOVE_FILE_NAME = "tomove";
  public static final String DATABASE_FILE_NAME = "db.fasta";
  public static final String DATABASE_NAME = "HUMAN";

  @Inject
  private DatabaseService databaseService;
  
  @Inject
  private TropixObjectService tropixObjectService;
  
  @Inject
  private FolderService folderService;
  
  @Inject
  private UserService userService;
  
  @Inject
  private StorageManager storageManager;

  public void create() {
    userService.ensureUserExists("admin");
    final Folder newFolder = new Folder();
    newFolder.setName("Test Folder");
    newFolder.setCommitted(true);
    final String homeFolderId = userService.getHomeFolder("admin").getId();
    final Folder savedNewFolder = folderService.createFolder("admin", homeFolderId, newFolder);
    
    createFile(TO_MOVE_FILE_NAME, "contents");
    createFile("Test File", "Hello World1");
    createFile("Test File", "Hello World2");    
    createFile("Subfile", "Sub Hello World", savedNewFolder.getId());
    final TropixFile databaseFile = createFile(DATABASE_FILE_NAME, "amino acids", null);
    
    final Database database = new Database();
    database.setName(DATABASE_NAME);
    database.setType("FASTA");
    database.setCommitted(true);
    databaseService.createDatabase("admin", homeFolderId, database, databaseFile.getId());    
    
  }

  protected void createFile(final String name, final String contents) {
    createFile(name, contents, userService.getHomeFolder("admin").getId());
  }
  
  protected TropixFile createFile(final String name, final String contents, final String parentFolderId) {
    final String id1 = UUID.randomUUID().toString();
    final TropixFile file1 = new TropixFile();
    file1.setName(name);
    file1.setCommitted(true);
    file1.setFileId(id1);
    final TropixFile tropixFile = tropixObjectService.createFile("admin", parentFolderId, file1, null);
    storageManager.upload(id1, "admin").onUpload(new ByteArrayInputStream(contents.getBytes()));
    return tropixFile;
  }

}
