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

package edu.umn.msi.tropix.webgui.server.models;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Named;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.models.TropixFile;

// TODO: Remove @Named when everything has been ported to use @Inject 
@ManagedBean @Named("beanSanitizer")
class BeanSanitizerImpl implements BeanSanitizer {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private static List<Class<?>> simpleTypes = Arrays.<Class<?>>asList(Integer.class, int.class, Double.class, double.class, Long.class, long.class, Short.class, short.class, Boolean.class, boolean.class, Float.class, float.class, String.class, java.sql.Date.class, java.util.Date.class);
  private static List<Method> methodsToCopy = Lists.newArrayList(REFLECTION_HELPER.getMethod(TropixFile.class, "getFileType"));
  private static final Predicate<Class<?>> NOT_INSTRUMENTED_CLASS = new Predicate<Class<?>>() {
    public boolean apply(final Class<?> clazz) {
      return !clazz.toString().contains("CGLIB") && !clazz.toString().contains("javassist");
    }
  };

  public <T> T sanitize(@Nullable final T object) {
    return this.sanitize(object, BeanSanitizerImpl.NOT_INSTRUMENTED_CLASS);
  }

  @SuppressWarnings("unchecked")
  private <T> T sanitize(@Nullable final T object, final Predicate<Class<?>> notInstrumentedClassPredicate) {
    if(object == null) {
      return null;
    }
    Class<T> clazz = (Class<T>) object.getClass();
    while(!notInstrumentedClassPredicate.apply(clazz)) { 
      // If its a dynamic proxy,
      // get super class until its a
      // POJO
      clazz = (Class<T>) clazz.getSuperclass();
    }
    final T copy = REFLECTION_HELPER.newInstance(clazz);
    for(final Method method : clazz.getMethods()) {
      final String methodName = method.getName();
      if(!methodName.startsWith("get")) {
        continue;
      }
      if(method.getParameterTypes().length != 0) {
        continue;
      }
      if(!simpleType(method.getReturnType()) && !methodsToCopy.contains(method)) {
        continue;
      }
      final String setMethodName = "s" + methodName.substring(1);
      Method setMethod;
      try {
        setMethod = clazz.getMethod(setMethodName, method.getReturnType());
      } catch(final NoSuchMethodException e) {
        // If no such setter exists, this wasn't a bean property, just proceed to next method.
        continue;
      }
      try {
        Object obtainedObject = REFLECTION_HELPER.invoke(method, object);
        if(obtainedObject == null) {
          continue;
        }
        // Sanitize this object if needed
        if(!notInstrumentedClassPredicate.apply(obtainedObject.getClass())) {
          obtainedObject = sanitize(obtainedObject, notInstrumentedClassPredicate);
        }
        REFLECTION_HELPER.invoke(setMethod, copy, obtainedObject);
      } catch(final RuntimeException e) {
        // Wasn't a simple bean parameter, or maybe was a lazy property. Either
        // way lets continue;
        continue;
      }
    }
    return copy;
  }

  private static boolean simpleType(final Class<?> type) {
    return simpleTypes.contains(type);
  }
}
