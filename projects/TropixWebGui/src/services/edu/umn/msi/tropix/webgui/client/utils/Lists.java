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

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class Lists {

  /**
   * Creates an {@code ArrayList} instance containing the given elements.
   *
   * <p><b>Note:</b> if you need an immutable List, use {@link ImmutableList}
   * instead.
   *
   * <p><b>Note:</b> due to a bug in javac 1.5.0_06, we cannot support the
   * following:
   *
   * <p>{@code List<Base> list = Lists.newArrayList(sub1, sub2);}
   *
   * <p>where {@code sub1} and {@code sub2} are references to subtypes of {@code
   * Base}, not of {@code Base} itself. To get around this, you must use:
   *
   * <p>{@code List<Base> list = Lists.<Base>newArrayList(sub1, sub2);}
   *
   * @param elements the elements that the list should contain, in order
   * @return a new {@code ArrayList} containing those elements
   */
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(final E... elements) {
    // Avoid integer overflow when a large array is passed in
    int capacity = computeArrayListCapacity(elements.length);
    ArrayList<E> list = new ArrayList<E>(capacity);
    Collections.addAll(list, elements);
    return list;
  }


  @VisibleForTesting static int computeArrayListCapacity(final int arraySize) {
    Preconditions.checkArgument(arraySize >= 0);

    // TODO: Figure out the right behavior, and document it
    return (int) Math.min(5L + arraySize + (arraySize / 10), Integer.MAX_VALUE);
  }

  
  /**
   * Creates an {@code ArrayList} instance backed by an array of the
   * <i>exact</i> size specified; equivalent to
   * {@link ArrayList#ArrayList(int)}.
   *
   * <p><b>Note:</b> if you know the exact size your list will be, consider
   * using a fixed-size list ({@link Arrays#asList(Object[])}) or an {@link
   * ImmutableList} instead of a growable {@link ArrayList}.
   *
   * <p><b>Note:</b> If you have only an <i>estimate</i> of the eventual size of
   * the list, consider padding this estimate by a suitable amount, or simply
   * use {@link #newArrayListWithExpectedSize(int)} instead.
   *
   * @param initialArraySize the exact size of the initial backing array for
   *     the returned array list ({@code ArrayList} documentation calls this
   *     value the "capacity")
   * @return a new, empty {@code ArrayList}, which is guaranteed not to resize
   *     itself unless its size reaches {@code initialArraySize + 1}
   * @throws IllegalArgumentException if {@code initialArraySize} is negative
   */
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayListWithCapacity(
      final int initialArraySize) {
    return new ArrayList<E>(initialArraySize);
  }


}
