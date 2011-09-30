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

package edu.umn.msi.tropix.webgui.server.security.impl;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;

@ManagedBean @Named("authenticationEventHandler")
class AuthenticationEventHandler implements AuthenticationEventPublisher {
  private static final Log LOG = LogFactory.getLog(AuthenticationEventHandler.class); 
  
  public void publishAuthenticationFailure(final AuthenticationException exception, final Authentication authentication) {
    ExceptionUtils.logQuietly(LOG, exception, "Authentication failed.");
  }

  public void publishAuthenticationSuccess(final Authentication authentication) {
    LOG.debug("Authentication succeeded for principal " + authentication.getPrincipal());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
