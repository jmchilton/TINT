package edu.umn.msi.tropix.ssh;

import org.apache.sshd.server.SshFile;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

public class FileSystemFactoryImplTest {

  private SshFileFactory fileFactory;
  private SshFile sshFile;

  @BeforeMethod(groups = "unit")
  public void init() {
    fileFactory = EasyMock.createMock(SshFileFactory.class);
    sshFile = EasyMock.createMock(SshFile.class);
    EasyMock.expect(fileFactory.getFile("moo", "/test")).andStubReturn(sshFile);
    EasyMock.replay(fileFactory);
  }

  @Test(groups = "unit")
  public void testDelegationToSshFileFactory() {
    final FileSystemFactoryImpl factory = new FileSystemFactoryImpl(fileFactory);
    assert factory.createFileSystemView("moo").getFile("/test") == sshFile;
  }

  @Test(groups = "unit")
  public void testMapper() {
    final FileSystemFactoryImpl factory = new FileSystemFactoryImpl(fileFactory);
    factory.setUserNameToIdentityFunction(Functions.<String, String>forMap(ImmutableMap.<String, String>builder().put("cow", "moo").build()));
    assert factory.createFileSystemView("cow").getFile("/test") == sshFile;
  }

}
