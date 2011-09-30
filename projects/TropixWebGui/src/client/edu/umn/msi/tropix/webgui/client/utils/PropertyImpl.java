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
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

class PropertyImpl<T> implements Property<T> {
  private T value;
  private List<Listener<? super T>> listeners = new ArrayList<Listener<? super T>>();
  
  PropertyImpl(final T initialValue) {
    value = initialValue;
  }

  public HandlerRegistration addListener(final Listener<? super T> listener) {
    listeners.add(listener);
    return new HandlerRegistration() {
      public void removeHandler() {
        listeners.remove(listener);
      }      
    };
  }
  
  public void set(final T newValue) {
    value = newValue;
    for(Listener<? super T> listener : listeners) {
      listener.onEvent(newValue);
    }
  }
  
  public T get() {
    return value;
  }
  
}
