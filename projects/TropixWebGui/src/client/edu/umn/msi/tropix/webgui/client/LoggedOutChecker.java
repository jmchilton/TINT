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

package edu.umn.msi.tropix.webgui.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.client.mediators.LoginMediator;
import edu.umn.msi.tropix.webgui.services.session.LoginListener;
import edu.umn.msi.tropix.webgui.services.session.LoginService;

/**
 * An object of this class is responsible for repeatedly checking with the server and verifying the user is still logged in. If the server indicates the user is no longer logged in or the server goes down. A logout event is issued.
 * 
 */
public class LoggedOutChecker extends Timer implements LoginListener, AsyncCallback<Boolean> {
  private final LoginMediator loginMediator;
  private boolean loggedIn = false;
  private int delta = 30000;

  LoggedOutChecker(final LoginMediator loginMediator) {
    this.loginMediator = loginMediator;
  }
  
  public void run() {
    if(this.loggedIn) {
      LoginService.Util.getInstance().isLoggedIn(this);
    }
  }

  protected void schedule() {
    this.schedule(this.delta);
  }

  protected void logout() {
    if(this.loggedIn) {
      loginMediator.logoutEvent();
    }
  }

  public void loginEvent() {
    this.loggedIn = true;
    this.schedule();
  }

  public void logoutEvent() {
    this.loggedIn = false;
  }

  public void onFailure(final Throwable t) {
    this.logout();
  }

  public void onSuccess(final Boolean loggedIn) {
    if(!loggedIn) {
      this.logout();
    } else {
      this.schedule();
    }
  }

  public void loginFailedEvent() {
  }
}
