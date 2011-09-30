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

package edu.umn.msi.tropix.models.utils;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.TropixObject;

public class TropixObjectTypeEnumTest {

  @Test(groups = "unit")
  public void testEnum() throws Exception {
    for(final TropixObjectTypeEnum value : TropixObjectTypeEnum.values()) {
      assert value.equals(TropixObjectTypeEnum.valueOf(value.toString()));
      final String className = value.getClassName();
      if(!className.endsWith("TropixObject")) {
        final TropixObjectTypeEnum parentType = value.getParentType();
        final Class<?> clazz = Class.forName(className);
        final TropixObject instance = (TropixObject) clazz.newInstance();
        assert clazz.getSuperclass().equals(Class.forName(parentType.getClassName()));
        assert value.isInstance(instance);
        assert parentType.isInstance(instance);
        assert !value.isInstance(new TropixObject());
        assert TropixObjectTypeEnum.getType(instance).equals(value);
      }
    }
  }

}
