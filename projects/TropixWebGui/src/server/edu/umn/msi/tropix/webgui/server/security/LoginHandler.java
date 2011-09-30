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

public interface LoginHandler {

  /**
   * Causes the user to logout.
   * 
   */
  void logout();

  /**
   * Attempts a guest login. If this is not allowed, an exception should be thrown.
   */
  void guestLogin();
  
  /**
   * Logs the user in. Should set whatever state information necessecary for isLoggedIn() to be called.
   *
   * This method should thrown an exception if this login attempt fails.
   * 
   * @param username
   *          Username corresponding to user log in
   * @param password
   *          User password to use for authorization
   * @param key 
   *          Key to authentication mechanism (i.e. service) to use.
   * @return
   */
  void loginAttempt(final String username, final String password, final String key);
  
}
