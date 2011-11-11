package edu.umn.msi.tropix.ssh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.io.FileContext;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.files.creator.TropixFileCreator;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.core.StorageManager;

public class SshFileFactoryImplTest {
  private static final Object[] HOME_DIR_REPRESENTATIONS = new Object[] {".", "/My Home/", "/My Home", "../My Home"};
  private static final Object[] ROOT_DIR_REPRESENTATIONS = new Object[] {"/", "..", "../.."};

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
  private Credential credential;

  @BeforeMethod(groups = "unit")
  public void init() {
    tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    tropixFileCreator = EasyMock.createMock(TropixFileCreator.class);
    storageManager = EasyMock.createMock(StorageManager.class);
    folderService = EasyMock.createMock(FolderService.class);
    sshFileFactoryImpl = new SshFileFactoryImpl(tropixObjectService, tropixFileCreator, storageManager, folderService);
    id = UUID.randomUUID().toString();
    credential = Credentials.getMock(id);
    fileId = UUID.randomUUID().toString();
    path = "test/path";

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
    path = "/My Home/test/path";
    backingObject = new Folder();
    expectGetPath(new String[] {"test", "path"});
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
    assert sshFile.getAbsolutePath().equals("/My Home/test/path");
  }

  @Test(groups = "unit")
  public void testGetPathAbsolute() {
    path = "/My Home/test/path";
    replayAndSetFile();
    Assert.assertEquals(sshFile.getAbsolutePath(), "/My Home/test/path");
  }

  @Test(groups = "unit")
  public void testEmptyObjectNotRemovable() {
    backingObject = null;
    expectGetPath();
    assert !removable();
  }

