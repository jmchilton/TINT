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

import java.util.Collection;

import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.Canvas;

import edu.umn.msi.tropix.webgui.client.components.MultiSelectionComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

/**
 * Abstract superclass for SelectionComponents and MultiSelectionComponents. Sub classes should override getSelection and getMultiSelection (optional), and call onSelectionChangedCommand.execute() each time the components selection is changed.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu).
 * 
 */
public abstract class SelectionComponentBaseImpl<S, T extends Canvas> extends WidgetSupplierImpl<T> implements MultiSelectionComponent<S, T> {
  private final ListenerList<S> listenerList = ListenerLists.getInstance(); // new ListenerListImpl<S>();
  private final ListenerList<Collection<S>> multiListenerList = ListenerLists.getInstance(); // new ListenerListImpl<Collection<S>>();

  public S getSelection() {
    return null;
  }

  public Collection<S> getMultiSelection() {
    return null;
  }

  public void addSelectionListener(final Listener<S> listener) {
    this.listenerList.add(listener);
  }

  public void removeSelectionListener(final Listener<S> listener) {
    this.listenerList.remove(listener);
  }

  private final Command onSelectionChangedCommand = new Command() {
    public void execute() {
      if(!SelectionComponentBaseImpl.this.listenerList.isEmpty()) {
        final S selection = SelectionComponentBaseImpl.this.getSelection();
        SelectionComponentBaseImpl.this.listenerList.onEvent(selection);
      }
      if(!SelectionComponentBaseImpl.this.multiListenerList.isEmpty()) {
        final Collection<S> multiSelection = SelectionComponentBaseImpl.this.getMultiSelection();
        SelectionComponentBaseImpl.this.multiListenerList.onEvent(multiSelection);
      }
    }
  };

  public void addMultiSelectionListener(final Listener<Collection<S>> listener) {
    this.multiListenerList.add(listener);
  }

  public void removeMultiSelectionListener(final Listener<Collection<S>> listener) {
    this.multiListenerList.remove(listener);
  }

  protected Command getOnSelectionChangedCommand() {
    return onSelectionChangedCommand;
  }

}
