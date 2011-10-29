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
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

@Aspect
@Order(10)
// This must be applied before AutoUserAspect so not just anyone can create a user, so this has Order 10 that has Order 20
class SecurityAspect {
  private static final Log LOG = LogFactory.getLog(SecurityAspect.class);
  private static int count = 0;
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private TropixObjectDao tropixObjectDao;
  private SecurityProvider securityProvider;

  SecurityAspect(final TropixObjectDao tropixObjectDao, final SecurityProvider securityProvider) {
    LOG.debug("Constructing SecurityAspect number " + ++count);
    this.tropixObjectDao = tropixObjectDao;
    this.securityProvider = securityProvider;
  }

  public boolean containsAnnotation(final Class<? extends Annotation> type, final Annotation[] annotations) {
    boolean contained = false;
    for(final Annotation annotation : annotations) {
      if(annotation.annotationType().equals(type)) {
        contained = true;
        break;
      }
    }
    return contained;
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

  private void verify(final String userId, final Object object, final Annotation[] annotations, final String description) {
    if(object == null && !containsAnnotation(Nullable.class, annotations)) {
      throw new IllegalArgumentException("Null id found for not nullable argument - " + description);
    } else if(object == null) {
      return;
    }
    String[] objectIds = null;
    if(object instanceof String) {
      objectIds = new String[] {(String) object};
    } else if(object instanceof String[]) {
      objectIds = (String[]) object;
    } else {
      objectIds = null;
    }
    if(containsAnnotation(Owns.class, annotations)) {
      for(final String objectId : objectIds) {
        if(!tropixObjectDao.isAnOwner(userId, objectId)) {
          throw new RuntimeException("User " + userId + " does not own object with id " + objectId);
        }
      }
    } else if(containsAnnotation(Modifies.class, annotations)) {
      if(objectIds == null) {
        final Modifies modifies = getAnnnotation(Modifies.class, annotations);
        final String methodName = modifies.method();
        final String objectId = (String) REFLECTION_HELPER.invoke(methodName, object);
        objectIds = new String[] {objectId};
      }
      for(final String objectId : objectIds) {
        if(!securityProvider.canModify(objectId, userId)) {
          throw new RuntimeException("User " + userId + " cannot modify object with id " + objectId);
        }
      }
      return;
    } else if(containsAnnotation(Reads.class, annotations)) {
      for(final String objectId : objectIds) {
        if(!securityProvider.canRead(objectId, userId)) {
          throw new RuntimeException("User " + userId + " cannot read object with id " + objectId + " object is " + object);
        }
      }
      return;
    }
  }

  @Before(value = "execution(* edu.umn.msi.tropix.persistence.service.*+.*(..))")
  public void doAccessCheck(final JoinPoint joinPoint) {
    LOG.trace("Verifying persistence layer access to " + joinPoint.getSignature().toLongString());
    final Method method = AopUtils.getMethod(joinPoint);
    if(method.getAnnotation(PersistenceMethod.class) == null) {
      return;
    }
    final Annotation[][] annotations = method.getParameterAnnotations();
    String userId = null;
    for(int i = 0; i < annotations.length; i++) {
      final UserId userIdAnnotation = getAnnnotation(UserId.class, annotations[i]);
      if(userIdAnnotation != null) {
        final Object arg = joinPoint.getArgs()[i];
        userId = (String) arg;
        break;
      }
    }
    Preconditions.checkNotNull(userId, "No parameter ananotations of type UserId found, but one is required.");
    @SuppressWarnings("unchecked")
    final Collection<Class<? extends Annotation>> securityAnnotations = Arrays.asList(Owns.class, Reads.class, Modifies.class);
    for(int i = 0; i < annotations.length; i++) {
      for(final Annotation annotation : annotations[i]) {
        if(securityAnnotations.contains(annotation.annotationType())) {
          verify(userId, joinPoint.getArgs()[i], annotations[i], joinPoint.getSignature() + "[" + i + "]");
        }
      }
    }

  }

}
