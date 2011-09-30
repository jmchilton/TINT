package edu.umn.msi.tropix.ssh;

import org.apache.sshd.server.SshFile;

public interface SshFileFactory {
  SshFile getFile(final String identity, final String virtualPath);
}
