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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import edu.umn.msi.tropix.webgui.server.TropixStatus;

/**
 * This class locks all web services that are not read only after TropixStatus is set to lock. This is to prevent new modifications before an impending restart of the servlet.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
@Aspect
class LockingAspect {
  private final TropixStatus tropixStatus;

  @Inject
  LockingAspect(final TropixStatus tropixStatus) {
    this.tropixStatus = tropixStatus;
  }


  @Before(value = "@annotation(serviceMethod)", argNames = "serviceMethod")
  public void doLocking(final JoinPoint joinPoint, final ServiceMethod serviceMethod) {
    if(!serviceMethod.readOnly() && tropixStatus.isLocked()) {
      throw new IllegalStateException("Attempt to call a service method that is not read only after Tropix has been locked.");
    }
  }

}
