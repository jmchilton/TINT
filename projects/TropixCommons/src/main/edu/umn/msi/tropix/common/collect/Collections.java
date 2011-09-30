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

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Function;

/**
 * Utilities for dealing with collections.
 * 
 * @author John Chilton
 *
 */
public class Collections {
  
  public static <F, T> Collection<T> transform(final Collection<F> collection, final Function<? super F, ? extends T> function) {
    final LinkedList<T> toCollection = new LinkedList<T>();
    Collections.transform(collection, function, toCollection);
    return toCollection;
  }

  public static <F, T> void transform(final Collection<F> collection, final Function<? super F, ? extends T> function, final Collection<T> toCollection) {
    for(final F element : collection) {
      toCollection.add(function.apply(element));
    }
  }
}
