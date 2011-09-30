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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.Collection;

import com.google.inject.Inject;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;

import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions.DestinationType;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

public class MetadataWizardPageFactory {
  private final MetadataInputComponentFactory metadataInputComponentFactory;

  @Inject
  MetadataWizardPageFactory(final MetadataInputComponentFactory metadataInputComponentFactory) {
    this.metadataInputComponentFactory = metadataInputComponentFactory;
  }

  public MetadataWizardPageImpl get(final Collection<TreeItem> treeItems, final String objectType) {
    return new MetadataWizardPageImpl(treeItems, objectType, DestinationType.ALL);
  }

  public MetadataWizardPageImpl get(final Collection<TreeItem> treeItems, final String objectType, final DestinationType destinationType) {
    return new MetadataWizardPageImpl(treeItems, objectType, destinationType);
  }

  class MetadataWizardPageImpl extends WizardPageImpl<Canvas> {
    private final MetadataInputComponent metadataInputComponent;

    public MetadataWizardPageImpl(final Collection<TreeItem> treeItems, final String objectType, final DestinationType destinationType) {
      this.setTitle("Metadata");
      this.setDescription("Specify " + objectType + " metadata.");
      final MetadataOptions options = new MetadataOptions(objectType);
      options.setIsValidListener(new Listener<Boolean>() {
        public void onEvent(final Boolean event) {
          MetadataWizardPageImpl.this.setValid(event);
        }
      });
      options.setDestinationType(destinationType);
      options.setInitialItems(treeItems);
      metadataInputComponent = metadataInputComponentFactory.get(options);
      this.setCanvas(metadataInputComponent.get());
      this.getCanvas().setAlign(Alignment.CENTER);
      this.getCanvas().setWidth100();
      this.getCanvas().setHeight100();
    }

    public MetadataInputComponent getMetadataCanvasSupplier() {
      return metadataInputComponent;
    }
  }
}
