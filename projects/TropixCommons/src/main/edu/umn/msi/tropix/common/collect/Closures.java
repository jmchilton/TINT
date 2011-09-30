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

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Function;

/**
 * Utilities for dealing with Closure objects.
 * 
 * @author John Chilton
 *
 */
public class Closures {

  public static <S> Closure<S> forFunction(final Function<? super S, ?> function) {
    return new Closure<S>() {
      public void apply(final S input) {
        function.apply(input);
      }
    };
  }

  public static <S> Closure<S> compose(final Closure<? super S>... modifiers) {
    return compose(Arrays.asList(modifiers));
  }

  public static <S> Closure<S> compose(final Iterable<Closure<? super S>> closures) {
    return new Closure<S>() {
      public void apply(final S input) {
        for(final Closure<? super S> modifier : closures) {
          modifier.apply(input);
        }
      }
    };
  }

  public static <S> Closure<S> nullClosure() {
    return new Closure<S>() {
      public void apply(final S input) {
      }
    };
  }

  public static <S> void forEach(final Iterator<? extends S> iterator, final Closure<? super S> closure) {
    while(iterator.hasNext()) {
      closure.apply(iterator.next());
    }
  }

  public static <S> void forEach(final Iterable<? extends S> iterable, final Closure<? super S> closure) {
    forEach(iterable.iterator(), closure);
  }

}
