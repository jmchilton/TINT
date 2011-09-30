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
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Constructed from any number of other iterables, this {@link Iterable}
 * will only return the unique instances of elements from these
 * {@link Iterable} objects. 
 * 
 * @author John Chilton
 *
 */
public class UniqueIterable<T> implements Iterable<T> {
  private final Iterable<? extends Iterable<T>> iterables;

  public UniqueIterable(final Iterable<T>... iterables) {
    this.iterables = Arrays.asList(iterables);
  }

  public UniqueIterable(final Iterable<? extends Iterable<T>> iterables) {
    this.iterables = iterables;
  }

  class IterablesIterator implements Iterator<T> {
    private final HashSet<T> set = new HashSet<T>();
    private final Iterator<? extends Iterable<T>> iterableIter = UniqueIterable.this.iterables.iterator();
    private Iterator<T> iterator = null;
    private T next = null;

    // Returns true and sets next if iterator has another unique item
    public boolean hasNext() {
      outer: while(this.next == null) {
        while(this.iterator == null || !this.iterator.hasNext()) {
          if(!this.iterableIter.hasNext()) {
            break outer;
          }
          this.iterator = this.iterableIter.next().iterator();
        }
        final T possibleNext = this.iterator.next();
        if(this.set.contains(possibleNext)) {
          continue outer;
        } else {
          this.set.add(possibleNext);
          this.next = possibleNext;
          break outer;
        }
      }
      return this.next != null;
    }

    public T next() {
      if(this.next == null && !this.hasNext()) {
        throw new NoSuchElementException();
      }
      final T nextToReturn = this.next;
      this.next = null;
      return nextToReturn;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public Iterator<T> iterator() {
    return new IterablesIterator();
  }
}
