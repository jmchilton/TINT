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

package edu.umn.msi.tropix.webgui.server.aop;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import edu.umn.msi.tropix.webgui.server.security.UserSession;

/**
 * The class intercepts all incoming requests to secure service methods and prevents execution if a user is not logged in.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
@Aspect
public class SecurityAspect {
  private static final Log LOG = LogFactory.getLog(SecurityAspect.class);
  private final UserSession userSession;

  @Inject
  SecurityAspect(final UserSession userSession) {
    this.userSession = userSession;
  }

  @Before(value = "@annotation(serviceMethod)", argNames = "serviceMethod")
  public void doAccessCheck(final JoinPoint joinPoint, final ServiceMethod serviceMethod) {
    if(serviceMethod.secure() && !userSession.isLoggedIn()) {
      LOG.warn("Invalid access attempting on method [" + joinPoint.toLongString() + "]");
      throw new IllegalStateException("Attempt to call secure method by an user who is not logged in.");
    } else if(serviceMethod.adminOnly()) {
      final String gridId = userSession.getGridId();
      if(gridId == null || !userSession.isAdmin()) {
        LOG.warn("Invalid access attempting on method [" + joinPoint.toLongString() + "]");
        throw new IllegalStateException("Attempt to call an admin method by a user who is not an admin.");
      }
    }
  }

}
