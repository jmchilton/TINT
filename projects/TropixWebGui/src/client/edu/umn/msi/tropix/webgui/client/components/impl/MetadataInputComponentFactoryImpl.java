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

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.locations.Location;
import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;

public class MetadataInputComponentFactoryImpl implements MetadataInputComponentFactory {
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;

  @Inject
  public MetadataInputComponentFactoryImpl(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
  }

  public MetadataInputComponent get(final MetadataOptions options) {
    return new MetadataInputComponentImpl(options);
  }

  private class MetadataInputComponentImpl implements MetadataInputComponent {
    private final MetadataOptions.DestinationType destinationType;
    private final Listener<Boolean> isValidListener;
    private boolean isValid = false;
    private TreeItem parentObject;
    private final Collection<TreeItem> initialItems;
    private final TextItem nameItem = new TextItem("Name"), descriptionItem = new TextItem("Description");
    private TreeComponent tree;
    private final String objectType;

    private TreeOptions getDefaultTreeOptions() {
      final TreeOptions treeOptions = new TreeOptions();
      if(destinationType == MetadataOptions.DestinationType.ALL) {
        treeOptions.setInitialItems(locationFactory.getTropixObjectDestinationRootItems(null));
      } else {
        treeOptions.setInitialItems(locationFactory.getFolderDestinationRootItems(null));
      }
      treeOptions.setShowPredicate(LocationPredicates.getDestinationsPredicate(true));
      if(destinationType != MetadataOptions.DestinationType.FOLDER) {
        treeOptions.setSelectionPredicate(LocationPredicates.getDestinationsPredicate(false));
      } else {
        treeOptions.setSelectionPredicate(new Predicate<Location>() {
          public boolean apply(final Location treeItem) {
            return !Locations.isMyGroupFoldersItem(treeItem);
          }
        });
      }
      if(initialItems != null) {
        for(final TreeItem initialItem : initialItems) {
          if(validInitialItem(initialItem)) {
            treeOptions.setExpandIds(Locations.getAncestorIds(initialItem));
            treeOptions.setSelectedItems(Arrays.asList(initialItem));
            break;
          }
        }
      }
      return treeOptions;
    }

    MetadataInputComponentImpl(final MetadataOptions options) {
      this.objectType = options.getObjectType();
      this.isValidListener = options.getIsValidListener();
      this.initialItems = options.getInitialItems();
      this.destinationType = options.getDestinationType();
    }

    private void checkValid() {
      this.isValid = this.parentObject != null && StringUtils.hasText(getName());
      this.isValidListener.onEvent(this.isValid);
    }

    private boolean validInitialItem(final TreeItem treeItem) {
      if(treeItem instanceof TropixObjectTreeItem) {
        final TropixObjectLocation toItem = (TropixObjectLocation) treeItem;
        return toItem.getObject() instanceof Folder || toItem.getObject() instanceof VirtualFolder;
      } else {
        return Locations.isMySharedFoldersItem(treeItem);
      }
    }

    public Canvas get() {
      final TreeOptions treeOptions = getDefaultTreeOptions();
      this.tree = treeComponentFactory.get(treeOptions);
      this.tree.addSelectionListener(new Listener<TreeItem>() {
        public void onEvent(final TreeItem treeItem) {
          parentObject = tree.getSelection();
          checkValid();
        }
      });
      this.parentObject = this.tree.getSelection();

      final TreeGrid treeGrid = this.tree.get();
      SmartUtils.setWidthAndHeight100(treeGrid);

      final CanvasItem item = new CanvasItem("parent", "Select parent folder for " + this.objectType);
      item.setCanvas(treeGrid);
      item.setTitleOrientation(TitleOrientation.TOP);
      item.setColSpan(2);
      item.setHeight("*");

      final Form form = new Form();
      SmartUtils.setWidthAndHeight100(form);
      form.setNumCols(2);

      form.setFields(this.nameItem, this.descriptionItem, item);

      this.nameItem.addChangedHandler(new ChangedHandler() {
        public void onChanged(final ChangedEvent event) {
          checkValid();
        }
      });

      return form;
    }

    public void addSelectionListener(final Listener<TreeItem> listener) {
      this.tree.addSelectionListener(listener);
      listener.onEvent(this.tree.getSelection());
    }

    public void setName(final String name) {
      this.nameItem.setValue(name);
      checkValid();
    }

    public String getName() {
      return (String) this.nameItem.getValue();
    }

    public String getDescription() {
      return StringUtils.toString(this.descriptionItem.getValue());
    }

    public String getDestinationId() {
      return this.parentObject == null ? null : this.parentObject.getId();
    }

    public TreeItem getParentObject() {
      return this.parentObject;
    }

    public boolean isValid() {
      return this.isValid;
    }
  }

}
