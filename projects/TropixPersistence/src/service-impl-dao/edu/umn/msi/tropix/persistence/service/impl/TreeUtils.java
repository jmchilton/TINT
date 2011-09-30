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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;

public class TreeUtils {

  static <T> void stackRecursion(final T initObject, final Function<T, ? extends Iterable<T>> childrenFunction, final Closure<T> modifier) {
    final Stack<T> toProcess = new Stack<T>();
    toProcess.add(initObject);
    while(!toProcess.isEmpty()) {
      final T object = toProcess.pop();
      Iterables.addAll(toProcess, childrenFunction.apply(object));
      modifier.apply(object);
    }
  }

  static void applyPermissionChange(final TropixObject object, final Closure<TropixObject> closure, final boolean applyFolder) {
    stackRecursion(object, new Function<TropixObject, Collection<TropixObject>>() {
      public Collection<TropixObject> apply(final TropixObject object) {
        return (!(object instanceof Folder) || applyFolder) ? object.getPermissionChildren() : new LinkedList<TropixObject>();
      }
    }, closure);
  }

  static void applyPermissionChange(final TropixObject object, final Closure<TropixObject> closure) {
    applyPermissionChange(object, closure, false);
  }

}
