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

import com.google.common.base.Predicate;

import edu.umn.msi.tropix.models.TropixObject;

/**
 * This class contains static methods for obtaining {@link Predicate} objects that
 * perform some basic operations on {@link TropixObject}s. 
 * 
 * @author John Chilton
 *
 */
public class ModelPredicates {

  /**
   * 
   * @param <S> Subclass of TropixObject for predicate if needed.
   * @param tropixObjectType A description of the type to query against.
   * @return A predicate that returns true iff the input object is an object
   * of the type described by tropixObjectType.
   */
  public static <S extends TropixObject> Predicate<S> getTypePredicate(final TropixObjectType tropixObjectType) {
    return new Predicate<S>() {
      public boolean apply(final S object) {
        return tropixObjectType.isInstance(object);
      }
    };
  }
}
