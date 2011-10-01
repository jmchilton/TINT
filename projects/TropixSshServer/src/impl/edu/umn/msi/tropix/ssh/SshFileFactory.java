package edu.umn.msi.tropix.ssh;

import org.apache.sshd.server.SshFile;

import edu.umn.msi.tropix.grid.credentials.Credential;

public interface SshFileFactory {
  SshFile getFile(final Credential credential, final String virtualPath);
}
