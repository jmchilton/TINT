package edu.umn.msi.tropix.ssh;

import org.apache.sshd.server.session.ServerSession;

public interface AttemptLogger {
  void loginAttempt(final String username, final String password, final ServerSession severSession);
}
