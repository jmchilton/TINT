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

import java.lang.reflect.Method;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.common.reflect.ReflectionRuntimeException;

public class ReflectionHelperTest {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();

  public static class TestClass {
    public String foo(final int x, final double y) {
      return "moo";
    }
  }

  public static class TestBean {
    private String prop1;
    private int prop2;
    
    public String getProp1() {
      return prop1;
    }
    
    public void setProp1(final String prop1) {
      this.prop1 = prop1;
    }
    
    public int getProp2() {
      return prop2;
    }

    public void setProp2(final int prop2) {
      this.prop2 = prop2;
    }
  }
  
  @Test(groups = "unit")
  public void getMethod() throws SecurityException, NoSuchMethodException {
    assert REFLECTION_HELPER.getMethod(TestClass.class, "foo", int.class, double.class).equals(TestClass.class.getMethod("foo", int.class, double.class));
  }

  @Test(groups = "unit", expectedExceptions=ReflectionRuntimeException.class)
  public void getMethodUnknown() {
    REFLECTION_HELPER.getMethod(TestClass.class, "foo2", int.class, double.class);
  }
  
  @Test(groups = "unit")
  public void testBeanProperties() {
    final TestBean bean1 = new TestBean();
    REFLECTION_HELPER.setBeanProperty(bean1, "prop1", "moo");
    assert bean1.getProp1().equals("moo");
    bean1.setProp2(6);
    assert REFLECTION_HELPER.getBeanProperty(bean1, "prop2").equals(Integer.valueOf(6));
    
    final TestBean bean2 = new TestBean();
    REFLECTION_HELPER.copyProperties(bean2, bean1);
    assert bean2.getProp1().equals("moo");
    assert bean2.getProp2() == 6;
  }

  @Test(groups = "unit")
  public void newInstanceOneArg() {
    assert REFLECTION_HELPER.newInstance(String.class, new Class<?>[] {String.class}, new Object[] {"Moo"}).equals("Moo");
  }

  @Test(groups = "unit", expectedExceptions = ReflectionRuntimeException.class)
  public void invalidConstructor() {
    REFLECTION_HELPER.getConstructor(Object.class, String.class);
  }

  @Test(groups = "unit")
  public void argConstructorException() {
    assert REFLECTION_HELPER.getConstructor(ExceptionInConstructor.class, String.class) != null;
    ReflectionRuntimeException e = null;
    try {
      REFLECTION_HELPER.newInstance(ExceptionInConstructor.class, new Class<?>[] {String.class}, new Object[] {"Moo"});
    } catch(final ReflectionRuntimeException ie) {
      e = ie;
    }
    assert e != null;
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void invalidMethodName() {
    REFLECTION_HELPER.invoke("mooCowMethod", "A String");
  }

  @Test(groups = "unit", expectedExceptions = ReflectionRuntimeException.class)
  public void invokeMethodException() throws SecurityException, NoSuchMethodException {
    final Object object = new Object();
    final Method waitMethod = object.getClass().getMethod("wait");
    REFLECTION_HELPER.invoke(waitMethod, object); // Cannot wait without lock, this should throw an exception
  }

  @Test(groups = "unit")
  public void newInstance() {
    assert REFLECTION_HELPER.newInstance(String.class).equals(new String());
  }

  static class ExceptionInConstructor {
    public ExceptionInConstructor() throws Exception {
      throw new Exception();
    }

    public ExceptionInConstructor(final String arg) throws Exception {
      throw new Exception(arg);
    }
  }

  @Test(groups = "unit", expectedExceptions = ReflectionRuntimeException.class)
  public void newInstanceException() {
    REFLECTION_HELPER.newInstance(ExceptionInConstructor.class);
  }

  public static class Moo {
    public int moo(final int x, final int y) {
      return x + y;
    }

    public int moo(final int x, final int y, final int z) {
      return x + y + z;
    }
  }

  @Test(groups = "unit")
  public void invoke() {
    assert 9 == (Integer) REFLECTION_HELPER.invoke("moo", new Moo(), 4, 5);
    assert 9 == (Integer) REFLECTION_HELPER.invoke("moo", new Moo(), new Object[] {4, 5});
    assert 9 == (Integer) REFLECTION_HELPER.invoke("moo", new Moo(), 4, 4, 1);
    assert 9 == (Integer) REFLECTION_HELPER.invoke("moo", new Moo(), new Object[] {4, 4, 1});
  }

  @Test(groups = "unit")
  public void forName() {
    assert String.class.equals(REFLECTION_HELPER.forName("java.lang.String"));
  }

  @Test(groups = "unit", expectedExceptions = ReflectionRuntimeException.class)
  public void forNameInvalid() {
    assert String.class.equals(REFLECTION_HELPER.forName("java.util.String"));
  }

}
