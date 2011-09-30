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

package edu.umn.msi.tropix.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.google.common.collect.Maps;

class ReflectionHelperImpl implements ReflectionHelper {
  private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPES = Maps.newHashMapWithExpectedSize(8);
  static {
    PRIMITIVE_TYPES.put(Boolean.TYPE, Boolean.class);
    PRIMITIVE_TYPES.put(Byte.TYPE, Byte.class);
    PRIMITIVE_TYPES.put(Character.TYPE, Character.class);
    PRIMITIVE_TYPES.put(Short.TYPE, Short.class);
    PRIMITIVE_TYPES.put(Integer.TYPE, Integer.class);
    PRIMITIVE_TYPES.put(Long.TYPE, Long.class);
    PRIMITIVE_TYPES.put(Double.TYPE, Double.class);
    PRIMITIVE_TYPES.put(Float.TYPE, Float.class);
  }

  public <T> T newInstance(final Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch(final Exception e) {
      throw new ReflectionRuntimeException(e);
    }
  }

  public static Class<?> box(final Class<?> arg) {
    return arg.isPrimitive() ? PRIMITIVE_TYPES.get(arg) : arg;
  }

  public Object invoke(final String methodName, final Object targetObject, final Object... arguments) {
    Method targetMethod = null;
    outer: for(final Method method : targetObject.getClass().getMethods()) {
      if(!method.getName().equals(methodName)) {
        continue;
      }
      final Class<?>[] parameterTypes = method.getParameterTypes();
      if(parameterTypes.length != arguments.length) {
        continue;
      }
      for(int i = 0; i < arguments.length; i++) {
        if(arguments[i] != null && !box(parameterTypes[i]).isAssignableFrom(box(arguments[i].getClass()))) {
          continue outer;
        }
      }
      targetMethod = method;
      break;
    }
    if(targetMethod == null) {
      throw new IllegalArgumentException("No such method " + methodName);
    }
    return this.invoke(targetMethod, targetObject, arguments);
  }

  public Object invoke(final Method targetMethod, final Object targetObject, final Object... arguments) {
    Object result;
    try {
      result = targetMethod.invoke(targetObject, arguments);
    } catch(final Exception e) {
      throw new ReflectionRuntimeException(e);
    }
    return result;
  }

  public Class<?> forName(final String name) {
    try {
      return Class.forName(name);
    } catch(final ClassNotFoundException e) {
      throw new ReflectionRuntimeException(e);
    }
  }

  public <C> Constructor<C> getConstructor(final Class<C> clazz, final Class<?>... args) {
    Constructor<C> constructor = null;
    try {
      constructor = clazz.getConstructor(args);
    } catch(final Exception e) {
      throw new ReflectionRuntimeException(e);
    }
    return constructor;
  }

  public <T> T newInstance(final Class<T> clazz, final Class<?>[] constructorArgTypes, final Object[] constructorArgs) {
    final Constructor<T> constructor = getConstructor(clazz, constructorArgTypes);
    T instance = null;
    try {
      instance = constructor.newInstance(constructorArgs);
    } catch(final Exception e) {
      throw new ReflectionRuntimeException(e);
    }
    return instance;
  }

  public void setBeanProperty(final Object targetObject, final String propertyName, final Object value) {
    final String setterName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    invoke(setterName, targetObject, value);
  }

  public Object getBeanProperty(final Object targetObject, final String propertyName) {
    final String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    return invoke(getterName, targetObject);
  }

  public void copyProperties(final Object dest, final Object orig) {
    try {
      BeanUtils.copyProperties(dest, orig);
    } catch(Exception e) {
      throw new ReflectionRuntimeException(e);
    }
  }

  public Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
    try {
      return clazz.getMethod(methodName, parameterTypes);
    } catch(Exception e) {
      throw new ReflectionRuntimeException(e);
    }
  }

  public boolean hasMethod(final Class<?> clazz, final String name) {
    boolean hasMethod = false;
    for(Method method : clazz.getMethods()) {
      if(method.getName().equals(name)) {
        hasMethod = true;
        break;
      }
    }
    return hasMethod;
  }

}
