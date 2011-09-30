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

package edu.umn.msi.tropix.persistence.service.impl.utils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectType;

public class PersistenceModelUtils {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();

  public static <T extends TropixObject> Iterable<T> typeFilter(final Iterable<T> objects, final Iterable<TropixObjectType> types) {
    return Iterables.filter(objects, new Predicate<TropixObject>() {
      public boolean apply(final TropixObject object) {
        return Iterables.any(types, new Predicate<TropixObjectType>() {
          public boolean apply(final TropixObjectType type) {
            return type.isInstance(object);
          }
        });
      }
    });
  }

  public static Class<? extends TropixObject> getClass(final TropixObjectType tropixObjectType) {
    @SuppressWarnings("unchecked")
    final Class<? extends TropixObject> tropixObjectClass = (Class<? extends TropixObject>) REFLECTION_HELPER.forName(tropixObjectType.getClassName());
    return tropixObjectClass;
  }

}