  @Test(groups = "unit")
  public void testOwnedObjectRemovable() {
    backingObject = new Folder();
    expectGetPath();
    assert removable();
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
  public void testCanDeleteOwnedObject() {
    backingObject = new TropixObject();
    backingObject.setId(UUID.randomUUID().toString());
    expectGetPath();
    tropixObjectService.delete(expectId(), EasyMock.eq(backingObject.getId()));
    assert delete();
    EasyMock.verify(tropixObjectService);
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
  public void testCanTruncateNonExistentFile() throws IOException {
    backingObject = null;
    expectGetPath();
    replayAndSetFile();
    sshFile.truncate();
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testCannotTruncateExistingFile() throws IOException {
    backingObject = new TropixFile();
    expectGetPath();
    replayAndSetFile();
    sshFile.truncate();
  }

  @Test(groups = "unit")
  public void testOutputStream() throws IOException {
    expectDirectoryWithPath(null);
    path = "test/path/file-name";
    final Capture<TropixFile> fileCapture = EasyMockUtils.newCapture();
    EasyMock.expect(
        tropixFileCreator.createFile(EasyMock.same(credential), EasyMock.eq(folderId), EasyMock.capture(fileCapture), EasyMock.<String>isNull()))
        .andReturn(null);
    final Capture<String> fileIdCapture = EasyMockUtils.newCapture();
    final ByteArrayOutputStream underlyingOutputStream = new ByteArrayOutputStream();
    EasyMock.expect(storageManager.prepareUploadStream(EasyMock.capture(fileIdCapture), expectId())).andReturn(underlyingOutputStream);
    replayAndSetFile();
    final OutputStream outputStream = sshFile.createOutputStream(0);
    outputStream.write("Test".getBytes());
    outputStream.close();

    assert new String(underlyingOutputStream.toByteArray()).equals("Test");
    assert fileCapture.getValue().getName().equals("file-name");
    assert fileCapture.getValue().getFileId().equals(fileIdCapture.getValue());
    assert fileCapture.getValue().getCommitted();
  }

  @Test(groups = "unit")
  public void testList() {
    expectDirectoryWithPath(null);
    final TropixObject object1 = objectWithName("name1"), object2 = objectWithName("name2"), object3 = objectWithName("name3");
    final TropixObject objectWithDuplicateName = objectWithName("name2");
    EasyMock.expect(tropixObjectService.getChildren(id, folderId)).andReturn(new TropixObject[] {object1, object2, object3, objectWithDuplicateName});
    final List<SshFile> children = list();
    Assert.assertEquals(children.size(), 4);
    final Set<String> uniqueNames = Sets.newHashSet();
    for(SshFile child : children) {
      uniqueNames.add(child.getName());
    }
    // assert uniqueNames.equals(Sets.newHashSet("name1", "name2", "name3"));
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testListRoot(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    final Set<String> names = listAndGetNames();
    assert names.contains("My Home");
  }

  @Test(groups = "unit")
  public void testValidMkdir() {
    expectDirectoryWithPath(null);
    path = "test/path/subdir";
    final Capture<Folder> newFolder = EasyMockUtils.newCapture();
    EasyMock.expect(folderService.createFolder(expectId(), EasyMock.eq(folderId), EasyMock.capture(newFolder))).andReturn(new Folder());
    replayAndSetFile();
    assert sshFile.mkdir();
    assert newFolder.getValue().getName().equals("subdir");
  }

  @Test(groups = "unit")
  public void testSomePathOutsideAValidRootDoesNotExist() {
    path = "/test";
    assert !exists();
  }

  @Test(groups = "unit")
  public void testCanMovePlainFile() {
    final TropixFile test = new TropixFile();
    test.setId(UUID.randomUUID().toString());
    expectGetPath(test, new String[] {"test"});
    expectGetPath(null, new String[] {"test2"});
    Folder folder = new Folder();
    folder.setId(UUID.randomUUID().toString());
    expectGetPath(folder, new String[] {});
    tropixObjectService.move(expectId(), EasyMock.eq(test.getId()), EasyMock.eq(folder.getId()));
    expectRename(test, "test2");
    replay();
    assert getFile("/My Home/test").move(getFile("/My Home/test2"));
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeCannotBeMoved(final String homeDirectoryPath) {
    replay();
    assert !getFile("/My Home").move(getFile("/My Home/test2"));
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeGetName(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    assert getName().equals("My Home");
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeGetAbsolutePath(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    assert getAbsolutePath().equals("/My Home");
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeExists(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    assert exists();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeReadable(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    replayAndSetFile();
    assert sshFile.isReadable();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeExecutable(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    replayAndSetFile();
    assert sshFile.isExecutable();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeIsDirectory(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    replayAndSetFile();
    assert sshFile.isDirectory();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeIsNotFile(final String homeDirectoryPath) {
    setPathToMyHomeAndExpectGet(homeDirectoryPath);
    assert !sshFile.isFile();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testMyHomeNotRemovable(String homePath) {
    setPathToMyHomeAndExpectGet(homePath);
    assert !removable();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testMyHomeGetSize(String homePath) {
    setPathToMyHomeAndExpectGet(homePath);
    replayAndSetFile();
    assert sshFile.getSize() == 0;
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths", expectedExceptions = IllegalStateException.class)
  public void testHomeCannotBeTruncated(String homePath) throws IOException {
    setPathToMyHomeAndExpectGet(homePath);
    replayAndSetFile();
    sshFile.truncate();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeCannotBeDeleted(String homePath) throws IOException {
    setPathToMyHomeAndExpectGet(homePath);
    assert !delete();
  }

  @Test(groups = "unit", dataProvider = "homeDirectoryPaths")
  public void testHomeCannotBeMkdired(final String homePath) throws IOException {
    setPathToMyHomeAndExpectGet(homePath);
    assert !mkdir();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootCannotBeMoved(final String rootDirectoryPath) {
    replay();
    assert !getFile(rootDirectoryPath).move(getFile("/My Home/test2"));
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootGetName(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    assert getName().equals("");
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootReadable(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    assert sshFile.isReadable();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootIsExecutable(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    assert sshFile.isReadable();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootIsNotWritable(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    assert !sshFile.isWritable();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootNotRemovable(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    assert !removable();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootIsNotFile(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    assert !sshFile.isFile();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootExists(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    assert exists();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootGetAbsolutePath(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    assert getAbsolutePath().equals("/");
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootIsDirectory(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    assert sshFile.isDirectory();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootGetSize(final String rootDirectoryPath) {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    assert sshFile.getSize() == 0;
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths", expectedExceptions = IllegalStateException.class)
  public void testRootCannotBeTruncated(final String rootDirectoryPath) throws IOException {
    setPathToRoot(rootDirectoryPath);
    replayAndSetFile();
    sshFile.truncate();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootCannotBeDeleted(final String rootDirectoryPath) throws IOException {
    setPathToRoot(rootDirectoryPath);
    assert !delete();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testRootCannotBeMkdired(final String rootDirectoryPath) throws IOException {
    setPathToRoot(rootDirectoryPath);
    assert !mkdir();
  }

  @Test(groups = "unit", dataProvider = "rootDirectoryPaths")
  public void testCannotBeMkdirBeneathRoot(final String rootDirectoryPath) throws IOException {
    setPathToRoot(rootDirectoryPath + "/cow");
    assert !mkdir();
  }

  private boolean mkdir() {
    replayAndSetFile();
    return sshFile.mkdir();
  }

  private TropixObject objectWithName(final String name) {
    final TropixObject object = new TropixObject();
    object.setCommitted(true);
    object.setName(name);
    return object;
  }

  @DataProvider(name = "homeDirectoryPaths")
  public Object[][] getHomeDirectoryPaths() {
    return expandArray(HOME_DIR_REPRESENTATIONS);
  }

  @DataProvider(name = "rootDirectoryPaths")
  public Object[][] getRootDirectoryPaths() {
    return expandArray(ROOT_DIR_REPRESENTATIONS);
  }

  private Object[][] expandArray(final Object[] array) {
    final Object[][] expandedArray = new Object[array.length][];
    for(int i = 0; i < array.length; i++) {
      expandedArray[i] = new Object[] {array[i]};
    }
    return expandedArray;
  }

  private boolean delete() {
    replayAndSetFile();
    boolean deleted = sshFile.delete();
    return deleted;
  }

  private List<SshFile> list() {
    replayAndSetFile();
    final List<SshFile> children = sshFile.listSshFiles();
    return children;
  }

  private Set<String> listAndGetNames() {
    final Set<String> names = Sets.newHashSet();
    final List<SshFile> children = list();
    for(final SshFile file : children) {
      names.add(file.getName());
    }
    return names;
  }

  private boolean removable() {
    replayAndSetFile();
    return sshFile.isRemovable();
  }

  private String getName() {
    replayAndSetFile();
    return sshFile.getName();
  }

  private String getAbsolutePath() {
    replayAndSetFile();
    return sshFile.getAbsolutePath();
  }

  private void setPathToMyHomeAndExpectGet(final String myHomePath) {
    path = myHomePath;
    backingObject = new Folder();
    expectDirectoryWithPath(new String[0]);
  }

  private boolean exists() {
    replayAndSetFile();
    return sshFile.doesExist();
  }

  private void setPathToRoot(String rootDirectoryPath) {
    path = rootDirectoryPath;
  }

  private void expectDirectoryWithPath(final String[] pathPieces) {
    final Folder folder = new Folder();
    folderId = UUID.randomUUID().toString();
    folder.setId(folderId);
    // backingObject = folder;
    expectGetPath(folder, pathPieces);
  }

  private void expectGetPath(final TropixObject tropixObject) {
    expectGetPath(tropixObject, null);
  }

  private void expectGetPath(final TropixObject tropixObject, final String[] pathArray) {
    EasyMock.expect(tropixObjectService.getPath(expectId(), pathArray == null ? expectPathArray() : EasyMock.aryEq(pathArray))).andStubReturn(
        tropixObject);
  }

  private void expectGetPath(final String[] pathPieces) {
    expectGetPath(backingObject, pathPieces);
  }

  private void expectGetPath() {
    expectGetPath(backingObject);
  }

  private void replayAndSetFile() {
    replay();
    sshFile = getFile(path);
  }

  private void replay() {
    EasyMockUtils.replayAll(tropixObjectService, storageManager, folderService, tropixFileCreator);
  }

  private SshFile getFile(final String path) {
    return sshFileFactoryImpl.getFile(credential, path);
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
    String localPath = path;
    if(path.startsWith("/")) {
      localPath = path.substring(1);
    }
    final String[] pathPieces = localPath.split("/");
    return EasyMock.aryEq(pathPieces);
  }

  private String expectId() {
    return EasyMock.eq(id);
  }

  private void expectRename(final TropixObject object, final String to) {
    final TropixObject loadedObject = new TropixObject();
    EasyMock.expect(tropixObjectService.load(expectId(), EasyMock.eq(object.getId()))).andReturn(loadedObject);
    tropixObjectService.update(expectId(),
        EasyMockUtils.<TropixObject>isBeanWithProperties(ImmutableMap.<String, Object>builder().put("name", to).build()));
  }

}
