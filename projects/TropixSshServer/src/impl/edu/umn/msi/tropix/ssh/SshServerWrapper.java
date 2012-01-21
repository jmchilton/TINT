package edu.umn.msi.tropix.ssh;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;

@ManagedBean
public class SshServerWrapper {
  public static final int DEFAULT_PORT = 8021;
  private static final Log LOG = LogFactory.getLog(SshServerWrapper.class);
  private final PasswordAuthenticator passwordAuthenticator;
  private final FileSystemFactory fileSystemFactory;
  private final boolean useCustomSftpSubsystem = true;

  private final SshServer wrappedServer;

  private static final class ScpCommandFactoryWrapper extends ScpCommandFactory {

    @Override
    public Command createCommand(final String command) {
      LOG.debug(String.format("Creating SCP command object for string [%s]", command));
      return super.createCommand(command);
    }

  }

  @Inject
  public SshServerWrapper(final PasswordAuthenticator passwordAuthenticator,
      final FileSystemFactory fileSystemFactory,
      final KeyPairProvider keyPairProvider,
      @Value("${ssh.port}") final Integer port) {
    this.passwordAuthenticator = passwordAuthenticator;
    this.fileSystemFactory = fileSystemFactory;

    wrappedServer = SshServer.setUpDefaultServer();

    final NamedFactory<Command> sftpCommand;
    if(useCustomSftpSubsystem) {
      sftpCommand = new SftpSubsystem.Factory();
    } else {
      sftpCommand = new org.apache.sshd.server.sftp.SftpSubsystem.Factory();
    }

    wrappedServer.setSubsystemFactories(Lists.<NamedFactory<Command>>newArrayList(sftpCommand));
    // final KeyPairProvider keyPair = new SimpleGeneratorHostKeyProvider();
    wrappedServer.setKeyPairProvider(keyPairProvider);
    wrappedServer.setCommandFactory(new ScpCommandFactoryWrapper());
    wrappedServer.setPort(port);
    wrappedServer.setPasswordAuthenticator(passwordAuthenticator);
    wrappedServer.setFileSystemFactory(fileSystemFactory);
  }

  public void start() {
    try {
      wrappedServer.start();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      wrappedServer.stop();
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

}
