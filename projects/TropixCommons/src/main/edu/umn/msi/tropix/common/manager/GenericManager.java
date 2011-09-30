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

package edu.umn.msi.tropix.common.manager;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

/**
 * A thin wrapper around a simple java map, whose contents can easily be set from Spring. This seems to 
 * be a fair base class for dispatcher classes.
 * 
 * I considered using just an association with a Function<S,T> from inside of the dispatchers. This 
 * would be a slightly better design in an abstract sense, but would be more difficult to populate from 
 * Spring and would less easy to add dynamic adding and removing of handlers later
 * on if need be for whatever reason.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 * @param <S>
 *          Key type
 * @param <T>
 *          Value type
 */
public class GenericManager<S, T> implements Function<S, T> {
  private Map<S, T> map;

  public void setMap(final Map<S, T> map) {
    this.map = map;
  }

  public T apply(final S key) {
    return this.map.get(key);
  }

  public T get(final S key) {
    return this.map.get(key);
  }

  public void clear() {
    this.map.clear();
  }

  public boolean containsKey(final Object key) {
    return this.map.containsKey(key);
  }

  public boolean containsValue(final Object value) {
    return this.map.containsValue(value);
  }

  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  public Set<S> keySet() {
    return this.map.keySet();
  }

  public T put(final S key, final T value) {
    return this.map.put(key, value);
  }

  public void putAll(final Map<? extends S, ? extends T> t) {
    this.map.putAll(t);
  }

  public T remove(final Object key) {
    return this.map.remove(key);
  }

  public int size() {
    return this.map.size();
  }

}
