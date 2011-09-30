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

package edu.umn.msi.tropix.webgui.services.session;

/**
 * Interface to represent the constants contained in resource bundle:'/home/blynch/code/proteomics/trunk/src/edu/umn/msi/client/LoginConstants.properties' .
 */
public interface LoginConstants extends com.google.gwt.i18n.client.Constants {

  /**
   * Translated "Logout...".
   * 
   * @return translated "Logout..."
   * @gwt.key logoutLinkText
   */
  String logoutLinkText();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
   * @gwt.key namePrompt
   */
  String namePrompt();

  /**
   * Translated "Welcome to the GWT!".
   * 
   * @return translated "Welcome to the GWT!"
   * @gwt.key welcomeMsg
   */
  String welcomeMsg();

  /**
   * Translated "Password".
   * 
   * @return translated "Password"
   * @gwt.key passwordPrompt
   */
  String passwordPrompt();

  /**
   * Translated "Invalid name/pwd combination. Please try again.".
   * 
   * @return translated "Invalid name/pwd combination. Please try again."
   * @gwt.key errorMsg
   */
  String errorMsg();

  /**
   * Translated "Please <a href="http://www.cnn.com">www.cnn.com</a> click that".
   * 
   * @return translated "Please <a href="http://www.cnn.com">www.cnn.com</a> click that"
   * @gwt.key loginPrompt
   */
  String loginPrompt();

  /**
   * Translated "Log in".
   * 
   * @return translated "Log in"
   * @gwt.key loginButtonText
   */
  String loginButtonText();
}
