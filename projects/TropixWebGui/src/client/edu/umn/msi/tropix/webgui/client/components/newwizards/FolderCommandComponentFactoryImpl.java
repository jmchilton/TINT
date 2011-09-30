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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions.DestinationType;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.constants.NewWizardConstants;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;
import edu.umn.msi.tropix.webgui.services.object.FolderService;

public class FolderCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private final MetadataInputComponentFactory metadataInputComponentFactory;

  @Inject
  public FolderCommandComponentFactoryImpl(final MetadataInputComponentFactory metadataInputComponentFactory) {
    this.metadataInputComponentFactory = metadataInputComponentFactory;
  }

  private AsyncCallback<Void> getCallback(final String parentFolderId) {
    return new AsyncCallbackImpl<Void>() {
      @Override
      public void onSuccess(final Void ignored) {
        LocationUpdateMediator.getInstance().onEvent(new UpdateEvent(parentFolderId, null));
      }
    };
  }
  
  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new WizardCommand(locations) {
      class MetadataWizardPageImpl extends WizardPageImpl<VLayout> {
        private MetadataInputComponent metadataCanvasSupplier;

        MetadataWizardPageImpl() {
          this.setTitle("Metadata");
          this.setDescription("Specify folder metadata.");

          final MetadataOptions options = new MetadataOptions("Folder");
          options.setInitialItems(locations);
          options.setIsValidListener(new Listener<Boolean>() {
            public void onEvent(final Boolean event) {
              MetadataWizardPageImpl.this.setValid(event);
            }
          });
          options.setDestinationType(DestinationType.FOLDER);
          metadataCanvasSupplier = metadataInputComponentFactory.get(options);

          // TODO: Replace label with TextBlock
          final Label folderType = new Label("");
          folderType.setWidth100();
          folderType.setHeight("25%");

          final VLayout layout = new VLayout();
          layout.addMember(this.metadataCanvasSupplier.get());
          final Listener<TreeItem> listener = new Listener<TreeItem>() {
            public void onEvent(final TreeItem treeItem) {
              if(treeItem == null) {
                return;
              }
              String description;
              if(TreeItems.isMySharedFoldersItem(treeItem)) {
                description = NewWizardConstants.INSTANCE.rootSharedFolderDescription();
              } else if(((TropixObjectTreeItem) treeItem).getObject() instanceof Folder) {
                description = NewWizardConstants.INSTANCE.folderDescription();
              } else {
                description = NewWizardConstants.INSTANCE.sharedFolderDescription();
              }
              folderType.setContents(description);
            }
          };
          this.metadataCanvasSupplier.addSelectionListener(listener);
          // folderType.setGroupTitle("Folder Type");
          layout.addMember(folderType);
          this.setCanvas(layout);
          this.getCanvas().setAlign(Alignment.CENTER);
          this.getCanvas().setWidth100();
          this.getCanvas().setHeight100();
        }
      }

      public void execute() {
        final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(1);
        final MetadataWizardPageImpl metadataWizardPage = new MetadataWizardPageImpl();
        pages.add(metadataWizardPage);
        final WizardOptions options = new WizardOptions();
        options.setTitle("New Folder");
        WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
          public void onCompletion(final Window wizard) {
            final MetadataInputComponent metadataSupplier = metadataWizardPage.metadataCanvasSupplier;
            final TreeItem parentObject = metadataSupplier.getParentObject();
            final String id = metadataSupplier.getDestinationId();
            if(parentObject instanceof TropixObjectTreeItem && ((TropixObjectTreeItem) parentObject).getObject() instanceof Folder) {
              final JobDescription jobDescription = new JobDescription("Create folder " + metadataSupplier.getName());
              final CreateFolderDescription description = new CreateFolderDescription();
              description.setJobDescription(jobDescription);
              ActivityDescriptions.initCommonMetadata(description, metadataSupplier);
              description.setCommitted(true);
              JobSubmitService.Util.getInstance().submit(Sets.<ActivityDescription>newHashSet(description), getCallback(id));
            } else {
              final VirtualFolder folder = new VirtualFolder();
              folder.setName(metadataSupplier.getName());
              folder.setDescription(metadataSupplier.getDescription());
              FolderService.Util.getInstance().createVirtualFolder(id.equals("-1") ? null : id, folder, getCallback(id));
            }
            wizard.destroy();
          }
        });
      }
    };
  }
}
