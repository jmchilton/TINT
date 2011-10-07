package edu.umn.msi.tropix.ssh;

import org.apache.sshd.server.SshFile;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class FileSystemFactoryImplTest {

  private SshFileFactory fileFactory;
  private SshFile sshFile;
  private Credential credential = Credentials.getMock("moo");

  @BeforeMethod(groups = "unit")
  public void init() {
    fileFactory = EasyMock.createMock(SshFileFactory.class);
    sshFile = EasyMock.createMock(SshFile.class);
    EasyMock.expect(fileFactory.getFile(credential, "/test")).andStubReturn(sshFile);
    EasyMock.replay(fileFactory);
  }

  @Test(groups = "unit")
  public void testDelegationToSshFileFactory() {
    final FileSystemFactoryImpl factory = new FileSystemFactoryImpl(fileFactory);
    assert factory.createFileSystemView(credential).getFile("/test") == sshFile;
  }

  @Test(groups = "unit")
  public void testMapper() {
    // final FileSystemFactoryImpl factory = new FileSystemFactoryImpl(fileFactory);
    // factory.setUserNameToIdentityFunction(Functions.<String, String>forMap(ImmutableMap.<String, String>builder().put("cow", "moo").build()));
    // assert factory.createFileSystemView("cow").getFile("/test") == sshFile;
  }

}
