package edu.umn.msi.tropix.ssh;

import org.apache.mina.core.session.IoSession;

public interface FailedAttemptLogger {
  void logFailedAttempt(final String username, final String password, final IoSession ioSession);
}
