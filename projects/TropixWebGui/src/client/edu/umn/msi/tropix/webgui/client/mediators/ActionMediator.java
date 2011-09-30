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

package edu.umn.msi.tropix.webgui.client.mediators;

import java.util.HashMap;
import java.util.Map;

import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;

public class ActionMediator {
  private final Map<String, ListenerList<ActionEvent>> eventHandlers = new HashMap<String, ListenerList<ActionEvent>>();

  public interface ActionEvent {
    String getActionType();
  }

  public void registerActionListener(final String actionType, final Listener<ActionEvent> eventHandler) {
    System.out.println("Registering action listener for type <" + actionType + "> " + eventHandler);
    if(!eventHandlers.containsKey(actionType)) {
      eventHandlers.put(actionType, ListenerLists.<ActionEvent>getInstance());
    }
    eventHandlers.get(actionType).add(eventHandler);
  }

  public void handleEvent(final ActionEvent actionEvent) {
    System.out.println("Handling actionEvent <" + actionEvent.getActionType() + ">");
    try {
      final ListenerList<ActionEvent> listeners = eventHandlers.get(actionEvent.getActionType());
      System.out.println("Using hanldler list " + listeners);
      listeners.onEvent(actionEvent);
    } catch(final RuntimeException e) {
      e.printStackTrace();
    }
  }

}
