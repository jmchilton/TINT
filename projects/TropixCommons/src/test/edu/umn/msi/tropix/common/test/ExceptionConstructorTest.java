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

import java.lang.reflect.Constructor;

public class ExceptionConstructorTest extends ClassTest {

  public void testClass(final Class<?> clazz) throws Exception {
    final Throwable cause = new Throwable();
    Throwable t;
    for(final Constructor<?> constructor : clazz.getConstructors()) {
      final Class<?>[] classes = constructor.getParameterTypes();
      if(classes.length == 0) {
        t = (Throwable) constructor.newInstance();
      } else if(classes.length == 2 && classes[0].isAssignableFrom(String.class) && classes[1].isAssignableFrom(Throwable.class)) {
        t = (Throwable) constructor.newInstance("moo", cause);
        assert t.getCause().equals(cause);
        assert t.getMessage().equals("moo");
      } else if(classes.length == 1) {
        if(classes[0].isAssignableFrom(String.class)) {
          t = (Throwable) constructor.newInstance("moo");
          assert t.getMessage().equals("moo");
        } else if(classes[0].isAssignableFrom(Throwable.class)) {
          t = (Throwable) constructor.newInstance(cause);
          assert t.getCause().equals(cause);
        }
      }

    }
  }

}
