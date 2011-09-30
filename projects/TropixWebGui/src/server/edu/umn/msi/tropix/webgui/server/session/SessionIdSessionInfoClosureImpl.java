package edu.umn.msi.tropix.webgui.server.session;

import javax.annotation.ManagedBean;

import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
public class SessionIdSessionInfoClosureImpl implements SessionInfoClosure {

  public void apply(final SessionInfo sessionInfo) {
    final String sessionId = org.gwtwidgets.server.spring.ServletUtils.getRequest().getSession().getId();
    sessionInfo.setSessionId(sessionId);
  }

}
