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

package edu.umn.msi.tropix.webgui.client;

import java.util.Collection;

import com.google.inject.Inject;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.PageConfiguration;
import edu.umn.msi.tropix.webgui.client.components.tree.Location;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.RemoveUpdateEvent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator.NavigationSelection;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.Frame;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;
import edu.umn.msi.tropix.webgui.services.object.TropixObjectContext;

public class PageManager extends WidgetSupplierImpl<Layout> implements Listener<NavigationSelection>, CanvasComponent<Layout> {
  private Canvas displayedCanvas = null;
  private ComponentFactory<PageConfiguration, ? extends CanvasComponent<? extends Canvas>> pageComponentFactory;
  private ComponentFactory<Request, Canvas> incomingRequestComponentFactory;
  private TreeItem loadedObject;

  public PageManager() {
    LocationUpdateMediator.getInstance().addListener(new Listener<UpdateEvent>() {
      public void onEvent(final UpdateEvent event) {
        final String locationId = event.getLocationId();
        if(event instanceof RemoveUpdateEvent) {
          if(loadedObject != null && loadedObject.getId().equals(locationId)) {
            reset();
          }
        }
        if(loadedObject != null && loadedObject.getId().equals(locationId)) {
          loadedObject.refresh(new AsyncCallbackImpl<Void>() {
            @Override
            public void onSuccess(final Void ignored) {
              load(loadedObject);
            }
          });
        }
      }
    });
    this.setWidget(new VLayout());
    this.reset();
  }

  @Inject
  public void setNavigationSelectionMediator(final NavigationSelectionMediator mediator) {
    mediator.addNavigationSelectionChangedListener(this);
  }

  @Inject
  public void setPageComponentFactory(final ComponentFactory<PageConfiguration, ? extends CanvasComponent<? extends Canvas>> pageComponentFactory) {
    this.pageComponentFactory = pageComponentFactory;
  }

  @Inject
  public void setIncomingRequestComponentFactory(final ComponentFactory<Request, Canvas> incomingRequestComponentFactory) {
    this.incomingRequestComponentFactory = incomingRequestComponentFactory;
  }

  public void reset() {
    this.loadedObject = null;
    final Frame frame = new Frame();
    frame.setTitle("Welcome");
    final HTMLPane htmlPane = new HTMLPane();
    htmlPane.setContentsURL("home.html");
    htmlPane.setContentsType(ContentsType.PAGE);
    frame.addItem(htmlPane);
    this.setCanvas(frame);
  }

  private void setCanvas(final Canvas cavnas) {
    // Remove previously displayed page if it exists.
    if(this.displayedCanvas != null) {
      this.get().removeMember(this.displayedCanvas);
      this.displayedCanvas.destroy();
    }

    // Add and record current page.
    this.get().addMember(cavnas);
    this.displayedCanvas = cavnas;
  }

  private void load(final TreeItem treeItem) {
    this.loadedObject = treeItem;
    final Location parentItem = treeItem.getParent();
    if(parentItem != null && TreeItems.isIncomingRequestsItem(parentItem)) {
      final Request request = (Request) ((TropixObjectTreeItem) loadedObject).getObject();
      this.setCanvas(incomingRequestComponentFactory.get(request));
    } else if(treeItem instanceof TropixObjectTreeItem) {
      setCanvas(SmartUtils.getLoadingCanvas());
      // Create a new page to display and display it
      ObjectService.Util.getInstance().getObjectContext(treeItem.getId(), new AsyncCallbackImpl<TropixObjectContext>() {
        public void onSuccess(final TropixObjectContext tropixObjectContext) {
          final TropixObjectTreeItem tropixObjectTreeItem = (TropixObjectTreeItem) treeItem;
          final PageConfiguration pageConfiguration = new PageConfiguration(tropixObjectTreeItem, tropixObjectContext);
          final Canvas pageCanvas = pageComponentFactory.get(pageConfiguration).get();
          setCanvas(pageCanvas);
        }
      });
      //setCanvas(getLoadingCanvas());
    }
  }

  public void onEvent(final NavigationSelection navigationSelection) {
    // If something new wasn't select don't perform any action.
    if(navigationSelection == null || navigationSelection.getLocations().isEmpty()) {
      return;
    }
    final Collection<TreeItem> navigationLocations = navigationSelection.getLocations();
    if(navigationLocations.size() == 1) {
      load(navigationLocations.iterator().next());
    } else {
      // TODO: Something more sophisticated
      reset();
    }
  }

}
