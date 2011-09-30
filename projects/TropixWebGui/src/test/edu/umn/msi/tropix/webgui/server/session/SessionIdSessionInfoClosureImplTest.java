package edu.umn.msi.tropix.webgui.server.session;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class SessionIdSessionInfoClosureImplTest extends BaseSessionInfoClosureImplTest {

  @Test(groups = "unit")
  public void testSetSessionId() {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpSession session = new MockHttpSession();
    assert session.getId() != null;
    request.setSession(session);
    org.gwtwidgets.server.spring.ServletUtils.setRequest(request);

    final SessionIdSessionInfoClosureImpl closure = new SessionIdSessionInfoClosureImpl();
    final SessionInfo sessionInfo = get(closure);

    assert sessionInfo.getSessionId().equals(session.getId());
    org.gwtwidgets.server.spring.ServletUtils.setRequest(null); // Reset this
  }
}
