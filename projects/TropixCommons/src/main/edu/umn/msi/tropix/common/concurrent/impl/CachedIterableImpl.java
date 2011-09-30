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

package edu.umn.msi.tropix.common.concurrent.impl;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.collect.Iterables;

/**
 * This class is an {@link Iterable} that caches another {@link Iterable} with
 * elements of type {@code T}.
 * 
 * @author John Chilton
 *
 * @param <T> Type of element of {@link Iterable} to cache.
 */
public class CachedIterableImpl<T> extends CachedSupport<Iterable<T>> implements Iterable<T> {
  private Iterable<T> baseIterable;

  public Iterator<T> iterator() {
    return this.getCachedInstance().iterator();
  }

  public void setBaseIterable(final Iterable<T> baseIterable) {
    this.baseIterable = baseIterable;
  }

  @Override
  protected Iterable<T> getInstance() {
    final LinkedList<T> cachedList = new LinkedList<T>();
    for(final T object : this.baseIterable) {
      cachedList.add(object);
    }
    return Iterables.unmodifiableIterable(cachedList);
  }

}
