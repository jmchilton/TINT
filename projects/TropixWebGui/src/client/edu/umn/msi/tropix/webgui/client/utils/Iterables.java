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

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * 
 * Like a subset com.google.common.collect.Iteralbes, but safe for GWT.
 * 
 * Certain methods in the class were taken in part or in whole for the Google Collections library, which is licensed under the Apache License, Version 2.0.
 * 
 * @author John Chilton
 * 
 */
public class Iterables {

  /**
   * Returns the first element in {@code iterable} that satisfies the given predicate.
   * 
   */
  public static <E> E find(final Iterable<E> iterable, final Predicate<? super E> predicate) {
    E result = null;
    for(final E item : iterable) {
      if(predicate.apply(item)) {
        result = item;
        break;
      }
    }
    // Like Google Collections, it might be a better design to throw
    // NoSuchElementException
    // here, but I am not sure about the runtime performance of throwing
    // exceptions
    // in GWT generated code.
    return result;
  }

  /**
   * Returns the elements of {@code unfiltered} that satisfy a predicate. The resulting iterable's iterator does not support {@code remove()}.
   */
  public static <T> Iterable<T> filter(final Iterable<T> unfiltered, final Predicate<? super T> predicate) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        return Iterators.filter(unfiltered.iterator(), predicate);
      }
    };
  }

  /**
   * Returns whether the given iterable contains no elements.
   * 
   * @return {@code true} if the iterable has no elements, {@code false} if the iterable has one or more elements
   */
  public static <T> boolean isEmpty(final Iterable<T> iterable) {
    return !iterable.iterator().hasNext();
  }

  /**
   * Returns {@code true} if one or more elements in {@code iterable} satisfy the predicate.
   */
  public static <T> boolean any(final Iterable<T> iterable, final Predicate<? super T> predicate) {
    return Iterators.any(iterable.iterator(), predicate);
  }

  /**
   * Returns {@code true} if every element in {@code iterable} satisfies the predicate. If {@code iterable} is empty, {@code true} is returned.
   */
  public static <T> boolean all(final Iterable<T> iterable, final Predicate<? super T> predicate) {
    return Iterators.all(iterable.iterator(), predicate);
  }

  /**
   * Returns the number of elements in {@code iterable}.
   */
  public static int size(final Iterable<?> iterable) {
    return (iterable instanceof Collection<?>) ? ((Collection<?>) iterable).size() : Iterators.size(iterable.iterator());
  }

  /**
   * Returns an iterable that applies {@code function} to each element of {@code fromIterable}.
   * 
   * <p>
   * The returned iterable's iterator supports {@code remove()} if the provided iterator does. After a successful {@code remove()} call, {@code fromIterable} no longer contains the corresponding element.
   */
  public static <F, T> Iterable<T> transform(final Iterable<F> fromIterable, final Function<? super F, ? extends T> function) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        return Iterators.transform(fromIterable.iterator(), function);
      }
    };
  }

  /**
   * Returns the single element contained in {@code iterable}.
   * 
   * @throws NoSuchElementException
   *           if the iterable is empty
   * @throws IllegalArgumentException
   *           if the iterable contains multiple elements
   */
  public static <T> T getOnlyElement(final Iterable<T> iterable) {
    return Iterators.getOnlyElement(iterable.iterator());
  }

}
