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

import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;
import edu.umn.msi.tropix.webgui.services.message.ObjectAddedMessage;

public class LocationUpdateMediator implements Listener<LocationUpdateMediator.UpdateEvent> {
  private static LocationUpdateMediator instance = null;
  private final ListenerList<UpdateEvent> listeners = ListenerLists.getInstance();

  public static class UpdateEvent {
    private final String locationId;
    private final Object source;

    public UpdateEvent(final String locationId, final Object source) {
      this.locationId = locationId;
      this.source = source;
    }

    public String getLocationId() {
      return this.locationId;
    }

    public Object getSource() {
      return this.source;
    }
  }

  public static class RemoveUpdateEvent extends UpdateEvent {
    public RemoveUpdateEvent(final String locationId, final Object source) {
      super(locationId, source);
    }
  }

  public static class AddUpdateEvent extends UpdateEvent {
    private final String parentLocationId;

    public AddUpdateEvent(final String locationId, final Object source, final String parentLocationId) {
      super(locationId, source);
      this.parentLocationId = parentLocationId;
    }

    public String getParentLocationId() {
      return this.parentLocationId;
    }
  }

  protected LocationUpdateMediator() {
    ObjectAddedMessage.registerListener(new Listener<ObjectAddedMessage>() {
      public void onEvent(final ObjectAddedMessage message) {
        final AddUpdateEvent event = new AddUpdateEvent(message.getObjectId(), message, message.getDestinationId());
        LocationUpdateMediator.this.onEvent(event);
      }
    });
  }

  public static LocationUpdateMediator getInstance() {
    if(LocationUpdateMediator.instance == null) {
      LocationUpdateMediator.instance = new LocationUpdateMediator();
    }
    return LocationUpdateMediator.instance;
  }

  public void addListener(final Listener<UpdateEvent> listener) {
    this.listeners.add(listener);
  }

  public void removeListener(final Listener<UpdateEvent> listener) {
    this.listeners.remove(listener);
  }

  public void onEvent(final UpdateEvent updateEvent) {
    this.listeners.onEvent(updateEvent);
    if(updateEvent instanceof AddUpdateEvent) {
      // Upon adding stuff, update recent activity as well...
      this.listeners.onEvent(new UpdateEvent(TreeItems.MY_RECENT_ACTIVITY_ID, this));
    }
  }
}
