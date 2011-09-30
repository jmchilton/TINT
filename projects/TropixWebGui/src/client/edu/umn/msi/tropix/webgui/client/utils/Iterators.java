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

package edu.umn.msi.tropix.webgui.client.utils;

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;

/**
 * 
 * Like a subset com.google.common.collect.Iteralbes, but safe for GWT.
 * 
 * Certain methods in the class were taken in part or in whole for the Google Collections library, which is licensed under the Apache License, Version 2.0.
 * 
 * @author John Chilton
 * 
 */
public class Iterators {
  /**
   * Returns the elements of {@code unfiltered} that satisfy a predicate. The resulting iterator does not support {@code remove()}.
   */
  public static <T> Iterator<T> filter(final Iterator<T> unfiltered, final Predicate<? super T> predicate) {
    return new AbstractIterator<T>() {
      @Override
      protected T computeNext() {
        while(unfiltered.hasNext()) {
          final T element = unfiltered.next();
          if(predicate.apply(element)) {
            return element;
          }
        }
        return this.endOfData();
      }
    };
  }

  /**
   * Returns the number of elements remaining in {@code iterator}. The iterator will be left exhausted: its {@code hasNext()} method will return {@code false}.
   */
  public static int size(final Iterator<?> iterator) {
    int count = 0;
    while(iterator.hasNext()) {
      iterator.next();
      count++;
    }
    return count;
  }

  /**
   * Returns {@code true} if one or more elements returned by {@code iterator} satisfy the given predicate.
   */
  public static <T> boolean any(final Iterator<T> iterator, final Predicate<? super T> predicate) {
    while(iterator.hasNext()) {
      final T element = iterator.next();
      if(predicate.apply(element)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if every element returned by {@code iterator} satisfies the given predicate. If {@code iterator} is empty, {@code true} is returned.
   */
  public static <T> boolean all(final Iterator<T> iterator, final Predicate<? super T> predicate) {
    while(iterator.hasNext()) {
      final T element = iterator.next();
      if(!predicate.apply(element)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns an iterator that applies {@code function} to each element of {@code fromIterator}.
   * 
   * <p>
   * The returned iterator supports {@code remove()} if the provided iterator does. After a successful {@code remove()} call, {@code fromIterator} no longer contains the corresponding element.
   */
  public static <F, T> Iterator<T> transform(final Iterator<F> fromIterator, final Function<? super F, ? extends T> function) {
    return new Iterator<T>() {
      public boolean hasNext() {
        return fromIterator.hasNext();
      }

      public T next() {
        final F from = fromIterator.next();
        return function.apply(from);
      }

      public void remove() {
        fromIterator.remove();
      }
    };
  }

  /**
   * Returns the single element contained in {@code iterator}.
   * 
   * @throws NoSuchElementException
   *           if the iterator is empty
   * @throws IllegalArgumentException
   *           if the iterator contains multiple elements. The state of the iterator is unspecified.
   */
  public static <T> T getOnlyElement(final Iterator<T> iterator) {
    final T first = iterator.next();
    if(!iterator.hasNext()) {
      return first;
    }
    throw new IllegalArgumentException();
  }

}
