package edu.umn.msi.tropix.ssh;

import java.net.InetSocketAddress;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.productivity.java.syslog4j.SyslogIF;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Supplier;

@ManagedBean
public class FailedAttemptLoggerImpl implements FailedAttemptLogger {
  private static final Log LOG = LogFactory.getLog(FailedAttemptLoggerImpl.class);
  private final SyslogIF syslog;
  private Type type = Type.PASSWORD;

  private enum Type {
    KEYBOARD("sshd[12345]: Failed keyboard-iteractive/pam for invalid user %s from %s port %d ssh2"),
    PASSWORD("sshd[12345]: Failed password for %s from %s port %d ssh2");

    private String format;

    private Type(final String format) {
      this.format = format;
    }
  }

  public void setType(@Value("${ssh.log.format}") final String logType) {
    this.type = Type.valueOf(logType);
  }

  @Inject
  public FailedAttemptLoggerImpl(@Named("sshSyslogSupplier") final Supplier<SyslogIF> syslogSupplier) {
    this(syslogSupplier.get());
  }

  public FailedAttemptLoggerImpl(@Nullable final SyslogIF syslog) {
    this.syslog = syslog;
  }

  public void logFailedAttempt(final String username,
      final String password,
      final IoSession ioSession) {
    LOG.debug("Logging failed auth attempt to " + syslog);
    if(syslog != null) {
      syslog.info(logMessage(username, ioSession));
    }
  }

  private String getIpAddress(final IoSession ioSession) {
    InetSocketAddress socketAddress = (InetSocketAddress) ioSession.getRemoteAddress();
    return socketAddress.getAddress().getHostAddress();
  }

  private int getRemoteReport(final IoSession ioSession) {
    InetSocketAddress socketAddress = (InetSocketAddress) ioSession.getRemoteAddress();
    return socketAddress.getPort();

  }

  private String logMessage(final String username, final IoSession ioSession) {
    final String message = String.format(type.format, username, getIpAddress(ioSession), getRemoteReport(ioSession));
    return message;
  }

}
