package edu.umn.msi.tropix.ssh;

import java.io.IOException;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.Session;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

public class ApiTest {

  public static final void main(final String[] args) throws IOException {
    final SshServer sshd = SshServer.setUpDefaultServer();
    sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

      public boolean authenticate(final String username, final String password, final ServerSession session) {
        return username.equals("moo") && password.equals("cow");
      }

    });
    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
    sshd.setPort(3002);
    //sshd.setCommandFactory(new SftpCommandFactory());
    
    sshd.setFileSystemFactory(new FileSystemFactory() {
      public FileSystemView createFileSystemView(final Session session) {
        return null;
      }
    });
    sshd.start();
  }

}
