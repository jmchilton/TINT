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

package edu.umn.msi.tropix.common.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

/**
 * This class intercepts all exceptions coming out of methods tagged with the LogExceptions annotation and logs them.
 * 
 * @author John Chilton
 * 
 */
@Aspect
public class LoggingAspect {
  private static final Log LOG = LogFactory.getLog(LoggingAspect.class);

  @AfterThrowing(pointcut = "@annotation(edu.umn.msi.tropix.common.logging.LogExceptions)", throwing = "t")
  public void loggerThrowable(final JoinPoint jp, final Throwable t) {
    LOG.warn("Exception thrown by method [" + jp.getSignature().toLongString() + "]");
    ExceptionUtils.logQuietly(LOG, t);
  }

}
