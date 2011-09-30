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

package edu.umn.msi.tropix.persistence.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.dao.UserDao;
import edu.umn.msi.tropix.persistence.service.impl.UserUtils;

@Aspect @Order(20) // See not on security aspect for explaination of @Order
class AutoUserAspect {
  private static final Log LOG = LogFactory.getLog(AutoUserAspect.class);
  private UserDao userDao;
  private TropixObjectDao tropixObjectDao;

  AutoUserAspect(final UserDao userDao, final TropixObjectDao tropixObjectDao) {
    LOG.debug("In auto user aspect constructor");
    this.userDao = userDao;
    this.tropixObjectDao = tropixObjectDao;
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getAnnnotation(final Class<T> type, final Annotation[] annotations) {
    T theAnnotation = null;
    for(final Annotation annotation : annotations) {
      if(annotation.annotationType().equals(type)) {
        theAnnotation = (T) annotation;
        break;
      }
    }
    return theAnnotation;
  }

  @Before(value = "execution(* edu.umn.msi.tropix.persistence.service.*+.*(..))")
  public void autoCreateUsers(final JoinPoint joinPoint) {
    final Method method = AopUtils.getMethod(joinPoint);
    //if(method.getAnnotation(PersistenceMethod.class) == null) {
    //  return;
    //}
    final Annotation[][] annotations = method.getParameterAnnotations();
    for(int i = 0; i < annotations.length; i++) {
      final AutoUser autoUserAnnotation = getAnnnotation(AutoUser.class, annotations[i]);
      if(autoUserAnnotation != null) {
        final String autoId = (String) joinPoint.getArgs()[i];
        if(StringUtils.hasText(autoId)) {
          UserUtils.ensureUserExists(autoId, userDao, tropixObjectDao);
        }
      }
    }
  }
}
