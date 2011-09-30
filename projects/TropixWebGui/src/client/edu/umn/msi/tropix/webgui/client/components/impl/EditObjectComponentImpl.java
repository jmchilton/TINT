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

package edu.umn.msi.tropix.webgui.client.components.impl;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;

import edu.umn.msi.tropix.webgui.client.components.EditObjectComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public abstract class EditObjectComponentImpl<C extends Canvas, T> extends WidgetSupplierImpl<C> implements EditObjectComponent<C, T> {
  private final ListenerList<Boolean> validationListeners = ListenerLists.getInstance();
  
  public abstract T getObject();

  protected ItemChangedHandler getItemChangedHandler() {
    return new ItemChangedHandler() {
      public void onItemChanged(final ItemChangedEvent event) {
        validationListeners.onEvent(isValid());
      }    
    };
  }
  
  public void addValidationListener(final Listener<Boolean> validationListener) {
    validationListeners.add(validationListener);    
  }

  public abstract boolean isValid();

}
