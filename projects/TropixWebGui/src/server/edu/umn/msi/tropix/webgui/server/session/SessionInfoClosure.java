package edu.umn.msi.tropix.webgui.server.session;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

/**
 * Classes that implement this interface populate SessionInfo objects
 * that hold configuration information that gets sent to the GWT client.
 * 
 * @author John Chilton
 * 
 */
public interface SessionInfoClosure extends Closure<SessionInfo> {

}
