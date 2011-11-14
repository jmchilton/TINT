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

package edu.umn.msi.tropix.common.test;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.easymock.EasyMock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

public class BeanTest {
  private static final  HashMultimap<Class<?>, Object> DEFAULT_TYPE_OBJECTS = HashMultimap.create();
  static {
    DEFAULT_TYPE_OBJECTS.put(String.class, "Hello");
    DEFAULT_TYPE_OBJECTS.put(String.class, "World");
    DEFAULT_TYPE_OBJECTS.put(Integer.class, Integer.valueOf(14));
    DEFAULT_TYPE_OBJECTS.put(int.class, 14);
    DEFAULT_TYPE_OBJECTS.put(Long.class, Long.valueOf(14));
    DEFAULT_TYPE_OBJECTS.put(long.class, 14L);
    DEFAULT_TYPE_OBJECTS.put(Boolean.class, Boolean.TRUE);
    DEFAULT_TYPE_OBJECTS.put(Boolean.class, Boolean.FALSE);
    DEFAULT_TYPE_OBJECTS.put(boolean.class, true);
    DEFAULT_TYPE_OBJECTS.put(boolean.class, false);
    DEFAULT_TYPE_OBJECTS.put(File.class, new File("testFile"));
  }
  
  private final HashMultimap<Class<?>, Object> typeObjects = HashMultimap.create(DEFAULT_TYPE_OBJECTS); 

  /**
   * Object to test getters and setters of.
   */
  private Collection<Object> testObjects = Lists.newLinkedList();

  public static void testBeanProperties(final Object testBean) {
    testBeanProperties(testBean, DEFAULT_TYPE_OBJECTS);
  }
  
  public static void testBeanProperties(final Object testBean, final HashMultimap<Class<?>, Object> typeObjects) {
    try {
      @SuppressWarnings("unchecked")
      final Map<String, ?> propertyMap = PropertyUtils.describe(testBean);
      for(final String propertyName : propertyMap.keySet()) {
        if(propertyName.equals("class") || !PropertyUtils.isWriteable(testBean, propertyName)) {
          continue;
        }
        final Class<?> type = PropertyUtils.getPropertyType(testBean, propertyName);
        Collection<?> objects = null;
        for(final Class<?> typeQuery : typeObjects.keySet()) {
          if(typeQuery.isAssignableFrom(type)) {
            objects = typeObjects.get(typeQuery);
          }
        }
        boolean useEquals = true;
        if(objects == null) {
          useEquals = false;
          try {
            objects = Lists.<Object>newArrayList(EasyMock.createMock(type));
          } catch(final Exception e) {
            // Cannot instantiate mock of this type
            continue;
          }
        }
        for(final Object expectedObject : objects) {
          PropertyUtils.setProperty(testBean, propertyName, expectedObject);
          final Object object = PropertyUtils.getProperty(testBean, propertyName);
          if(useEquals) {
            assert object.equals(expectedObject) : "Expected " + expectedObject + " obtained " + object;
          } else {
            assert object == expectedObject : "Expected " + expectedObject + " obtained " + object;
          }
        }
      }
    } catch(final Exception e) {
      throw new IllegalStateException(e);
    }
  }
  
  public void beanTest() {
    for(final Object testObject : this.getTestObjects()) {
      testBeanProperties(testObject, typeObjects);
    }
  }

  protected void setTestObjects(final Collection<Object> testObjects) {
    this.testObjects = testObjects;
  }

  protected Collection<Object> getTestObjects() {
    return testObjects;
  }
}
