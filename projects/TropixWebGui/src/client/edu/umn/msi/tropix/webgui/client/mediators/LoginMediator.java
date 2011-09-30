/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.webgui.client.mediators;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.smartgwt.client.util.SC;

import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.services.session.LoginListener;
import edu.umn.msi.tropix.webgui.services.session.LoginService;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class LoginMediator implements LoginListener {
  private final List<LoginListener> listeners = new ArrayList<LoginListener>();
  private Session session;

  @Inject
  public void setSession(final Session session) {
    this.session = session;
  }

  public void addListener(final LoginListener listener) {
    this.listeners.add(listener);
  }

  public void loginEvent() {
    Log.warn("User logged in");
    for(final LoginListener listener : this.listeners) {
      listener.loginEvent();
    }
  }

  public void loginFailedEvent() {
    for(final LoginListener listener : this.listeners) {
      listener.loginFailedEvent();
    }
  }

  public void logoutEvent() {
    for(final LoginListener listener : this.listeners) {
      try {
        listener.logoutEvent();
      } catch(final Exception ignored) {
        ignored.printStackTrace();
      }
    }
  }

  protected class UserLoginCallback implements AsyncCallback<SessionInfo> {
    public UserLoginCallback() {
    }

    public void onFailure(final Throwable caught) {
      loginFailedEvent();
    }

    public void onSuccess(final SessionInfo userInfo) {
      if(userInfo != null) {
        session.init(userInfo);
        loginEvent();
      } else {
        loginFailedEvent();
      }
    }
  }

  public void loginAttempt(final String userName, final String password, final String authentication) {
    final UserLoginCallback callback = new UserLoginCallback();
    LoginService.Util.getInstance().isValidLogin(userName, password, authentication, callback);
  }

  public void guestLoginAttempt() {
    final UserLoginCallback callback = new UserLoginCallback();
    LoginService.Util.getInstance().guestLogin(callback);
  }

  public void attemptLogout() {
    LoginService.Util.getInstance().logout(new LogoutCallback());
  }

  class LogoutCallback implements AsyncCallback<Void> {

    public void onFailure(final Throwable caught) {
      SC.say("Failed to inform server of logout");
      // Server communication is broken, so should probably log out anyway.
      // -John
      logoutEvent();
    }

    public void onSuccess(final Void result) {
      logoutEvent();
    }

  }
}
