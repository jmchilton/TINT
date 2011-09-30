package edu.umn.msi.tropix.ssh;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Function;
import com.google.common.base.Functions;

@ManagedBean
public class FileSystemFactoryImpl implements FileSystemFactory {
  private Function<String, String> userNameToIdentityFunction = Functions.identity();

  @Autowired(required = false)
  public void setUserNameToIdentityFunction(final Function<String, String> userNameToIdentityFunction) {
    this.userNameToIdentityFunction = userNameToIdentityFunction;
  }

  private final SshFileFactory sshFileFactory;

  @Inject
  public FileSystemFactoryImpl(final SshFileFactory sshFileFactory) {
    this.sshFileFactory = sshFileFactory;
  }

  public FileSystemView createFileSystemView(final String userName) {
    return new FileSystemViewImpl(userNameToIdentityFunction.apply(userName));
  }

  public FileSystemView createFileSystemView(final Session session) throws IOException {
    return createFileSystemView(session.getUsername());
  }
  
  private class FileSystemViewImpl implements FileSystemView {
    private final String identity;

    FileSystemViewImpl(final String identity) {
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
