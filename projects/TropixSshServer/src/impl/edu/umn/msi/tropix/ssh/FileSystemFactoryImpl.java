package edu.umn.msi.tropix.ssh;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;

import edu.umn.msi.tropix.grid.credentials.Credential;

@ManagedBean
public class FileSystemFactoryImpl implements FileSystemFactory {
  private final SshFileFactory sshFileFactory;

  @Inject
  public FileSystemFactoryImpl(final SshFileFactory sshFileFactory) {
    this.sshFileFactory = sshFileFactory;
  }

  public FileSystemView createFileSystemView(final Credential credential) {
    return new FileSystemViewImpl(credential);
  }

  public FileSystemView createFileSystemView(final Session session) throws IOException {
    return createFileSystemView((Credential) session.getAttribute(PasswordAuthenticatorImpl.CREDENTIAL_KEY));
  }

  private class FileSystemViewImpl implements FileSystemView {
    private final Credential identity;

    FileSystemViewImpl(final Credential identity) {
      this.identity = identity;
    }

    public SshFile getFile(final String file) {
      return sshFileFactory.getFile(identity, file);
    }

    public SshFile getFile(final SshFile parent, final String file) {
      final String newPath = parent.getAbsolutePath() + "/" + file;
      return getFile(newPath);
    }

  }

}
