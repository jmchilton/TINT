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

package edu.umn.msi.tropix.webgui.server.security;

import edu.umn.msi.tropix.grid.credentials.Credential;

/**
 * Bean implementation of UserSession for testing purposes.
 * 
 * @author John Chilton
 *
 */
public class UserSessionBean implements UserSession {
  private long loggedInTime = -1;
  private Credential proxy;
  private String gridId;
  private boolean guest = false, admin = false;

  public boolean isLoggedIn() {
    return proxy != null;
  }
  
  public boolean isAdmin() {
    return admin;
  }
  
  public void setAdmin(final boolean admin) {
    this.admin = admin;
  }
  
  public boolean isGuest() {
    return guest;
  }

  public void setGuest(final boolean guest) {
    this.guest = guest;
  }

  public long getLoggedInTime() {
    return loggedInTime;
  }

  public void setLoggedInTime(final long loggedInTime) {
    this.loggedInTime = loggedInTime;
  }

  public Credential getProxy() {
    return this.proxy;
  }

  public void setProxy(final Credential proxy) {
    this.proxy = proxy;
  }

  public String getGridId() {
    return this.gridId;
  }

  public void setGridId(final String gridId) {
    this.gridId = gridId;
  }


}
