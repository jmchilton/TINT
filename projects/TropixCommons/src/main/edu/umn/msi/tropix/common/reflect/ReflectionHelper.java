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

/**
 * This is a utility interface meant to ease reflection by implementing
 * some high level methods and converting any checked exceptions to
 * runtime exceptions.
 * 
 * @author John Chilton
 * 
 */
public interface ReflectionHelper {

  Class<?> forName(String name);

  <T> T newInstance(Class<T> clazz);

  Object invoke(String methodName, Object targetObject, Object... arguments);

  Object invoke(Method targetMethod, Object targetObject, Object... arguments);

  <C> Constructor<C> getConstructor(Class<C> clazz, Class<?>... args);

  <T> T newInstance(Class<T> clazz, Class<?>[] constructorArgTypes, Object[] constructorArgs);

  void setBeanProperty(Object targetObject, String propertyName, Object value);

  Object getBeanProperty(Object targetObject, String propertyName);

  void copyProperties(Object dest, Object orig);

  Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes);

  boolean hasMethod(Class<?> clazz, String methodName);
}
