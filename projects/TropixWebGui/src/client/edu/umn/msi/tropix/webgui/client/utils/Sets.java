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

import java.util.Collections;
import java.util.HashSet;


/**
 * Code ripped out of Google Collections that in the larger context is not
 * GWT compatible.
 * 
 */
public class Sets {
  
  /**
   * Creates an empty {@code HashSet} instance.
   *
   * <p><b>Note:</b> if {@code E} is an {@link Enum} type, use {@link
   * EnumSet#noneOf} instead.
   *
   * <p><b>Note:</b> if you need an <i>immutable</i> empty Set, use {@link
   * Collections#emptySet} instead.
   *
   * @return a newly created, empty {@code HashSet}
   */
  public static <E> HashSet<E> newHashSet() {
    return new HashSet<E>();
  }
  
  /**
   * Creates a {@code HashSet} instance containing the given elements.
   *
   * <p><b>Note:</b> if {@code E} is an {@link Enum} type, use {@link
   * EnumSet#of(Enum, Enum...)} instead.
   *
   * <p><b>Note:</b> if you need an immutable set without nulls, you should use
   * {@link ImmutableSet#of(Object...)}.
   *
   * <p><b>Note:</b> due to a bug in javac 1.5.0_06, we cannot support the
   * following:
   *
   * <p>{@code Set<Base> set = Sets.newHashSet(sub1, sub2);}
   *
   * <p>where {@code sub1} and {@code sub2} are references to subtypes of {@code
   * Base}, not of {@code Base} itself. To get around this, you must use:
   *
   * <p>{@code Set<Base> set = Sets.<Base>newHashSet(sub1, sub2);}
   *
   * @param elements the elements that the set should contain
   * @return a newly created {@code HashSet} containing those elements (minus
   *     duplicates)
   */
  public static <E> HashSet<E> newHashSet(final E... elements) {
    int capacity = Maps.capacity(elements.length);
    HashSet<E> set = new HashSet<E>(capacity);
    Collections.addAll(set, elements);
    return set;
  }


}
