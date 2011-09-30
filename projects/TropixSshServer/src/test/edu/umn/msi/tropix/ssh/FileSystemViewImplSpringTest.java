package edu.umn.msi.tropix.ssh;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.easymock.classextension.EasyMock;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.UserService;

@ContextConfiguration(locations = "test-context.xml")
public class FileSystemViewImplSpringTest extends FreshConfigTest {

  @Inject
  private FolderService folderService;

  @Inject
  private UserService userService;

  @Inject
  private TropixObjectService tropixObjectService;

  @Inject
  private FileSystemFactory fileSystemFactory;

  //@Test(groups = "spring")
  public void test() throws IOException {
    final User user = userService.createOrGetUser("testuser1");
    final Folder subFolder = new Folder();
    subFolder.setName("Test");

    folderService.createFolder(user.getCagridId(), user.getHomeFolder().getId(), subFolder);
    final Session session = EasyMock.createMock(Session.class);
    EasyMock.expect(session.getUsername()).andStubReturn("testuser1");
    EasyMock.replay(session);
    final FileSystemView view = fileSystemFactory.createFileSystemView(session);
    SshFile sshFile = view.getFile("/Test");
    assert sshFile.doesExist();
    assert sshFile.isDirectory();

    final TropixFile tropixFile1 = new TropixFile();
    tropixFile1.setName("test.mgf");
    tropixFile1.setCommitted(true);
    tropixObjectService.createFile(user.getCagridId(), subFolder.getId(), tropixFile1, null);
    sshFile = view.getFile("/Test/test.mgf");
    assert !sshFile.isDirectory();

    sshFile = view.getFile("/Test2");
    sshFile.mkdir();
    
    final TropixObject[] objects = tropixObjectService.getChildren(user.getCagridId(), user.getHomeFolder().getId());
    TropixObject createdFolder = null;
    for(final TropixObject object : objects) {
      if(object.getName().equals("Test2")) {
        createdFolder = object;
        break;
      }
    }
    assert createdFolder != null;
    
    sshFile = view.getFile("/Test2/test2");
    final OutputStream outputStream = sshFile.createOutputStream(0);
    outputStream.write("This is a test".getBytes());
    outputStream.close();
    
    assert sshFile.isFile();
    Assert.assertEquals("This is a test", InputContexts.toString(InputContexts.forInputStream(sshFile.createInputStream(0))));    

    final TropixObject[] test2Children = tropixObjectService.getChildren(user.getCagridId(), createdFolder.getId());
    final TropixObject test2File = test2Children[0];
    assert test2File.getName().equals("test2");
    assert test2File instanceof TropixFile;

  }

}

