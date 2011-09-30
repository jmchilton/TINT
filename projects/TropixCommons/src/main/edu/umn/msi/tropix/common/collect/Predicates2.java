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

package edu.umn.msi.tropix.common.collect;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Additional {@link Predicate} utilities not found the 
 * Google Collections {@link com.google.common.base.Predicates} class.
 * 
 * @author John Chilton
 *
 */
public class Predicates2 {

  public static <T> Predicate<T> isIn(final Iterable<?> iterable) {
    return new Predicate<T>() {
      public boolean apply(final T object) {
        return Iterables.contains(iterable, object);
      }
    };
  }

  private static class SwappingPredicate<T> implements Predicate<T> {
    private Boolean state;

    SwappingPredicate(final boolean startOdd) {
      this.state = startOdd;
    }

    public boolean apply(final T input) {
      state = !state;
      return state;
    }

  }

  /**
   * Returns a predicate that ignores input and returns true on even invocations.
   * apply(1) => true, apply(1) => false, apply(1) => true.
   */
  public static <T> Predicate<T> even() {
    return new SwappingPredicate<T>(false);
  }

  /**
   * Returns a predicate that ignores input and returns true on odd invocations.
   * apply(1) => false, apply(1) => true, apply(1) => false.
   */
  public static <T> Predicate<T> odd() {
    return new SwappingPredicate<T>(true);
  }
}
