package edu.umn.msi.tropix.ssh;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.mina.core.session.IoSession;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.productivity.java.syslog4j.SyslogIF;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class FailedAttemptLoggerImplTest {
  private Capture<String> loggedMessage;

  @BeforeMethod(groups = "unit")
  public void init() {
    loggedMessage = EasyMockUtils.newCapture();
  }

  @Test(groups = "unit")
  public void testNullLogger() throws UnknownHostException {
    loginAttempt(null);
  }

  private void loginAttempt(final SyslogIF logger) {
    final FailedAttemptLoggerImpl attemptLogger = new FailedAttemptLoggerImpl(logger);
    attemptLogger.setType("KEYBOARD");
    attemptLogger.logFailedAttempt("moo", "cow", getIoSession());
  }

  private void loginAttempt() {
    loginAttempt(getLogger());
  }

  @Test(groups = "unit")
  public void testObtainsIpAddress() {
    loginAttempt();
    assert loggedMessage.getValue().contains("192.168.0.100");
  }

  @Test(groups = "unit")
  public void testMessage() {
    loginAttempt();
    assert loggedMessage.getValue().matches("sshd\\[\\d+\\]: Failed keyboard-iteractive/pam for invalid user .+ from .+ port \\d+ ssh2");
  }

  private SyslogIF getLogger() {
    SyslogIF instance = EasyMock.createMock(SyslogIF.class);
    instance.info(EasyMock.capture(loggedMessage));
    EasyMock.replay(instance);
    return instance;
  }

  private IoSession getIoSession() {
    final IoSession ioSession = EasyMock.createMock(IoSession.class);
    InetAddress address;
    try {
      address = InetAddress.getByName("192.168.0.100");
    } catch(UnknownHostException e) {
      throw new IllegalStateException(e);
    }

    InetSocketAddress sockAddress = new InetSocketAddress(address, 8021);
    EasyMock.expect(ioSession.getRemoteAddress()).andStubReturn(sockAddress);
    EasyMock.replay(ioSession);
    return ioSession;
  }

}
