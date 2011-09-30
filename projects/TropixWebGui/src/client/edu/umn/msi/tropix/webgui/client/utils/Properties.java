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

/**
 * Utility methods for creating Property objects.
 * 
 * @author John Chilton
 *
 */
public class Properties {
  
  /**
   * Creates a new property with null as the initial value.
   * 
   * @param <T> Type of property to create.
   */
  public static <T> Property<T> newProperty() {
    return newProperty(null);
  }
  
  /**
   * Creates a new property with the specified initialValue.
   * 
   * @param <T> Type of property to create.
   */
  public static <T> Property<T> newProperty(final T initialValue) {
    return new PropertyImpl<T>(initialValue);
  }
  
  /**
   * 
   * @param <T> Type of property and listener.
   * @param property Property to update onEvent.
   * @return A listener which upon invocation will update supplied
   * property.
   */
  public static <T> Listener<T> asListener(final Property<T> property) {
    return new Listener<T>() {

      public void onEvent(final T value) {
        property.set(value);
      }
      
    };
    
  }
  
}
