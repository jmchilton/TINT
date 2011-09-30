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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * Code ripped out of Google Collections that in the larger context is not
 * GWT compatible.
 * 
 */
public class Maps {
  /**
   * Returns an appropriate value for the "capacity" (in reality, "minimum
   * table size") parameter of a {@link HashMap} constructor, such that the
   * resulting table will be between 25% and 50% full when it contains
   * {@code expectedSize} entries.
   *
   * @throws IllegalArgumentException if {@code expectedSize} is negative
   */
  static int capacity(final int expectedSize) {
    Preconditions.checkArgument(expectedSize >= 0);
    return Math.max(expectedSize * 2, 16);
  }

  /**
   * Creates a {@code HashMap} instance with the same mappings as the specified
   * map.
   *
   * <p><b>Note:</b> if {@code K} is an {@link Enum} type, use {@link
   * #newEnumMap} instead.
   *
   * @param map the mappings to be placed in the new map
   * @return a new {@code HashMap} initialized with the mappings from
   *     {@code map}
   */
  public static <K, V> HashMap<K, V> newHashMap(
      final Map<? extends K, ? extends V> map) {
    return new HashMap<K, V>(map);
  }

  /**
   * Creates an insertion-ordered {@code LinkedHashMap} instance.
   *
   * @return a new, empty {@code LinkedHashMap}
   */
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
    return new LinkedHashMap<K, V>();
  }


  
}
