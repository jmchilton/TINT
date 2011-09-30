package edu.umn.msi.tropix.webgui.server.session;

import edu.umn.msi.tropix.webgui.server.BaseGwtServiceTest;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class BaseSessionInfoClosureImplTest extends BaseGwtServiceTest {

  public BaseSessionInfoClosureImplTest() {
    super();
  }

  protected SessionInfo get(final SessionInfoClosure sessionInfoClosure) {
    final SessionInfo info = new SessionInfo();
    sessionInfoClosure.apply(info);
    return info;
  }

}