package edu.umn.msi.tropix.ssh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.sshd.server.SshFile;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.io.FileContext;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.files.TropixFileCreator;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;

public class SshFileFactoryImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private SshFileFactoryImpl sshFileFactoryImpl;
  private TropixFileCreator tropixFileCreator;
  private TropixObjectService tropixObjectService;
  private StorageManager storageManager;
  private FolderService folderService;
  private String id;
  private String fileId;
  private String folderId;
  private String path;
  private SshFile sshFile;
  private TropixObject backingObject;
  private File tempFile;

  @BeforeMethod(groups = "unit")
  public void init() {
    tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    tropixFileCreator = EasyMock.createMock(TropixFileCreator.class);
    storageManager = EasyMock.createMock(StorageManager.class);
    folderService = EasyMock.createMock(FolderService.class);
    sshFileFactoryImpl = new SshFileFactoryImpl(tropixObjectService, tropixFileCreator, storageManager, folderService);
    id = UUID.randomUUID().toString();
    fileId = UUID.randomUUID().toString();
    path = "/test/path";

    folderId = null;
    backingObject = null;
    tempFile = null;
  }

  @AfterMethod(groups = "unit")
  public void teardown() {
    FILE_UTILS.deleteQuietly(tempFile);
  }

  @Test(groups = "unit")
  public void testFileExists() {
    backingObject = new TropixObject();
    expectGetPath();
    replayAndSetFile();
    assert sshFile.doesExist();
  }

  @Test(groups = "unit")
  public void testFileExistsDoesntExist() {
    backingObject = null;
    expectGetPath();
    replayAndSetFile();
    assert !sshFile.doesExist();
  }

  @Test(groups = "unit")
  public void testIsDirectory() {
    path = "/test/path";
    backingObject = new Folder();
    expectGetPath();
    replayAndSetFile();
    assert sshFile.isDirectory();
  }

  @Test(groups = "unit")
  public void testName() {
    replayAndSetFile();
    assert sshFile.getName().equals("path");
  }

  @Test(groups = "unit")
  public void testGetPath() {
    replayAndSetFile();
    assert sshFile.getAbsolutePath().equals("/test/path");
  }

  @Test(groups = "unit")
  public void testRemovable() {
    // For now, everything should just return false
    replayAndSetFile();
    assert sshFile.isRemovable();
  }

  @Test(groups = "unit")
  public void testGetParentOfRoot() { // API says should just return root again
    path = "/";
    replayAndSetFile();
    assert sshFile.getParentFile().getAbsolutePath().equals("/");
  }

  @Test(groups = "unit")
  public void testGetParent() {
    replayAndSetFile();
    assert !sshFile.getParentFile().getAbsolutePath().equals("/test");
  }

  @Test(groups = "unit")
  public void testCannotUpdateLastModified() {
    replayAndSetFile();
    assert !sshFile.setLastModified(100000L);
  }

  @Test(groups = "unit")
  public void testCannotDelete() {
    backingObject = new TropixObject();
    backingObject.setId(UUID.randomUUID().toString());
    expectGetPath();
    tropixObjectService.delete(expectId(), EasyMock.eq(backingObject.getId()));
    replayAndSetFile();
    assert sshFile.delete();
    EasyMock.verify(tropixObjectService);
  }

  @Test(groups = "unit")
  public void testCannotMove() {
    replayAndSetFile();
    assert !sshFile.move(null);
  }

  @Test(groups = "unit")
  public void testReadable() {
    // Everything in your directory structure should be readable
    replayAndSetFile();
    assert sshFile.isReadable();
  }
  
  @Test(groups = "unit")
  public void testExecutable() {
    // Nothing should be executable?
    replayAndSetFile();
    assert sshFile.isExecutable();
  }

  @Test(groups = "unit")
  public void testDirectoriesAreWritable() {
    backingObject = new Folder();
    expectGetPath();
    replayAndSetFile();
    assert sshFile.isWritable();
  }


  @Test(groups = "unit")
  public void testSize() {
    EasyMock.expect(storageManager.getLength(expectFileId(), expectId())).andReturn(13L);
    final byte[] testBytes = "test contents".getBytes();
    replayAndSetFileWithContents(testBytes);
    assert sshFile.getSize() == 13L;
  }

  @Test(groups = "unit")
  public void testLastModified() {
    EasyMock.expect(storageManager.getDateModified(expectFileId(), expectId())).andReturn(13L);
    replayAndSetFileWithContents("test contents".getBytes());
    assert sshFile.getLastModified() == 13L;
  }
  
  @Test(groups = "unit")
  public void testInputStream() throws IOException {
    replayAndSetFileWithContents("test contents".getBytes());
    assert InputContexts.toString(InputContexts.forInputStream(sshFile.createInputStream(0))).equals("test contents");
  }
  
  @Test(groups = "unit")
  public void testInputStreamWithOffset() throws IOException {
    replayAndSetFileWithContents("test contents".getBytes());
    assert InputContexts.toString(InputContexts.forInputStream(sshFile.createInputStream(1))).equals("est contents");
  }
  
  @Test(groups = "unit")
  public void testOutputStream() throws IOException {
    expectDirectoryWithPath(path);
    path = "/test/path/file-name";
    final Capture<TropixFile> fileCapture = EasyMockUtils.newCapture();
    EasyMock.expect(tropixFileCreator.createFile(expectId(), EasyMock.eq(folderId), EasyMock.capture(fileCapture), EasyMock.<String>isNull())).andReturn(null);
    UploadCallback callback = new UploadCallback() {
      private String contents;
      
      public void onUpload(final InputStream inputStream) {
        contents = InputContexts.toString(InputContexts.forInputStream(inputStream));
      }
      
      public String toString() {
        return contents;
      }
      
    };
    final Capture<String> fileIdCapture = EasyMockUtils.newCapture();
    EasyMock.expect(storageManager.upload(EasyMock.capture(fileIdCapture), expectId())).andReturn(callback);
    replayAndSetFile();
    final OutputStream outputStream = sshFile.createOutputStream(0);
    outputStream.write("Test".getBytes());
    outputStream.close();
    EasyMock.verify(storageManager);
    assert callback.toString().equals("Test");
    assert fileCapture.getValue().getName().equals("file-name");
    assert fileCapture.getValue().getFileId().equals(fileIdCapture.getValue());
    assert fileCapture.getValue().getCommitted();
  }
  
  @Test(groups = "unit")
  public void testList() {
    expectDirectoryWithPath(path);
    final TropixObject object1 = objectWithName("name1"), object2 = objectWithName("name2"), object3 = objectWithName("name3");
    final TropixObject objectWithDuplicateName = objectWithName("name2");
    EasyMock.expect(tropixObjectService.getChildren(id, folderId)).andReturn(new TropixObject[] {object1, object2, object3, objectWithDuplicateName});
    replayAndSetFile();
    final List<SshFile> children = sshFile.listSshFiles();
    Assert.assertEquals(children.size(), 4);
    final Set<String> uniqueNames = Sets.newHashSet();
    for(SshFile child : children) {
      uniqueNames.add(child.getName());
    }
    //assert uniqueNames.equals(Sets.newHashSet("name1", "name2", "name3"));
  }
  
  private TropixObject objectWithName(final String name) {
    final TropixObject object = new TropixObject();
    object.setCommitted(true);
    object.setName(name);
    return object;    
  }

  @Test(groups = "unit")
  public void testValidMkdir() {
    expectDirectoryWithPath(path);
    path = "/test/path/subdir";
    final Capture<Folder> newFolder = EasyMockUtils.newCapture();
    EasyMock.expect(folderService.createFolder(expectId(), EasyMock.eq(folderId), EasyMock.capture(newFolder))).andReturn(new Folder());
    replayAndSetFile();
    assert sshFile.mkdir();
    assert newFolder.getValue().getName().equals("subdir");
  }
  
  private void expectDirectoryWithPath(final String path) {    
    final Folder folder = new Folder();
    folderId = UUID.randomUUID().toString();
    folder.setId(folderId);
    backingObject = folder;
    expectGetPath();    
  }

  private void expectGetPath() {
    EasyMock.expect(tropixObjectService.getPath(expectId(), expectPathArray())).andStubReturn(backingObject);
  }

  private void replayAndSetFile() {
    EasyMockUtils.replayAll(tropixObjectService, storageManager, folderService, tropixFileCreator);
    sshFile = sshFileFactoryImpl.getFile(id, path);
  }
  
  private String expectFileId() {
    return EasyMock.eq(fileId);
  }

  private void replayAndSetFileWithContents(final byte[] contents) {
    tempFile = FILE_UTILS.createTempFile();
    FILE_UTILS.writeByteArrayToFile(tempFile, contents);
    EasyMock.expect(storageManager.download(fileId, id)).andStubReturn(new FileContext(tempFile));
    backingObject = new TropixFile();
    ((TropixFile) backingObject).setFileId(fileId);
    expectGetPath();
    replayAndSetFile();
  }

  private String[] expectPathArray() {
    return EasyMock.aryEq(Iterables.toArray(Utils.pathPieces(path), String.class));
  }

  private String expectId() {
    return EasyMock.eq(id);
  }

}
