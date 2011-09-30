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

import java.util.ArrayList;

import javax.inject.Inject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;

@Aspect
public class SanitizingAspect {
  private final BeanSanitizer beanSanitizer;
  
  @Inject
  public SanitizingAspect(final BeanSanitizer beanSanitizer) {
    this.beanSanitizer = beanSanitizer;
  }

  @Around("@annotation(edu.umn.msi.tropix.webgui.aop.SanitizeResults)")
  public Object sanitizeResults(final ProceedingJoinPoint joinPoint) throws Throwable {
    final Object toSanitize = joinPoint.proceed();
    final Object sanitized;
    if(toSanitize instanceof ArrayList<?>) {
      @SuppressWarnings("unchecked")
      final ArrayList<Object> toSanitizeList = (ArrayList<Object>) toSanitize;
      final ArrayList<Object> sanitizedList = Lists.newArrayList(Lists.transform(toSanitizeList, BeanSanitizerUtils.<Object>asFunction(beanSanitizer)));
      sanitized = sanitizedList;
    } else {
      sanitized = beanSanitizer.sanitize(toSanitize);
    }
    return sanitized;
  }
  

}
