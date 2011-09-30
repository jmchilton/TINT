package edu.umn.msi.tropix.webgui.server.session;

import java.util.UUID;

import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class TestClosure1 implements SessionInfoClosure {
  public static final String TEST_ID = UUID.randomUUID().toString();

  public void apply(final SessionInfo input) {
    input.setSessionId(TEST_ID);
  }
}
