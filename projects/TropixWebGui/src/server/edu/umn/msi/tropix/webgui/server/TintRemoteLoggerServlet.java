package edu.umn.msi.tropix.webgui.server;

import com.allen_sauer.gwt.log.server.RemoteLoggerServlet;

public class TintRemoteLoggerServlet extends RemoteLoggerServlet {

  @Override
  protected boolean shouldReturnDeobfuscatedStackTraceToClient() {
    return false;
  }

}
